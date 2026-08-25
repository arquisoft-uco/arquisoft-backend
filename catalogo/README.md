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
| `podar.sh` | Borra de Redis las claves que ya no están en los `.properties`. Manual, nunca automático. |
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
vía prevista es un contenedor. Eso implica una condición previa: **Docker tiene que estar corriendo**
antes de cualquiera de las dos formas de abajo. En Windows y macOS eso significa abrir Docker Desktop
y esperar a que el icono deje de estar en «starting»; si no, el error que sale no habla de Docker
sino de un pipe (`docker API at npipe:...`, o `/var/run/docker.sock` en Linux), y despista.

Para comprobarlo antes de nada:

```sh
docker info > /dev/null 2>&1 && echo "Docker listo" || echo "Docker no está corriendo"
```

### Instancia local (docker-compose)

Con la pila local basta el servicio, que ya trae las variables apuntando al Redis del compose:

```sh
docker compose up redis
docker compose up catalogo-loader
```

El primer comando es necesario si el Redis no está ya levantado: `catalogo-loader` depende de él con
`service_healthy` y se queda esperando hasta que responda.

Levantando la pila completa con `docker-compose up`, la carga ocurre sola: el backend espera al
servicio `catalogo-loader` con `service_completed_successfully`.

### Cualquier otra instancia (nube, staging)

Con un contenedor de usar y tirar, montando este directorio:

```sh
MSYS_NO_PATHCONV=1 docker run --rm -v "$(pwd)/catalogo:/catalogo" \
    -e REDIS_HOST=... -e REDIS_PORT=6379 -e REDIS_USER=... -e REDIS_PASSWORD=... \
    redis:7-alpine sh /catalogo/cargar.sh
```

Se ejecuta **desde la raíz del repositorio**, no desde `catalogo/`: el `$(pwd)/catalogo` del volumen
lo da por hecho.

Los valores son los mismos que ya usa la aplicación: lee exactamente esas cuatro variables. Con ACL
de Redis 6+ hacen falta usuario **y** contraseña, no solo una.

El prefijo `MSYS_NO_PATHCONV=1` es para Git Bash en Windows, que si no reescribe **los dos** lados:
el `/catalogo` del volumen y el `/catalogo/cargar.sh` del argumento, este último a algo como
`C:/Program Files/Git/catalogo/cargar.sh`. En Linux y macOS la variable sobra y no molesta, así que
el comando es el mismo en todas partes.

> El runtime de msys mira si la variable **está definida**, no qué vale. `MSYS_NO_PATHCONV=`,
> `MSYS_NO_PATHCONV=1` y hasta `MSYS_NO_PATHCONV=0` desactivan por igual la conversión de rutas.
> Se documenta con `=1` porque `=0` se lee como «desactivado» y significa lo contrario de lo que
> parece, y la forma vacía no dice nada a quien la ve por primera vez.

### Qué actualiza exactamente

El script recorre los cinco `.properties` y hace un `SET` por clave, así que **toda clave presente en
los archivos queda con el texto de los archivos**: es re-ejecutable y sobrescribe siempre, no compara
ni omite lo que no cambió.

Lo que **no** hace es borrar. Nunca ejecuta `FLUSHDB` —esta instancia de Redis comparte espacio con
los buckets de rate limit y los tokens invalidados de seguridad—, ni un `DEL` de las claves que
sobran. Consecuencia práctica: si renombras o eliminas una clave del `.properties`, la vieja **sigue
en Redis** después de recargar. No rompe nada (nadie la pide, y el fail-fast del arranque solo
comprueba que no falte ninguna de las declaradas), pero se acumula. Para limpiarla hay que borrarla a
mano:

```sh
docker compose exec redis redis-cli -a default123 --no-auth-warning DEL la.clave.vieja
```

El script termina verificando con `EXISTS` que todo lo escrito está presente, y sale con error si no:
para que el fallo aparezca en la carga y no en el arranque.

> **La carga es obligatoria antes de desplegar.** Con el fail-fast de arranque, una sola clave
> declarada en Java sin texto en Redis impide que la aplicación levante.

## Podar lo que sobra

`cargar.sh` nunca borra (ver arriba), así que las claves renombradas o eliminadas se quedan en Redis.
`podar.sh` es la otra mitad: compara lo que hay en Redis con lo que declaran los `.properties` y borra
la diferencia.

Va en un script aparte a propósito. Cargar es un paso obligatorio del despliegue y debe poder
ejecutarse sin pensarlo; borrar es destructivo y merece una decisión explícita.

Solo toca claves cuyo nombre empieza por `<contexto>.` — el espacio del catálogo. El resto de claves
de esta instancia usan el prefijo `arquisoft:` (buckets de rate limit, blacklist de jti, caché), así
que no hay solape posible. Recorre con `SCAN`, no con `KEYS`: la instancia es compartida y `KEYS` la
bloquea entera mientras recorre el espacio de claves.

