# PLAN: ADR-013 — Redis como distribuidor del catálogo de mensajes

## Metadata

- **ID Historia:** HT-013
- **ADR de origen:** `ADR-013-catalogo-mensajes-redis.md` (arquisoft-docs, 2026-08-17, ✅ ACEPTADA)
- **Bounded Context:** transversal — `shared:message`, `shared:redis`, `shared:web`, raíz
- **Módulos Gradle afectados:** `shared:message`, `shared:redis`, `shared:web`, `fichas:infrastructure`, `build.gradle` (raíz), `src/main/**`, `docker-compose.yml`
- **Fecha de plan:** 2026-08-17 (v2 — 2026-08-18)
- **Rama sugerida:** `feature/HT-013-catalogo_mensajes_redis`
- **Base:** `323ae6d6` (`refactor/recomendaciones_arquitectura_hexagonal`)

### Historial de revisiones

| v | Cambio | Motivo |
|---|---|---|
| v1 | Plan inicial, alineado 1:1 con ADR-013 | — |
| **v2** | **Fail-fast de arranque** (§3.3), **monitor de reconexión** (§3.5), **registro `ClavesCatalogo`** (§3.6), **aridad declarada** (§3.7), mecánica del cargador (Fase 3), y la pregunta de `*Limits` dinámicos resuelta como **NO** (§2) | Decisión del equipo: sin Redis la aplicación no debe levantar, el catálogo debe estar completo o abortar, y un patrón con la aridad equivocada debe detectarse. Recogido en ADR-013 v1.1 |

## 1. Estado verificado del repositorio

Cifras medidas sobre `323ae6d6`, no estimadas:

| Hecho | Valor |
|---|---|
| Enums en `key/{contexto}/` | **32** (`app` 7, `fichas` 11, `notificaciones` 3, `seguridad` 10, `usuarios` 1) |
| Claves de negocio en `.properties` (app, fichas, seguridad, usuarios, notificaciones) | **221** |
| Claves de documentación en `fichas-api.properties` | **78** |
| Controladores que consumen `FichasApiKeys` | **13** (todos en `fichas:infrastructure`) |
| Tests que importan `CatalogoMensajesConfig` | **17** slices `@WebMvcTest` |
| Controladores con `@Operation` fuera de `fichas` | **2** (`AutenticacionCommandController`, `UsuarioCommandController`) — con texto **literal incrustado**, no `${clave}` |
| Tests que asientan **texto** de mensaje | **1** (`CambiarAsesorFichaControllerTest:113`) |

### 1.1 La restricción estructural que gobierna el diseño

```
shared:message  <--(api)--  shared:domain  <--  TODOS los dominios y aplicaciones
```

`shared/domain/build.gradle:10` declara `api project(':shared:message')`. Cualquier dependencia
que se añada a `shared:message` aterriza en el classpath de **la capa de dominio de los 9
contextos**. La regla «dominio sin Spring ni Jakarta» lo prohíbe, y el propio repositorio ya
documenta esa decisión: `CatalogoMensajesConfig` vive en `shared:web` y no en `shared:message`
precisamente por esto (ver su Javadoc).

**Consecuencia dura del plan: el adaptador Redis NO puede vivir en `shared:message`.**
Va en `shared:redis`, que ya tiene `spring-boot-starter-data-redis` y ya alcanza el puerto
`CatalogoMensajes` de forma transitiva vía `shared:domain`.

### 1.2 Qué habilita la degradación sin tocar los 32 enums

El esquema `contexto.capa.objeto.`**`tipo`**`.descripcion` ya contiene la categoría del mensaje en
el cuarto segmento (`error`, `log`, `validacion`, `api`). El nivel 3 puede deducir qué texto
genérico servir **parseando la clave**, sin añadir un método `categoria()` a `ClaveMensaje` — lo
que obligaría a editar 32 enums y ~299 constantes. Un test convierte el supuesto en invariante
verificada (§6.1).

---

## 2. Alcance

### En alcance

0. **Enmienda a ADR-013** — ya hecha (registro de cambios v1.1): el ADR recoge el fail-fast de
   arranque de §3.3, de modo que plan y registro están alineados y no hay desviación que declarar.
1. Nuevo `CatalogoMensajesRedis` en `shared:redis` con carga fail-fast al arranque, degradación en
   caliente y monitor de reconexión.
2. Nuevo juego de respaldo compilado (`MensajesRespaldo` + `CatalogoMensajesRespaldo`) en `shared:message`.
3. Eliminación de `CatalogoMensajesResourceBundle`, `PaquetesMensajes`, `ClaveMensaje.paquete()`
   y los 5 `.properties` de negocio.
4. `FichasApiKeys` → `FichasApiMessages` con los 78 textos incrustados; eliminación de
   `fichas-api.properties` y `ApiDocsMessagesConfig`.
5. Script de carga versionado del catálogo + servicio de carga en `docker-compose`.
6. Reemplazo de `CatalogoMensajesClavesTest` por la red de seguridad equivalente sobre el script
   de carga, más los tests nuevos del adaptador y del respaldo.

### Fuera de alcance (declarado, no omitido)

- **`SeguridadApiMessages` / `UsuariosApiMessages`.** La regla 6 del ADR exige que *toda*
  anotación `@Operation`/`@ApiResponse` referencie una constante de `{Contexto}ApiMessages`. Los 2
  controladores de `seguridad` y `usuarios` llevan hoy el texto literal en la anotación: **cumplen
  la prohibición de `${…}` pero no la ubicación**. Se planifica como **Fase 7 opcional** para no
  mezclar una migración de infraestructura con un refactor cosmético de dos archivos. Si se decide
  dejarlo fuera, queda como deuda explícita del ADR-013.
- **El componente de administración de mensajes en tiempo real.** ADR-013 prepara el sustrato; el
  componente es de otro equipo.
- **i18n.** El prefijo de locale queda habilitado por diseño, no implementado.
- **Rechazar peticiones con 503 mientras Redis esté caído.** Descartado con argumento, ver §3.4.

### Pregunta resuelta: límites (`*Limits`) como variable dinámica en Redis — **NO**

Planteada el 2026-08-18 y descartada con evidencia del propio repositorio. Se registra aquí para
que no vuelva a abrirse sin datos nuevos.

Contraste de los 4 límites de `FichasLimits` contra las migraciones Flyway:

| Límite | Valor | Columna real |
|---|---|---|
| `FichaPerfil.TITULO_MAX` | 100 | `titulo_proyecto VARCHAR(100)` (V1.0:13) |
| `ItemFichaPerfil.CONTENIDO_MAX` | 7000 | `contenido VARCHAR(7000)` (V1.3:26) |
| `EstadoEvaluacionFicha.ESTADO_MAX` | 50 | `id VARCHAR(50)` (V1.5:8) |
| `FichaPerfil.ESTUDIANTES_MAX` | 3 | *(sin constraint en BD — política pura)* |

**Tres de los cuatro no son parámetros: son el reflejo del ancho de una columna.** Subir
`TITULO_MAX` sin migración no relaja nada — produce un error de PostgreSQL al insertar, que sale
como 500 en vez del 422 legible que hoy da el validador. Bajarlo deja filas existentes violando la
regla vigente. En ambos casos el cambio exige **igualmente** una migración de BD, así que la
edición en caliente no compra nada y solo añade una vía de romper producción sin revisión de código.

Tres costos adicionales, ninguno hipotético:

