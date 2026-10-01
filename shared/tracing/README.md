# shared:tracing

Dueño único del contexto de traza: qué metadatos acompañan a cada línea de log, cómo se
generan los identificadores, cómo se propagan entre saltos y cuándo se limpian.

`shared:logger` decide **cómo** se loguea (`AppLogger`, appenders, formato). Este módulo
decide **con qué contexto** se loguea. Son responsabilidades distintas y por eso son módulos
distintos.

## Por qué existe

Antes la trazabilidad estaba repartida en tres módulos sin dueño: `shared:logger` guardaba
las constantes, `shared:web` generaba un único id, y `seguridad:infrastructure` emitía la
línea de auditoría. De ahí venían tres problemas concretos:

- **No se auditaban los 401, 403 ni 429.** El filtro de auditoría estaba anidado dentro de
  `FilterChainProxy` (orden -100), así que cuando la autenticación o la autorización cortaban
  la cadena, nunca se ejecutaba.
- **Dos formatos de id convivían**: UUID con guiones por HTTP, sin guiones por AMQP.
- **No se podía seguir una transacción completa**: había un solo identificador global, sin
  forma de distinguir un salto concreto dentro de ella.

## Qué expone

Un puerto primario que se inyecta por constructor. **Ningún módulo implementa contratos de
este módulo**: se le pide un alcance y se invocan métodos con nombre sobre él.

```java
try (var alcance = gestorTraza.abrir(SolicitudTraza.paraHttp(...))) {
    // aquí dentro, toda línea de log lleva el contexto
    alcance.registrarSalida(response.getStatus());
}   // close() restaura el contexto previo

gestorTraza.registrarUsuario(jwt.getSubject());   // enriquece el alcance abierto en este hilo
```

`AlcanceTraza` es `AutoCloseable` **y debe usarse con try-with-resources**: la limpieza la
garantiza el lenguaje, no la disciplina de quien escriba el próximo filtro. `close()` restaura
el mapa MDC capturado al abrir — no hace `remove`, para que un alcance anidado (un consumo
dentro de otro) devuelva el valor exterior en lugar de borrarlo.

`registrarUsuario` vive en `GestorTraza` y no en `AlcanceTraza` porque quien conoce al usuario
—un filtro dentro de la cadena de Spring Security, un consumidor AMQP— no es quien abrió el
alcance y no tiene su referencia.

**No hay un `registrarAtributo(clave, valor)` genérico**, y es deliberado: un `put` libre es la
puerta por la que entran tokens y datos personales al MDC. Añadir un campo cuesta una clave en
`TrazaKeys` y un método con nombre.

## Estructura

Es el único módulo `shared:*` con capas hexagonales internas. El objetivo es que el MDC sea un
detalle sustituible y que el dominio se pueda probar sin SLF4J ni Spring.

```
domain/traza/          TrazaDomain + model/ (Traceparent, IdentificadorTraza, ClienteIp,
                       CorrelacionEntrante, RutaUri, OrigenTraza, TrazaValores) — Java puro
application/traza/     primaryport/ (GestorTraza, AlcanceTraza) + secondaryport/
infrastructure/traza/  MdcContextoDiagnosticoOutputAdapter (única clase que toca org.slf4j.MDC),
                       TrazaKeys, TrazaHeaders, TrazabilidadConfig
```

Dependencias: `slf4j-api`, `shared:util` y `spring-context`. **No** depende de spring-web ni de
spring-security, para que `shared:amqp` pueda consumirlo sin arrastrar la capa web.

## Contrato MDC

| Clave | Quién la pone | Presencia |
|---|---|---|
| `correlacionId` | `TrazabilidadFilter` / `AbstractEventConsumer` | Todo el request o consumo |
| `transaccionId` | idem — **nuevo en cada salto** | Todo el request o consumo |
| `origen` | idem (`HTTP`, `EVENTO`, `PROGRAMADO`) | Todo el request o consumo |
| `usuarioId` | semilla por origen; `IdentidadTrazaFilter` la sobrescribe | Todo el request |
| `clienteIp`, `metodoHttp`, `rutaUri`, `tiempoEntrada` | `TrazabilidadFilter` | Todo el request |
| `codigoEstado`, `duracionMs`, `tiempoSalida` | `TrazabilidadFilter` al cerrar | Solo línea `AUDIT` |

`correlacionId` agrupa la transacción entera y **se reutiliza verbatim** si llega por
`X-Correlation-Id`: normalizarla rompería la correlación con quien nos llamó. Solo al generarla
se usa forma W3C (32 hex), que es lo que permite emitir un `traceparent` válido.
`transaccionId` (16 hex) se regenera en cada salto, así que el consumidor de un evento comparte
la correlación del productor pero tiene su propia transacción.

`duracionMs` se calcula con `System.nanoTime()` (monotónico); un ajuste de NTP con
`currentTimeMillis()` puede dar duraciones negativas. Los dos `Instant` sí usan reloj de pared,
porque su valor está en ser comparables con el timestamp de la línea de log.

`servicioNombre` y `version` **no** son claves MDC: son constantes del proceso y se añaden como
miembros JSON estáticos con `logging.structured.json.add.*`. El formateador logstash de Spring
Boot solo serializa el mapa MDC y los key-value del evento — nunca las propiedades de contexto
de Logback —, así que `<springProperty scope="context">` no las emitiría.

## Qué nunca entra al MDC

La cabecera `Authorization`, cualquier access/refresh token o `jti`, cookies, cuerpos de
petición o respuesta, y el **query string**: `rutaUri` sale siempre de `getRequestURI()`, que es
el path sin query. Los path variables sí se registran, así que no se ponen datos personales en
la ruta.

La IP del cliente es dato personal bajo GDPR / Ley 1581 y se registra bajo interés legítimo
(seguridad y detección de abuso de la API). `arquisoft.trazas.anonimizar-ip=true` la reduce a la
subred: último octeto a cero en IPv4, truncado a /48 en IPv6 (expandiendo antes la compresión
`::`, o la dirección sobreviviría entera).

## Configuración

```yaml
arquisoft:
  trazas:
    anonimizar-ip: false
    rutas-excluidas-auditoria: /api/actuator/,/api/swagger-ui,/api/v3/api-docs,/api/swagger-resources
```

## Notas de integración

- El módulo se declara explícitamente en el `build.gradle` raíz aunque llegue transitivamente:
  si el jar no está en el classpath de runtime, sus `@Component` no se registran y la
  aplicación arranca **sin trazas y sin ningún error**.
- Los slices `@WebMvcTest` que importan `GlobalAppExceptionHandler` deben importar también
  `TrazabilidadConfig.class`, porque el handler inyecta `GestorTraza`.
- `IdentidadTrazaFilter` se declara como `@Bean` más un `FilterRegistrationBean` deshabilitado.
  Todo bean de tipo `Filter` es auto-registrado en la cadena de servlet, y al añadirlo también
  con `addFilterAfter` el atributo de `OncePerRequestFilter` haría que la segunda pasada se
  saltara entera.
- Los listeners de RabbitMQ corren sobre hilos de plataforma **reutilizados entre mensajes**
  (`RabbitListenerConfig` define su propia factory sin `TaskExecutor`), así que ahí el
  capturar/restaurar del contexto es funcional, no defensivo.

Ver [docs/OBSERVABILIDAD_LOCAL.md](../../docs/OBSERVABILIDAD_LOCAL.md) para las consultas LogQL.