### En seco, primero

```sh
DRY_RUN=1 docker compose --profile mantenimiento up catalogo-podador
```

Lista las claves sobrantes y no borra nada. Conviene mirar esa lista antes de la pasada real.

### Local

```sh
docker compose --profile mantenimiento up catalogo-podador
```

El perfil `mantenimiento` es deliberado: mantiene el servicio fuera de `docker compose up`, para que
levantar la pila no borre nada por su cuenta.

### Cualquier otra instancia

```sh
MSYS_NO_PATHCONV=1 docker run --rm -v "$(pwd)/catalogo:/catalogo" \
    -e REDIS_HOST=... -e REDIS_PORT=6379 -e REDIS_USER=... -e REDIS_PASSWORD=... \
    redis:7-alpine sh /catalogo/podar.sh
```

### Salvaguardas

El riesgo real de un script que borra es ejecutarlo apuntando al directorio equivocado: sin claves
declaradas, «no sobra nada» y «sobra todo» son la misma lectura. Hay dos cortes antes de tocar Redis:

1. Si falta cualquiera de los cinco `.properties`, aborta nombrando el que falta.
2. Si los archivos existen pero no declaran ni una clave, aborta diciendo que continuar borraría el
   catálogo entero.

Es re-ejecutable: una segunda pasada informa de 0 sobrantes y sale con éxito.

## Añadir un mensaje

**1.** Declarar la clave en su enum de `shared/message/.../key/`, con su aridad:

```java
ERROR_ASESOR_NO_ENCONTRADO("fichas.dominio.asesorficha.error.no-encontrado", 1),
```

El segundo argumento es cuántos marcadores lleva el patrón. No es decorativo, y cuenta también los
de las claves de log: `String.formatted` lanza si faltan argumentos y los ignora en silencio si
sobran, y SLF4J no protesta en ninguno de los dos casos — se come los sobrantes e imprime el `{}`
literal cuando faltan, en un log de producción que nadie mira hasta que hay un incidente. La aridad
declarada es lo que convierte los cuatro casos en un fallo visible.

Si el enum es nuevo —no una constante más en uno existente— hay que registrarlo también en
`ClavesCatalogo.ENUMS`.

**2.** Escribir el texto en el `.properties` del contexto que le corresponda:

```properties
fichas.dominio.asesorficha.error.no-encontrado=No existe el asesor: %s
```

La clave sigue el patrón `contexto.capa.objeto.tipo.descripcion`. La sustitución es
`String.formatted`, así que los parámetros son `%s`, **no** `{0}`. Los patrones de log son la
excepción: conservan los `{}` de SLF4J y se resuelven con `obtener`, nunca con `formatear`.

La vía y el marcador van juntos, y el catálogo lo comprueba en cada llamada (`AridadClave`): pedir
por `obtener` un patrón con `%s` devolvería los marcadores crudos al usuario final, y pedir por
`formatear` uno de log no sustituiría nada porque ahí no hay ningún `%s`. En ambos casos el catálogo
de Redis registra el error y degrada al respaldo, y el de pruebas lanza — en un test no hay nada que
degradar y un fallo silencioso no lo vería nadie.

**3.** `./gradlew :shared:message:test`

`CatalogoCargaTest` es la compuerta, y comprueba las dos direcciones: clave sin texto, texto sin
clave, aridad que no coincide con los marcadores reales del patrón —`%s`, o `{}` si es de log—,
clave declarada en dos archivos, y
enums que `ClavesCatalogo.ENUMS` no registra. Aquí es donde el error sale gratis.

**4.** Cargar en Redis antes de desplegar (ver arriba).

## Qué NO va aquí

El catálogo guarda prosa que lee una persona. Lo que una herramienta o un contrato compara de forma
exacta se queda como constante de Java, porque traducirlo rompe justo lo que lo lee:

- Códigos de error (`*Codes`), nombres de campo (`*Fields`), límites (`*Limits`)
- Claves y valores centinela del MDC (`TrazaKeys`, `TrazaValores`), cabeceras (`TrazaHeaders`)
- Marcadores de log que se buscan en Loki (`AUDIT`), nombres de infraestructura
  (`RabbitMQConfig.EXCHANGE_NAME`), códigos de API externas (`NoSuchKey` de MinIO)
- Las etiquetas visibles de los enums de catálogo (`EstadoFicha.getNombre()`): su fuente de verdad
  es el MER, y una copia aquí sería una segunda

Hay además una razón que el compilador impone: los valores de anotación tienen que ser expresiones
constantes (JLS §9.7.1).