1. **Arquitectura.** `FichasLimits.TITULO_MAX` se consume dentro de `{Command}.crear(...)`, una
   factoría estática sin punto de inyección. Leerlo de Redis obligaría a una segunda fachada
   estática global tipo `Mensajes`, esta vez para *reglas de negocio*. Es exactamente lo que la
   regla «el dominio no hace I/O» existe para impedir.
2. **Contrato de API.** El límite se publica en la documentación OpenAPI. Un número que cambia en
   runtime convierte el spec publicado en una afirmación falsa.
3. **Auditoría.** Un texto mal escrito es cosmético y reversible. Un límite mal escrito es un
   cambio de regla de negocio sin commit, sin revisión y sin rollback.

Es justo la línea que traza la regla 8 del ADR-013: a Redis va el *texto resuelto en runtime*;
`*Codes`, `*Limits` y `*Fields` siguen compilados porque son contrato y reglas.

**Único candidato legítimo detectado:** `ESTUDIANTES_MAX = 3`, que no tiene constraint en BD. Uno
solo no justifica un subsistema. Si en el futuro hacen falta parámetros operativos ajustables
(límites de rate limiting, TTL de sesión, intervalos de reintento, tamaño de lote de
notificaciones), el mecanismo correcto ya existe y es `application.yml` con variables de entorno —
no el catálogo de mensajes.

---

## 3. Diseño

### 3.1 Reparto por módulo

```
shared:message  (JDK puro — SIN cambios de dependencias)
├── CatalogoMensajes.java             <- puerto: firma INTACTA
├── ClaveMensaje.java                 <- se elimina paquete(); queda solo clave()
├── Mensajes.java                     <- cambia el valor por defecto estático
├── CategoriaMensaje.java             <- NUEVO enum: ERROR, LOG, VALIDACION, API
├── ContextosCatalogo.java            <- NUEVO (sustituye a PaquetesMensajes)
├── ClavesCatalogo.java               <- NUEVO: registro de los 32 enums, para el fail-fast
├── respaldo/
│   ├── MensajesRespaldo.java         <- NUEVO: constantes de texto genérico por categoría
│   └── CatalogoMensajesRespaldo.java <- NUEVO: implementación nivel 3, JDK puro
├── annotation/FichasApiMessages.java <- RENOMBRADO desde FichasApiKeys, textos incrustados
├── constant/**                       <- SIN CAMBIOS (Codes, Fields, Limits)
├── key/**                            <- 32 enums, solo se elimina el campo/método paquete
└── (resources/messages/*.properties  <- ELIMINADOS)

shared:redis
└── catalogo/
    ├── CatalogoMensajesRedis.java    <- NUEVO: niveles 1 y 2, delega el 3
    ├── MonitorCatalogoRedis.java     <- NUEVO: @Scheduled, reconexión y recarga
    ├── CatalogoMensajesHealthIndicator.java  <- NUEVO: estado en /actuator/health
    ├── exception/CatalogoMensajesIncompletoException.java  <- NUEVO: aborta el arranque
    └── config/CatalogoMensajesRedisConfig.java  <- NUEVO: @Bean @Primary + carga fail-fast

shared:web
└── config/CatalogoMensajesConfig.java  <- ELIMINADO (fachada en todas las capas, §3.9)

raíz
├── build.gradle                      <- + implementation project(':shared:redis')
├── src/main/java/.../ApiDocsMessagesConfig.java  <- ELIMINADO
└── catalogo/{contexto}.properties    <- NUEVO, FUERA de resources: script de carga versionado
```

### 3.2 Por qué `PaquetesMensajes` y `paquete()` desaparecen

Sin bundles no hay «bundle propietario». Dejar un `paquete()` que apunte a archivos borrados es
una mentira que el compilador no detecta. Su única función residual — saber qué prefijos existen
para el `SCAN` del warmup — la cubre `ContextosCatalogo.TODOS = {"app","fichas","seguridad",
"usuarios","notificaciones"}`, que es lo que el nombre dice que es.

Coste: edición mecánica de 32 enums (borrar un campo del constructor, un `final String`, un
getter). Es la parte más voluminosa del diff y la de menor riesgo.

### 3.3 Contrato de arranque: fail-fast y catálogo completo

> **Alineado con ADR-013.** El ADR fue enmendado (registro de cambios v1.1) para recoger el
> fail-fast de arranque: sin conexión a Redis, o con el catálogo incompleto, la aplicación no
> levanta. Este plan y el registro dicen ahora lo mismo; no queda desviación que justificar ante la
> validación arquitectónica.

**Al arrancar, `CatalogoMensajesRedisConfig`:**

1. Conecta a Redis. Si no hay conexión, **el contexto de Spring no levanta**
   (`CatalogoMensajesIncompletoException extends InfrastructureException`, en `shared:redis`;
   `shared:exception` llega vía `api` de `shared:domain`).
2. Hace `MGET` por lotes de **todas las claves declaradas** en `ClavesCatalogo.TODAS` y puebla la
   caché en memoria.
3. Si **una sola** clave declarada no resuelve, aborta el arranque nombrando las que faltan y su
   contexto. No arranca a medias.
4. **Valida la aridad de cada patrón**: cuenta los `%s` del texto cargado y los compara con el
   `parametros` que declara la constante (§3.7). Discrepancia = aborta, nombrando clave, esperados
   y encontrados.

**Por qué esto simplifica todo lo demás.** Tras un arranque exitoso, la caché cubre el **100 % de
lo que el código puede llegar a pedir** — las claves son constantes de compilación, no pueden
aparecer nuevas en runtime. Eso convierte la caché en autoritativa y hace que el juego de respaldo
compilado, que en el ADR era «una categoría de texto genérico por tipo de mensaje», se reduzca a
**un único texto de servicio no disponible** cuya rama es inalcanzable en la práctica.

**Precio, declarado sin adornos:** una clave faltante en Redis pasa de degradar un mensaje a
impedir el arranque de producción. La mitigación no es código, es proceso: la carga del catálogo
es una **compuerta del pipeline** (cargar → verificar conteo → desplegar), y `CatalogoCargaTest`
(§6.1) impide que una clave declarada llegue a producción sin línea en el script versionado.

### 3.4 Contrato en caliente: degradar sin caer, y reconectar

Una vez arriba, **ninguna caída de Redis tumba la aplicación ni rechaza peticiones**:

```java
// CatalogoMensajesRedis.obtener(clave)
// 1. StringRedisTemplate.opsForValue().get(clave.clave())
//      texto no nulo -> refresca la caché y lo retorna                    [NIVEL 1]
//      null          -> clave borrada de Redis en caliente: cae al nivel 2
// 2. catch (RuntimeException e)   // conexión, timeout, cualquier fallo del cliente
//      marcarDegradado(); cacheEnMemoria.get(clave.clave())              [NIVEL 2]
//      -> COMPLETA tras un arranque exitoso: devuelve el texto REAL, no uno genérico
// 3. si aun así no hay texto (inalcanzable tras arranque exitoso)
//      respaldo.obtener(clave) -> texto genérico de la CATEGORÍA          [NIVEL 3]
//      la categoría ERROR es el «servicio no disponible temporalmente»
```

Reglas que el código debe respetar y los tests asientan:

- **`obtener` nunca propaga excepción** una vez la aplicación está arriba. El fail-fast es
  exclusivo del arranque.
- **`obtener` nunca devuelve la clave** a un consumidor de API (regla 7 del ADR). Se elimina el
  marcador `??clave??`, que hoy sí se expondría en un `ErrorResponseDTO`.
