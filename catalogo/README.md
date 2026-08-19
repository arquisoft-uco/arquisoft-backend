# Catálogo de mensajes

Los textos que la aplicación muestra en tiempo de ejecución —errores, validaciones y patrones de
log— viven aquí, no en el código. Este directorio es la **única fuente de verdad**: es lo que se
carga en Redis para el despliegue y lo que leen los tests unitarios a través del artefacto de test
fixtures de `shared:message`, así que las dos vistas no pueden divergir.

El diseño está en ADR-013 (repositorio `arquisoft-docs`) y en `.workspace/h-plan/PLAN-HT-013.md`.

## Contenido

| Archivo | Contenido |
|---|---|
| `cargar.sh` | Carga el catálogo en Redis. Paso previo obligatorio del despliegue. |
| `app.properties` | Textos transversales (`shared:*`) |
| `fichas.properties` | Contexto `fichas` |
| `seguridad.properties` | Contexto `seguridad` |
| `usuarios.properties` | Contexto `usuarios` |
| `notificaciones.properties` | Contexto `notificaciones` |

Los textos de documentación OpenAPI **no** están aquí: son constantes de Java compiladas
(`{Contexto}ApiMessages`), porque la especificación se congela al arrancar y no se beneficia de nada de
lo que este mecanismo aporta.

## Cargar en Redis

**No hace falta instalar `redis-cli`.** Redis no publica binarios nativos para Windows, así que la
vía prevista es un contenedor. Contra cualquier instancia —nube, staging, la que sea— con un
contenedor de usar y tirar:

```sh
MSYS_NO_PATHCONV=1 docker run --rm -v "$(pwd)/catalogo:/catalogo" \
    -e REDIS_HOST=... -e REDIS_PORT=6379 -e REDIS_USER=... -e REDIS_PASSWORD=... \
    redis:7-alpine sh /catalogo/cargar.sh
```

Los valores son los mismos que ya usa la aplicación: lee exactamente esas cuatro variables. Con ACL
de Redis 6+ hacen falta usuario **y** contraseña, no solo una.

El prefijo `MSYS_NO_PATHCONV=1` es para Git Bash en Windows, que si no reescribe **los dos** lados:
el `/catalogo` del volumen y el `/catalogo/cargar.sh` del argumento, este último a algo como
`C:/Program Files/Git/catalogo/cargar.sh`. En Linux y macOS la variable sobra y no molesta, así que
el comando es el mismo en todas partes.

Contra la instancia local de `docker-compose` basta con el servicio, que ya trae las variables:

```sh
docker compose up catalogo-loader
```

Levantando la pila completa con `docker-compose up`, la carga ocurre sola: el backend espera al
servicio `catalogo-loader` con `service_completed_successfully`.

El script es **re-ejecutable** y sobrescribe clave por clave. Nunca hace `FLUSHDB`: esta instancia de
Redis comparte espacio con los buckets de rate limit y los tokens invalidados de seguridad.

> **La carga es obligatoria antes de desplegar.** Con el fail-fast de arranque, una sola clave
> declarada en Java sin texto en Redis impide que la aplicación levante. Por eso el script termina
> verificando con `EXISTS` que todo lo escrito está presente, y sale con error si no: para que el
> fallo aparezca en la carga y no en el arranque.

## Añadir un mensaje

**1.** Declarar la clave en su enum de `shared/message/.../key/`, con su aridad:

```java
ERROR_ASESOR_NO_ENCONTRADO("fichas.dominio.asesorficha.error.no-encontrado", 1),
```

El segundo argumento es cuántos parámetros lleva el patrón. No es decorativo: `String.formatted`
lanza si faltan argumentos y los ignora en silencio si sobran; la aridad declarada es lo que
convierte ambos casos en un fallo visible.

Si el enum es nuevo —no una constante más en uno existente— hay que registrarlo también en
`ClavesCatalogo.ENUMS`.

**2.** Escribir el texto en el `.properties` del contexto que le corresponda:

```properties
fichas.dominio.asesorficha.error.no-encontrado=No existe el asesor: %s
```

La clave sigue el patrón `contexto.capa.objeto.tipo.descripcion`. La sustitución es
`String.formatted`, así que los parámetros son `%s`, **no** `{0}`. Los patrones de log son la
excepción: conservan los `{}` de SLF4J y se resuelven con `obtener`, nunca con `formatear`.

**3.** `./gradlew :shared:message:test`

`CatalogoCargaTest` es la compuerta, y comprueba las dos direcciones: clave sin texto, texto sin
clave, aridad que no coincide con los `%s` reales del patrón, clave declarada en dos archivos, y
enums que `ClavesCatalogo.ENUMS` no registra. Aquí es donde el error sale gratis.

**4.** Cargar en Redis antes de desplegar (ver arriba).

## Qué NO va aquí

El catálogo guarda prosa que lee una persona. Lo que una herramienta o un contrato compara de forma
exacta se queda como constante de Java, porque traducirlo rompe justo lo que lo lee:

- Códigos de error (`*Codes`), nombres de campo (`*Fields`), rutas (`*Routes`), límites (`*Limits`)
- Claves y valores centinela del MDC (`TrazaKeys`, `TrazaValores`), cabeceras (`TrazaHeaders`)
- Marcadores de log que se buscan en Loki (`AUDIT`), nombres de infraestructura
  (`RabbitMQConfig.EXCHANGE_NAME`), códigos de API externas (`NoSuchKey` de MinIO)
- Las etiquetas visibles de los enums de catálogo (`EstadoFicha.getNombre()`): su fuente de verdad
  es el MER, y una copia aquí sería una segunda

Hay además una razón que el compilador impone: los valores de anotación tienen que ser expresiones
constantes (JLS §9.7.1).