- **`formatear` no revienta por aridad, pero sí la detecta.** Compara `args.length` con el
  `parametros` declarado (§3.7); si no coinciden, registra `error` con la clave y devuelve el
  **texto de respaldo de la categoría** — nunca el patrón crudo, que expondría `%s` al usuario.
  No lanza: `formatear` se ejecuta **dentro de `GlobalAppExceptionHandler`**, construyendo la
  respuesta de un error ya ocurrido; una excepción ahí convierte un 422 legible en un 500 sin
  cuerpo, y el cliente pierde el `errorCode`, que es lo único que su código consume.
- **`contiene` es `true` en niveles 1 y 2.** Solo el nivel 3 significa «no está en el catálogo».
- **Los logs sí pueden mostrar la clave** (destinatario técnico, lo dice el ADR).

**Lo que NO se hace, y por qué.** No se rechazan peticiones con 503 mientras Redis esté caído.
Con la caché completa la aplicación funciona íntegra; cortar todo el tráfico por la indisponibilidad
de un catálogo de textos cambiaría una degradación cosmética por una caída total. Lo que sí se
degrada de verdad en ese intervalo es el rate limiting de `seguridad`, que falla cerrado — y eso
ya ocurre hoy, con independencia de este plan.

### 3.5 Monitor de reconexión

`MonitorCatalogoRedis` (`shared:redis`), `@Scheduled(fixedDelayString =
"${arquisoft.catalogo.reintento-intervalo:PT30S}")`. Infraestructura ya disponible: `@EnableScheduling`
está activo (`seguridad/.../config/scheduling/SchedulingConfig`) y `FailedEventRetryConfig` es el
precedente exacto de este patrón.

- Solo actúa cuando el adaptador está en estado **degradado**; en estado sano no toca Redis.
- Al recuperar la conexión, **recarga el catálogo completo** (misma rutina del arranque, sin la
  parte que aborta) y vuelve a sano.
- Si tras la recarga faltan claves, **no vuelve a sano**: sigue degradado sirviendo desde caché y
  registra `error`. Una recarga parcial no puede pasar por recuperación.
- Registra la transición en ambos sentidos: `warn` al degradar, `info` al recuperar, con el número
  de claves recargadas.

**`CatalogoMensajesHealthIndicator`** expone el estado en `/actuator/health`, que la aplicación ya
publica con `show-details: always`. Es lo que hace visible la degradación en ADR-005 en lugar de
que quede solo en el log.

### 3.6 El registro de claves: `ClavesCatalogo.TODAS`

El fail-fast necesita enumerar las claves declaradas **en producción**, no solo en un test. Un
escaneo de classpath en runtime es frágil dentro de un `jar` y caro en arranque.

`ClavesCatalogo.TODAS` (`shared:message`) es una lista explícita de los 32 enums — una línea por
enum. El agujero clásico de una lista manual (alguien añade un enum y olvida registrarlo) lo cierra
un test: `CatalogoCargaTest` escanea el paquete `key/` sobre las clases compiladas y **falla el
build** si el registro no coincide con lo escaneado. La lista vive en producción, la garantía de
que está completa vive en el test.

Con esto, `ContextosCatalogo` deja de ser necesario para el warmup — su papel se limita a validar
prefijos en el test y a agrupar los archivos del script de carga.

### 3.7 Aridad declarada: `ClaveMensaje.parametros()`

Comportamiento actual, asimétrico y silencioso:

| Caso | Hoy |
|---|---|
| Faltan argumentos | `MissingFormatArgumentException` en runtime |
| **Sobran** argumentos | `String.formatted` los **ignora en silencio**. Nunca falla |

`CLAUDE.md` ya reconoce el hueco: «`CatalogoMensajesClavesTest` no comprueba que un patrón declare
tantos `%s` como le pasa su call site». Hoy lo tapan dos tests puntuales de texto renderizado
(`FiltroMensajesTest` en `shared:query`, `CampoSpecMensajesTest` en `shared:jpa`), que solo cubren
el subsistema de consulta. El catálogo tiene **93 `%s`** repartidos en patrones de 1 a 3 parámetros.

Cada constante declara su aridad junto a la clave:

```java
ERROR_TITULO_DUPLICADO("fichas.dominio.fichaperfil.error.titulo-duplicado", 1),
ERROR_VALOR_ENTRE("app.dominio.validador.error.valor-entre", 3),
```

Tres controles en tres momentos, que conviene no confundir:

| Momento | Qué detecta | Consecuencia |
|---|---|---|
| **Arranque** | El patrón en Redis no tiene tantos `%s` como declara el enum | **No levanta**, nombrando clave, esperados y encontrados |
| **Runtime** | El *call site* pasa una cantidad distinta a la declarada | `error` en log + texto de respaldo de la categoría |
| **Build** | — | Nada: el compilador no puede contar los argumentos de un varargs |

**El arranque es donde más vale.** El riesgo que este ADR *introduce* es que alguien edite un texto
en caliente y le añada o le quite un `%s`. Sin esta validación, el cambio pasa desapercibido hasta
que un usuario dispara ese error concreto; con ella, el despliegue siguiente no levanta y el fallo
aparece en el momento de la carga.

**Costo incremental casi nulo:** la Fase 4 ya abre los 32 enums para eliminar `paquete()`; el campo
`parametros` entra en el mismo pase. No sustituye a `FiltroMensajesTest` ni a `CampoSpecMensajesTest`:
la aridad vigila el conteo de marcadores, esos tests el contenido renderizado. Son complementarios
(ver la corrección en la Fase 4).

**Lo que NO da:** no convierte el error de call site en error de compilación. Conseguirlo exigiría
claves tipadas por aridad (`Clave1<A>`, `Clave2<A, B>`), y eso sí sería sobreingeniería para 32
enums y 299 constantes.

### 3.8 `StringRedisTemplate`, nunca `RedisClient`

`RedisClientImpl` serializa con `GenericJacksonJsonRedisSerializer` con *default typing*: escribe
el tipo como `@class` en el JSON. Un catálogo cargado por script (`SET clave "texto"`) contiene
cadenas planas que ese serializador **no puede deserializar**. El adaptador del catálogo usa
`StringRedisTemplate` directamente. Este punto es la trampa más fácil de pisar en toda la
migración y debe quedar comentado en el código.

### 3.9 Un solo camino al catálogo: la fachada estática, en todas las capas

> **Revisado tras implementar (2026-08-18).** El diseño original tenía dos beans de
> `CatalogoMensajes` —respaldo en `shared:web`, Redis en `shared:redis`— e inyección en aplicación e
> infraestructura, con la fachada estática reservada al dominio. Se descartó por evidencia.

| Config | Módulo | Bean | `Mensajes.instalar` |
|---|---|---|---|
| ~~`CatalogoMensajesConfig`~~ | ~~shared:web~~ | **eliminada** | — |
| `CatalogoMensajesRedisConfig` | `shared:redis` | `CatalogoMensajesRedis` | **sí llama** |

**Por qué se eliminó la inyección.** Tres datos del repositorio:

- 53 puntos de producción recibían `CatalogoMensajes` por constructor.
- **Cero tests lo sustituían por un doble.** Los 33 que "inyectaban" le pasaban el catálogo real.
- La convención ya estaba rota: `seguridad:infrastructure`, `usuarios:infrastructure` y el propio
  `shared:web` ya resolvían por la fachada estática en producción.

Y tener dos vías produjo un fallo real, no hipotético: en `CambiarAsesorFichaControllerTest` una
misma respuesta HTTP mezclaba el texto **real** en `fieldErrors[0].message` (que sale del dominio,
por fachada) con el **genérico del respaldo** en `message` (que salía del bean). Un catálogo de
textos es una tabla de consulta inmutable y de proceso — más pariente de `Math` que de un
`OutputPort`; que sea una sola vía es lo que garantiza que las tres capas resuelvan contra la misma
instancia.

**Esto no cierra la puerta al i18n, y conviene dejarlo dicho porque fue la objeción que se evaluó.**
El idioma llega por petición, no por componente, así que un bean inyectado —siendo singleton— sería
igual de ciego al locale que la fachada. De las tres vías posibles (locale como parámetro, locale
ambiental, o bean request-scoped), **la única que sirve para el dominio es la ambiental**: una
excepción construida con `new` no recibe inyección de ningún tipo. Y esa vía es indiferente entre
fachada y bean, porque quien lee el locale es la implementación, no el llamante — los puntos de
llamada no cambian. `shared:tracing` ya es el precedente de estado ambiental por petición en este
proyecto, con aislamiento probado bajo virtual threads.

**Consecuencia en los tests:** los 17 slices `@WebMvcTest` dejan de importar
`CatalogoMensajesConfig`. Resuelven por la fachada, que en tests tiene el catálogo de prueba
instalado por `InstaladorCatalogoPrueba` — así que ahora afirman sobre el **texto real**, sin Redis
(regla 9 del ADR) y sin quedar sujetos al fail-fast, que vive en la config de `shared:redis` y ellos
no cargan.

### 3.10 Textos OpenAPI

`FichasApiKeys` → `FichasApiMessages`. Las **constantes conservan su nombre**
(`FichaPerfil.REGISTRAR_SUMMARY`), solo cambia el valor: de `"${clave}"` al texto literal tomado de
`fichas-api.properties`. En los 13 controladores el diff es el import y el nombre de la clase —
nada más. Las constantes `ABRE`/`CIERRA` desaparecen.

Se eliminan `ApiDocsMessagesConfig` y `fichas-api.properties`.

---

## 4. Fases

Cada fase deja el build verde. El orden está elegido para que ninguna fase dependa de Redis
levantado salvo la validación manual final.

### Fase 1 — Respaldo compilado y categorías (`shared:message`)

1. `CategoriaMensaje` — enum `ERROR`, `VALIDACION`, `LOG`, `API`, con `desde(String clave)` que
   parsea el cuarto segmento y `POR_DEFECTO = ERROR` para lo no reconocido.
2. `MensajesRespaldo` — constantes de texto genérico, una por categoría. Redacción propuesta:
   - `ERROR`: `"El servicio no está disponible en este momento. Intente nuevamente más tarde."`
     — es el «servicio caído» que pide el requisito; con el fail-fast de §3.3 su rama es
     inalcanzable tras un arranque exitoso, así que se implementa como último recurso de una línea,
     no como funcionalidad a desarrollar.
   - `VALIDACION`: `"Error de validación en los datos enviados"` ← **texto exacto** que hoy
     asienta `CambiarAsesorFichaControllerTest:113`; conservarlo evita romper ese test (§7, R3).
   - `API`: `"Documentación no disponible"` (no se usa en runtime; existe por completitud).
   - `LOG`: devuelve la clave.
3. `CatalogoMensajesRespaldo implements CatalogoMensajes` — JDK puro, sin estado, `porDefecto()`.
   `contiene(...)` devuelve **`false` siempre**: el respaldo no es el catálogo.
4. `ClavesCatalogo.TODAS` — registro explícito de los 32 enums (§3.6).
5. `ContextosCatalogo` — sustituye a `PaquetesMensajes` en su único papel que sobrevive: agrupar
   los archivos del catálogo versionado (Fase 3) y validar prefijos en los tests.
6. ~~`Mensajes`: cambia el default estático~~ — **movido a la Fase 4**, ver la nota de abajo.

> **Hallazgo de implementación (2026-08-18): el cambio de default no es un cambio aislado.**
> Los tests unitarios no levantan contexto de Spring, así que resuelven las claves por la fachada
> estática `Mensajes` y hoy **afirman sobre el texto real del catálogo**. Cambiar el default al
> respaldo genérico deja **53 tests en rojo en 6 módulos** (`fichas:domain` 23, `shared:query` 11,
> `shared:validation` 7, `shared:jpa` 5, `usuarios:domain` 5, `usuarios:application` 2).
>
> No es un fallo del respaldo: es correcto que devuelva un texto genérico. Lo que revela es que la
> suite depende del `ResourceBundle` como fixture implícito, y que sustituirlo exige darle a esos
> módulos un catálogo de test explícito — trabajo transversal que pertenece a la Fase 4 (donde el
> `ResourceBundle` desaparece y el relevo deja de ser opcional) junto con la Fase 6.
>
> Las 8 afirmaciones de `fichas:application` que fallaban por esto **sí** se corrigieron aquí, pero
> no instalando un catálogo: afirmaban el nombre del campo a través del texto renderizado, cuando
> `ValidationResult.tieneErroresDeCampo(...)` lo dice de forma estructural y sin depender del
> catálogo. Es la afirmación que esos tests siempre quisieron hacer, y ya no se romperá en la Fase 4.

**Verde sin tocar nada más**: las clases nuevas son aditivas y el default sigue en el `ResourceBundle`,
así que en este punto conviven las dos implementaciones sin que ninguna suite cambie de resultado.

### Fase 2 — Adaptador Redis (`shared:redis`)

1. `build.gradle`: añadir `implementation project(':shared:message')` explícito (hoy llega
   transitivo por `shared:domain`; dejarlo implícito oculta la dependencia real).
2. `CatalogoMensajesRedis` — constructor `(StringRedisTemplate, CatalogoMensajes respaldo,
   AppLogger)`. Implementa §3.4. La caché es un `ConcurrentHashMap<String, String>`; el estado
   sano/degradado, un `AtomicBoolean` que el monitor consulta y actualiza.
3. `CatalogoMensajesIncompletoException extends InfrastructureException` — mensaje que nombra las
   claves faltantes agrupadas por contexto. `shared:exception` llega vía `api` de `shared:domain`.
4. `CatalogoMensajesRedisConfig` — `@Bean @Primary`, carga fail-fast por `MGET` sobre
   `ClavesCatalogo.TODAS` (§3.3), `Mensajes.instalar`.
5. `MonitorCatalogoRedis` + `CatalogoMensajesHealthIndicator` (§3.5). Propiedad nueva
   `arquisoft.catalogo.reintento-intervalo` en `application.yml`, por defecto `PT30S`.
6. `build.gradle` raíz: `implementation project(':shared:redis')`. Hoy solo llega por
   `seguridad:infrastructure` con scope `implementation` — funciona en runtime por component scan,
   pero es una dependencia accidental para algo que ahora es nuclear.

7. `shared:web/CatalogoMensajesConfig`: **eliminada por completo**. Queda una sola configuración que
   instala en la fachada —la de `shared:redis`— así que desaparece el problema de orden de creación
   que este paso originalmente venía a mitigar. Ver §3.9 para la evidencia que llevó a quitar la
   inyección entera.

**Aridad diferida.** El paso 4 de §3.3 (validar los `%s` del patrón cargado contra el
`parametros` declarado) **no entra en esta fase**: `ClaveMensaje.parametros()` lo añade la Fase 4.
La estructura ya lo admite —`recargar()` devuelve un `ResultadoCarga` en lugar de lanzar, y quien
decide es el llamador— así que la comprobación se acopla sin rediseñar nada. Lo que sí quedó
cubierto en caliente es el efecto: `formatear` captura `IllegalFormatException` y degrada al
respaldo con `error` en el log, en vez de propagar el fallo desde dentro de
`GlobalAppExceptionHandler`. La aridad declarada convierte eso de detección tardía en fail-fast.

### Fase 3 — Script de carga versionado

**Nota de alcance:** el Redis de este proyecto vive en la nube; `docker-compose.yml` levanta un
Redis **local** aparte, usado solo por quien desarrolla con `docker-compose up`. Ambos caminos son
independientes hoy — `SPRING_REDIS_HOST` del backend en compose apunta al contenedor `redis`, no a
la nube, y `.env.example` ya trata `REDIS_HOST`/`REDIS_PORT` como variables configurables. El
script de carga no debe asumir ningún host: toma esas mismas variables, así que un mismo script
sirve para cargar el catálogo en la nube (ejecutándolo suelto, fuera de Docker) y para el
contenedor local (paso 3 abajo). El cambio en `docker-compose.yml` es necesario **para quien sí
use ese flujo local** — con el fail-fast de §3.3, un compose sin el catálogo cargado deja el
backend sin poder arrancar nunca — pero no afecta a quien trabaja contra la nube sin levantar Docker.

1. `catalogo/{app,fichas,seguridad,usuarios,notificaciones}.properties` — **fuera de
   `src/main/resources`**, en la raíz del repositorio. Contenido: los 221 pares actuales,
   trasladados literalmente (`git mv` + edición del encabezado).
2. `catalogo/cargar.sh` — recorre los archivos y emite un `SET` por par contra
   `$REDIS_HOST:$REDIS_PORT` (mismas variables que `RedisConfig`, sin valor por defecto hardcodeado
   a `localhost`). Cinco detalles que importan y son fáciles de errar:
   - Partir cada línea **por el primer `=`**: los textos contienen `=` (p. ej. `id={}`).
   - Pasar el valor por *stdin* (`redis-cli -x SET clave`), nunca como argumento — evita el
     desastre de comillas, espacios y `%s`.
   - UTF-8 explícito, o las tildes llegan corruptas (mismo fallo que ya obligó a `encoding="UTF-8"`
     en `ApiDocsMessagesConfig`).
   - **Nunca `FLUSHDB`**: el catálogo comparte instancia con los buckets de rate limit y los tokens
     invalidados. El script es idempotente y re-ejecutable.
   - Al terminar, **cuenta las claves cargadas por prefijo y las verifica**. Ese conteo es la
     compuerta que el fail-fast de §3.3 necesita: sin él, un despliegue con carga parcial no
     levanta y el diagnóstico llega tarde.
3. `docker-compose.yml` — servicio `catalogo-loader` (imagen `redis:7-alpine`,
   `depends_on: redis: condition: service_healthy`, `restart: "no"`) que ejecuta el script y
   termina. **El backend pasa a `depends_on: catalogo-loader: condition:
   service_completed_successfully`** — sin esto, con fail-fast, arrancaría antes que el catálogo y
   moriría.
4. **Pipeline de despliegue (ADR-CICD):** paso previo al despliegue, no un *init container* en
   paralelo. Orden obligatorio: cargar → verificar conteo → desplegar la aplicación.
5. `docs/EJECUCION_LOCAL.md` — el paso de carga pasa a ser parte del arranque local, con la
   advertencia de que un `FLUSHDB` manual exige recargar antes de reiniciar el backend.

**Traslado de los archivos diferido a la Fase 4, y por qué.** El paso 1 pedía `git mv` de los 5
`.properties` a `catalogo/` en la raíz. Hacerlo ahora los saca del classpath y deja al
`ResourceBundle` —que sigue siendo el catálogo activo hasta la Fase 4— sin nada que resolver: los
mismos 53 tests del hallazgo de la Fase 1, en rojo otra vez. Duplicarlos en las dos ubicaciones
sería peor: 221 pares con dos copias que pueden divergir.

Así que la fuente de verdad sigue siendo una sola, en su sitio actual, y el script la toma de
`CATALOGO_DIR`, cuyo valor por defecto apunta ahí. La Fase 4 hace el `git mv` y cambia ese valor
por defecto y el montaje del `catalogo-loader` — dos líneas, ninguna duplicación de datos.

**Detalles que la implementación confirmó como reales, no hipotéticos:**

- Los `.properties` del repositorio tienen finales de línea **mezclados** (`app`, `seguridad`,
  `usuarios` con CRLF; `fichas`, `notificaciones` con LF). Un `\r` arrastrado al valor se escribe en
  Redis y sale en la respuesta al cliente sin que nada lo delate. El script lo recorta línea a línea.
- La verificación final usa `EXISTS` sobre las claves recién escritas y **no** un `SCAN` por
  prefijo: el `SCAN` recorrería también los buckets de rate limit y los tokens invalidados que viven
  en la misma instancia, y contaría de más.
- Verificado con un `redis-cli` simulado: **221 claves** cargadas y verificadas (app 92, fichas 47,
  seguridad 66, usuarios 7, notificaciones 9), con los tres casos frágiles intactos — un valor con
  `=` dentro (`id={}`), la raya UTF-8 (`—`) y los marcadores `%s`/`%d`.

### Fase 4 — Eliminación de `ResourceBundle`

1. Borrar `CatalogoMensajesResourceBundle` y `PaquetesMensajes`, y hacer el `git mv` de los 5
   `.properties` de negocio a `catalogo/` en la raíz (diferido desde la Fase 3). Con el mismo
   commit: el valor por defecto de `CATALOGO_DIR` en `catalogo/cargar.sh` y el montaje del
   servicio `catalogo-loader` en `docker-compose.yml` pasan a `./catalogo`.
2. `Mensajes`: relevo del default estático por `CatalogoMensajesRespaldo` y mismo cambio en
   `instalar(null)`. Movido desde la Fase 1 — ver el hallazgo allí.
3. **Catálogo de test para los módulos sin contexto de Spring.** Con el `ResourceBundle` borrado,
   los 53 tests que afirman texto renderizado (`fichas:domain`, `shared:query`, `shared:validation`,
   `shared:jpa`, `usuarios:domain`, `usuarios:application`) resuelven contra el respaldo genérico y
   quedan en rojo. Cada uno se arregla por una de dos vías, en este orden de preferencia:
   a) afirmar de forma estructural lo que el test realmente quiere decir — el patrón que ya se
      aplicó a las 8 afirmaciones de `fichas:application` con `tieneErroresDeCampo(...)`;
   b) instalar un catálogo de test que lea los `catalogo/*.properties` versionados de la Fase 3,
      cuando el texto renderizado sea de verdad lo que se está probando.
4. `ClaveMensaje`: eliminar `paquete()`, añadir `parametros()` (§3.7).
5. **Los 32 enums de `key/`**: en un solo pase, eliminar el campo `paquete` y añadir `parametros`
   con el número de `%s` de su patrón. Mecánico; verificar con
   `./gradlew :shared:message:compileJava` tras cada contexto.
6. ~~Borrar `FiltroMensajesTest` (`shared:query`) y `CampoSpecMensajesTest` (`shared:jpa`)~~ — **descartado**, ver la corrección abajo.

> **Corrección (2026-08-18): la aridad no subsume a esos dos tests, así que se conservan.** El plan
> daba por hecho que la validación general de aridad los hacía redundantes. No es cierto y se
> comprobó al borrarlos: la aridad cuenta **cuántos** marcadores tiene un patrón, no que el texto
> renderizado sea el correcto ni que nombre el campo u operador infractor, que es exactamente lo que
> esos 17 tests afirman. Borrarlos dejó `shared:query` con solo sus tests de DTO y `shared:jpa` sin
> ningún test en `query/`: una pérdida de cobertura real, no una limpieza. Restaurados, pasan sin
> cambios contra el catálogo de prueba. Los dos controles son complementarios: uno vigila el conteo,
> los otros el contenido.

**Cómo se resolvió el punto 3 (los 53 tests), y dos hallazgos de la implementación:**

Se aplicó la vía (b) para todos, y no la (a), porque al mirarlos de cerca la mayoría no depende del
texto *por accidente*: comprueban que el nombre del campo, el UUID o el operador infractor **llegan
al mensaje**, y eso es comportamiento real que merece verificarse. Un catálogo de prueba lo sostiene;
una afirmación estructural lo perdería. Piezas:

- `shared:message` gana el plugin `java-test-fixtures` y publica `CatalogoMensajesPrueba`, que lee
  los `catalogo/*.properties` **copiados por Gradle** desde la raíz (`processTestFixturesResources`).
  No hay segunda copia de los datos: el mismo archivo que sube a Redis es el que leen los tests.
- `InstaladorCatalogoPrueba` es un `LauncherSessionListener` del JUnit Platform registrado por
  `ServiceLoader`, no una extensión de Jupiter. Así se descubre solo, **sin anotar 53 clases de test**
  y sin activar `junit.jupiter.extensions.autodetection.enabled`, que registraría también las
  extensiones que traen otras librerías del classpath de test.
- La dependencia se declara **una vez** en el `subprojects` del `build.gradle` raíz, no módulo a
  módulo: la necesita cualquier test que afirme sobre un mensaje, incluidos los que lo resuelven por
  la fachada estática del dominio sin saberlo.
- Los ~28 tests que instanciaban `CatalogoMensajesResourceBundle.porDefecto()` pasan a
  `CatalogoMensajesPrueba.porDefecto()` — un renombrado mecánico.

1. **`java-test-fixtures` mete el jar del propio módulo en el classpath de test.** El escaneo de
   `key/` de `CatalogoMensajesClavesTest` resolvía el paquete a una URL `jar:` y reventaba con
   `FileSystemNotFoundException`. Se corrigió escaneando el **árbol de fuentes**, que además es más
   correcto: leer el jar significa leer un artefacto que puede estar obsoleto, y un enum recién
   añadido no aparecería — el test pasaría en falso justo en el caso que existe para detectar.
2. **El bean inyectado y la fachada estática resolvían catálogos distintos en los slices.** Es el
   riesgo R3, pero el fallo real era peor que un texto esperado: en `CambiarAsesorFichaControllerTest`
   una misma respuesta HTTP mezclaba el texto **real** en `fieldErrors[0].message` (que sale del
   dominio, vía fachada) con el **genérico del respaldo** en `message` (que sale del bean). Se
   corrigió en el diseño, no en el test. El arreglo inicial fue un `CatalogoMensajesDelegado` que
   delegaba en la fachada, pero eso dejó el bean como una fachada disfrazada — con la ceremonia del
   DI y ninguno de sus beneficios. La resolución definitiva fue eliminar la inyección: **fachada en
   todas las capas**, ver §3.9.

`CatalogoMensajesClavesTest` se sustituye por `CatalogoCargaTest` (§6.1) en esta fase, no en la 6:
tenía que compilar de todos modos al desaparecer `paquete()` y los bundles.

**Resultado verificado:** `./gradlew build` en verde, **617 tests, 0 fallos**. La única tarea que
falla es `:fichas:infrastructure:checkstyleMain`, por dos imports sin usar en
`FichaPerfilQueryOutputAdapter.java` — un cambio del working tree previo a esta HT, ajeno al plan.

### Fase 5 — OpenAPI a constantes

1. `FichasApiKeys` → `FichasApiMessages` con los 78 textos incrustados.
2. Actualizar los 13 controladores (import + nombre de clase).
3. Borrar `fichas-api.properties` y `ApiDocsMessagesConfig`.
4. `CLAUDE.md`: actualizar la línea de convención que hoy dice
   «Swagger texts from `FichasApiKeys` (`shared:message`)».

### Fase 6 — Tests (detalle en §6)

### Fase 7 — `SeguridadApiMessages` / `UsuariosApiMessages`

1. Crear ambas clases en `shared:message/annotation/`, con los textos que hoy están incrustados en
   las anotaciones de los 2 controladores.
2. Sustituir los literales por las constantes.
3. `CLAUDE.md`: generalizar la convención a `{Contexto}ApiMessages`.

> **Hecha (2026-08-19).** El diff es una reubicación pura: los textos ya cumplían la prohibición de
> `${…}`, lo que les faltaba era la ubicación. No se tocó ni una palabra del contenido — incluidas
> las tildes que faltan en `seguridad`/`usuarios` («Autenticacion», «contrasena», «invalidos»),
> que son un defecto real pero de otro cambio: corregirlas aquí mezclaría una reubicación mecánica
> con una edición del texto visible en Swagger.
>
> Queda fuera `OpenApiConfig` (raíz), cuyos textos de `@OpenAPIDefinition`/`@SecurityScheme` son
> metadatos globales de la especificación, no de un contexto, y solo los lee esa clase: sacarlos a
> `shared:message` ampliaría su alcance sin que nadie más los use.

---

## 5. Inventario de archivos

### Nuevos (6 clases + 5 datos + 1 script)

| Archivo | Módulo |
|---|---|
| `message/CategoriaMensaje.java` | shared:message |
| `message/ContextosCatalogo.java` | shared:message |
| `message/ClavesCatalogo.java` | shared:message |
| `message/respaldo/MensajesRespaldo.java` | shared:message |
| `message/respaldo/CatalogoMensajesRespaldo.java` | shared:message |
| `redis/catalogo/CatalogoMensajesRedis.java` | shared:redis |
| `redis/catalogo/MonitorCatalogoRedis.java` | shared:redis |
| `redis/catalogo/CatalogoMensajesHealthIndicator.java` | shared:redis |
| `redis/catalogo/exception/CatalogoMensajesIncompletoException.java` | shared:redis |
| `redis/catalogo/config/CatalogoMensajesRedisConfig.java` | shared:redis |
| `catalogo/*.properties` (5) | raíz |
| `catalogo/cargar.sh` | raíz |

### Modificados

| Archivo | Cambio |
|---|---|
| `ClaveMensaje.java` | menos `paquete()`, más `parametros()` |
| `Mensajes.java` | default estático |
| `key/**/*.java` (32) | menos el campo `paquete`, más el campo `parametros` |
| `annotation/FichasApiKeys.java` | pasa a `FichasApiMessages`, textos incrustados |
| `shared/web/.../CatalogoMensajesConfig.java` | **eliminada** — fachada en todas las capas (§3.9) |
| 53 puntos de inyección de `CatalogoMensajes` | pasan a `Mensajes.obtener`/`formatear` |
| 17 slices `@WebMvcTest` | pierden el `@Import` de `CatalogoMensajesConfig` |
| `shared/redis/build.gradle` | más `shared:message` |
| `build.gradle` (raíz) | más `shared:redis` |
| 13 controladores de `fichas` | import y nombre de clase |
| `docker-compose.yml`, `docs/EJECUCION_LOCAL.md`, `CLAUDE.md` | ver fases 3 y 5 |

### Eliminados

`CatalogoMensajesResourceBundle.java`, `PaquetesMensajes.java`, `ApiDocsMessagesConfig.java`,
`messages/{app,fichas,seguridad,usuarios,notificaciones,fichas-api}.properties`,
`CatalogoMensajesClavesTest.java` (sustituido por `CatalogoCargaTest`).

---

## 6. Estrategia de pruebas

**Ninguna prueba requiere una instancia de Redis.** El adaptador se prueba con
`StringRedisTemplate` mockeado.

### 6.1 `CatalogoCargaTest` (reemplaza a `CatalogoMensajesClavesTest`)

Al borrar los `.properties` desaparece la red que garantizaba «toda clave declarada tiene texto».
Se reconstruye **contra el script de carga**, que pasa a ser la fuente versionada:

| Prueba | Qué asienta |
|---|---|
| `debeEncontrarLosEnumsDeClaves_cuandoSeEscaneaElPaquete` | Se conserva tal cual — sin ella el resto es tautológico |
| `debeExistirEnElScriptDeCarga_cuandoSeRecorrenLosEnums` | Cada `ClaveMensaje` declarada tiene línea en `catalogo/*.properties` |
| `debeNoQuedarHuerfana_cuandoSeRecorreElScript` | Ninguna línea del script carece de constante Java |
| `debeNoRepetirseLaClave_cuandoSeComparanLosArchivos` | Una clave en dos archivos se sobrescribiría en Redis según el orden de carga |
| `debeTenerCategoriaReconocida_cuandoSeParseaLaClave` | **Nuevo e indispensable**: valida el supuesto de §1.2 — todo `tipo` es un `CategoriaMensaje` |
| `debeCoincidirElPrefijoConElContexto_cuandoSeRecorreElScript` | El primer segmento está en `ContextosCatalogo.TODOS` |
| `debeRegistrarTodosLosEnums_cuandoSeComparaConElEscaneo` | **Nuevo y crítico**: `ClavesCatalogo.TODAS` coincide con el escaneo del paquete `key/`. Cierra el agujero de la lista manual que el fail-fast necesita (§3.6) |
| `debeCoincidirLaAridad_cuandoSeCuentanLosMarcadores` | **Nuevo**: los `%s` de cada línea del script igualan el `parametros()` de su constante. Cierra el hueco que `CLAUDE.md` documenta (§3.7); complementa a `FiltroMensajesTest`/`CampoSpecMensajesTest`, no los sustituye |

Los archivos se leen del *working directory* del task `Test` de Gradle (el directorio del módulo),
vía ruta relativa a la raíz del repositorio.

### 6.2 `CatalogoMensajesRedisTest` (unitario, Mockito)

| Prueba |
|---|
| `debeDevolverElTextoDeRedis_cuandoLaClaveExiste` |
| `debePoblarLaCache_cuandoRedisResuelveLaClave` |
| `debeDevolverElTextoDeLaCache_cuandoRedisFalla` |
| `debeDevolverElRespaldo_cuandoRedisFallaYLaCacheEstaVacia` |
| `debeDevolverElRespaldo_cuandoLaClaveNoExisteEnRedis` |
| `debeNoPropagarExcepcion_cuandoRedisLanza` |
| `debeNoDevolverLaClave_cuandoDegradaANivelDeRespaldo` (regla 7 del ADR) |
| `debeDevolverElRespaldo_cuandoFaltanArgumentos` |
| `debeDevolverElRespaldo_cuandoSobranArgumentos` (hoy pasa en silencio) |
| `debeNoExponerElPatronCrudo_cuandoLaAridadNoCoincide` (nada de `%s` al usuario) |
| `debeRegistrarError_cuandoLaAridadNoCoincide` |
| `debeDevolverFalso_cuandoElTextoVieneDelRespaldo` (`contiene`) |
| `debeConservarMarcadoresSlf4j_cuandoElPatronEsDeLog` |

### 6.3 `CatalogoMensajesRespaldoTest`

Toda `CategoriaMensaje` tiene texto no vacío; `LOG` devuelve la clave; `contiene` es `false`.

### 6.4 `CatalogoMensajesRedisConfigTest` (arranque, `StringRedisTemplate` mockeado)

| Prueba |
|---|
| `debeCargarTodasLasClaves_cuandoRedisRespondeCompleto` |
| `debeAbortarElArranque_cuandoFaltaUnaClave` |
| `debeNombrarLasClavesFaltantes_cuandoAborta` (diagnóstico accionable, no un fallo mudo) |
| `debeAbortarElArranque_cuandoNoHayConexion` |
| `debeAbortarElArranque_cuandoElPatronNoTieneLaAridadDeclarada` |

### 6.5 `MonitorCatalogoRedisTest`

| Prueba |
|---|
| `debeNoConsultarRedis_cuandoElEstadoEsSano` |
| `debeRecargarElCatalogo_cuandoLaConexionVuelve` |
| `debeSeguirDegradado_cuandoLaRecargaQuedaIncompleta` |
| `debeNoPropagarExcepcion_cuandoElPingFalla` (un `@Scheduled` que lanza deja de reprogramarse) |

### 6.6 Regresión

`./gradlew build` completo. Atención especial a los 17 slices `@WebMvcTest`, que ahora resuelven
por respaldo (§7, R3), y a `jacocoTestReport` (mínimo 75 %) sobre `shared:redis`, que hoy tiene
poca cobertura y recibe una clase con bastante lógica de ramas.


> **Revisado al implementar (2026-08-19).** Tres desviaciones respecto a lo planificado, todas por
> lo mismo: la Fase 6 se escribió sobre supuestos que el código no cumplía.
>
> 1. **`debeTenerCategoriaReconocida_cuandoSeParseaLaClave` no era escribible tal cual.** El supuesto
>    de §1.2 —todo `tipo` de una clave es un `CategoriaMensaje`— es falso: no hay ninguna clave con
>    `validacion` ni `api` como cuarto segmento, y hay **diez** que usan segmentos propios
>    (`app.infraestructura.consulta.tipo.*`, `mensajeria.valor.*`,
>    `notificaciones.aplicacion.plantilla.{asunto,cuerpo}.*`). No son un error: nombran etiquetas de
>    tipo y plantillas de correo, no categorías de mensaje. Lo que sí es un riesgo es que
>    `CategoriaMensaje.desde` degrada a `ERROR` en silencio, así que un `erorr` mal escrito daría el
>    respaldo equivocado sin que nada avisara. La prueba escrita —`debeTenerSegmentoAceptado_…`—
>    **fija la lista cerrada de segmentos aceptados**, que es la red que el plan buscaba: un segmento
>    nuevo obliga a decidir si merece su propia categoría o si entra en la lista.
>
>    Efecto colateral que conviene registrar: `MensajesRespaldo.VALIDACION` y `MensajesRespaldo.API`
>    son hoy **inalcanzables** por `para(...)`, al no existir claves de esas categorías.
>
> 2. **El fail-fast de arranque no comprobaba la aridad**, aunque §3.7 y `CLAUDE.md` decían que sí.
>    Se implementó al escribir `debeAbortarElArranque_cuandoElPatronNoTieneLaAridadDeclarada`:
>    `ResultadoCarga` gana `desajustes()`/`esConsistente()` y
>    `CatalogoMensajesIncompletoException.porAridadInconsistente(...)`. La separación entre *falta la
>    clave* y *la clave miente sobre sus parámetros* no es cosmética: el arranque aborta ante
>    cualquiera de las dos, pero el monitor en caliente solo exige la primera — a esas alturas
>    abortar no es una opción, y un desajuste ya degrada de forma controlada en el adaptador.
>
> 3. **La preocupación de §6.6 por `jacocoTestReport` sobre `shared:redis` no aplica.** JaCoCo se
>    aplica solo a los contextos de negocio; `shared:*` está excluido a propósito en el
>    `build.gradle` raíz. La cobertura del adaptador la asientan sus 21 pruebas, no un umbral.
---

## 7. Riesgos

| # | Riesgo | Mitigación |
|---|---|---|
| **R1** | **Dos políticas opuestas sobre el mismo Redis.** El rate limiting de `seguridad` *falla cerrado* ante error de Redis (bucket agotado); el catálogo debe *fallar abierto* (degradar). Un `try/catch` copiado de un lado al otro invierte la semántica. | Comentario explícito en `CatalogoMensajesRedis`; el test `debeNoPropagarExcepcion_cuandoRedisLanza` lo fija |
| **R2** | **Serializador incompatible.** Usar `RedisClient` o `RedisTemplate<String, Object>` haría ilegible todo lo que cargue el script. | `StringRedisTemplate` más comentario; §3.8 |
| **R3** | ~~**Los 17 `@WebMvcTest` pasan a resolver por respaldo.**~~ **No se materializó.** Los slices resuelven por el catálogo de prueba, que `InstaladorCatalogoPrueba` instala en la fachada al abrir la sesión de tests: siguen viendo el texto real, sin Redis. Lo que sí apareció fue el fallo inverso —una respuesta HTTP mezclando texto de catálogo con texto de respaldo—, causado por convivir bean y fachada; lo resolvió la Fase 4 al dejar un solo camino (§3.9). Nota: el respaldo de `VALIDACION` resultó ser **inalcanzable**, porque ninguna clave usa esa categoría (§6) |
| **R4** | **`FLUSHDB` borra el catálogo.** Comparte instancia y base lógica con los buckets de rate limit y los tokens invalidados. | Documentar en `EJECUCION_LOCAL.md`; el `catalogo-loader` es idempotente y re-ejecutable. Considerar base lógica separada en despliegue |
| **R5** | **Deriva entre Redis y el script** (riesgo declarado por el propio ADR). | Fuera de alcance hasta que exista el componente de administración; queda anotado |
| **R6** | **Diff voluminoso en los 32 enums.** Alto volumen, bajo riesgo, pero un error de dedo rompe el build de todos los contextos. | Fase 4 aislada, compilación por contexto, `checkstyleMain` |
| **R7** | **Una clave faltante impide levantar producción.** Es el precio explícito del fail-fast (§3.3): lo que antes era un mensaje feo ahora es un despliegue caído. | `CatalogoCargaTest` falla el build si una clave declarada no está en el script; la carga es compuerta del pipeline con verificación de conteo; la excepción **nombra** las claves faltantes por contexto |
| **R8** | **`docker-compose up` sin el `catalogo-loader` deja el backend en bucle de arranque.** El fail-fast convierte un orden de servicios incorrecto en un fallo total, no en un aviso. | `service_completed_successfully` en `depends_on` (Fase 3); documentado en `EJECUCION_LOCAL.md` |
| **R9** | **El `@Scheduled` del monitor deja de reprogramarse si lanza.** Spring cancela la tarea ante excepción no capturada: la aplicación quedaría degradada para siempre, sin reintentar, y sin señal evidente. | `try/catch` total dentro del monitor; test `debeNoPropagarExcepcion_cuandoElPingFalla`; el `HealthIndicator` hace visible el estado atascado |
| **R10** | **Pérdida de trazabilidad por commit de los textos.** | El script `catalogo/*.properties` se versiona y se revisa con el rigor de una migración Flyway |
| **R11** | **Edición en caliente que altera los `%s` de un patrón.** Es un riesgo que el ADR *introduce*: hoy los textos no cambian sin recompilar. Sin control, el fallo no aparece hasta que un usuario dispara ese error concreto. | Aridad declarada (§3.7): el arranque siguiente aborta nombrando la clave, y en caliente `formatear` degrada al respaldo registrando `error` |

---

## 8. Criterios de aceptación

1. `./gradlew build checkstyleMain checkstyleTest jacocoTestReport` en verde **sin Redis levantado**.
2. `grep -rn "ResourceBundle" shared/message/src` sin resultados.
3. `find shared/message/src/main/resources -name "*.properties"` vacío.
4. Ningún `${` en `shared/message/src/main/java/**/annotation/`.
5. Con Redis levantado y catálogo cargado: un `PUT` inválido devuelve el texto real del catálogo.
6. Con Redis **detenido desde antes del arranque**: la aplicación **no levanta**, y el log nombra
   la causa (sin conexión) — no un `NullPointerException` ni un fallo mudo de contexto.
7. Con **una clave borrada** de Redis (`redis-cli DEL ...`) y reinicio: la aplicación **no levanta**
   y el log **nombra esa clave** y su contexto.
8. Con Redis **detenido en caliente**: la aplicación sigue sirviendo, las peticiones devuelven el
   **texto real** desde caché (no el genérico), `errorCode` correcto, **ninguna clave** en el
   cuerpo, y `/actuator/health` reporta el catálogo degradado.
9. Al **volver** Redis: dentro del intervalo de reintento el log registra la recuperación con el
   número de claves recargadas y `/actuator/health` vuelve a sano, **sin reiniciar**.
10. `redis-cli SET fichas.dominio.fichaperfil.error.titulo-duplicado "otro texto: %s"` se refleja
    en la siguiente petición **sin reiniciar**.
11. Con ese mismo texto editado a `"otro texto sin parámetro"` (aridad 0 donde se declara 1): en
    caliente la petición devuelve el texto de respaldo y registra `error` con la clave; al
    reiniciar, la aplicación **no levanta** y nombra clave, esperados y encontrados.

---

## 9. Trazabilidad

| Regla ADR-013 | Dónde se cumple |
|---|---|
| 1. Clave Redis igual a la clave semántica | §3.3, `CatalogoCargaTest` |
| 2. Nunca clave literal en el código | Sin cambios: el puerto sigue tipado sobre `ClaveMensaje` |
| 3. Un enum plano por concepto | Fase 4 solo elimina `paquete()`; la granularidad no se toca |
| 4. Todo mensaje al script versionado | Fase 3 más `CatalogoCargaTest` |
| 5. Catálogo de solo lectura | `CatalogoMensajesRedis` no expone escritura |
| 6. OpenAPI como constantes | Fase 5 (y Fase 7 para `seguridad` y `usuarios`) |
| 7. Nunca exponer la clave | §3.4, `debeNoDevolverLaClave_...` |
| 8. Degradar, no fallar | §3.4 en caliente; §3.3 en el arranque (fail-fast, recogido en ADR-013 v1.1) |
| 9. Tests sin Redis | §6, criterio de aceptación 1 |
| 10. Ningún flujo escribe | `CatalogoMensajesRedis` sin operaciones de escritura |

### Ejecución

| Fase | Estado | Commit |
|---|---|---|
| 0. Enmienda a ADR-013 (arquisoft-docs) | ✅ Hecha (v1.1) | — |
| 1. Respaldo, categorías y registro de claves | ✅ Hecha | — |
| 2. Adaptador Redis, fail-fast y monitor | ✅ Hecha | — |
| 3. Script de carga | ✅ Hecha | — |
| 4. Eliminación de ResourceBundle, `git mv` a `catalogo/` y relevo del default | ✅ Hecha | — |
| 5. OpenAPI a constantes | ✅ Hecha | — |
| 6. Tests | ✅ Hecha | — |
| 7. ApiMessages seguridad/usuarios | ✅ Hecha | — |
