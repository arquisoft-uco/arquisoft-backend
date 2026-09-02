# Plantillas de correo

El HTML que envuelve todos los correos vive aquí, no dentro del jar. Este directorio es la **única
fuente de verdad**: es lo que se carga en Redis para el despliegue y lo que lee
`PlantillaCorreoRenderTest` para probar el render, así que las dos vistas no pueden divergir.

Es el mismo mecanismo que el catálogo de mensajes (ADR-013) aplicado a otro tipo de contenido, con
una diferencia de lectura que conviene entender antes de tocar nada — ver
[Cómo la lee la aplicación](#cómo-la-lee-la-aplicación).

## Contenido

| Archivo | Contenido |
|---|---|
| `cargar.sh` | Carga las plantillas en Redis. Paso previo obligatorio del despliegue. |
| `podar.sh` | Borra de Redis las plantillas que ya no se declaran. Manual, nunca automático. |
| `correo-base.html` | Envoltura HTML común a todos los correos. Clave `plantilla.correo-base`. |

## Qué es una plantilla y qué no

El reparto con el catálogo de mensajes no es arbitrario, y es la confusión más fácil de tener:

| | Dónde vive | Qué es |
|---|---|---|
| **Texto** del correo | `catalogo/notificaciones.properties` (`notificaciones.aplicacion.plantilla.*`) | Asunto, cuerpo y pie. Prosa con `%s`. |
| **Envoltura** HTML | Aquí (`plantilla.correo-base`) | Tabla, colores, tipografía. Tres huecos y ningún texto. |

Un correo se compone de las dos cosas: el consumidor resuelve el texto con
`Mensajes.formatear(PlantillaKey.CUERPO_...)` y `PlantillaCorreoRender` lo inserta en los huecos de
esta envoltura. **Aquí no va ni una palabra dirigida a una persona** — si estás a punto de escribir
una frase en el HTML, esa frase pertenece al catálogo.

La envoltura declara exactamente tres huecos, y los tres son obligatorios:

```
{{titulo}}   -> asunto
{{cuerpo}}   -> cuerpo
{{pie}}      -> pie
```

`PlantillaCorreoRender` los verifica al arrancar y aborta el contexto si falta alguno. No es celo
excesivo: `String.replace` de un hueco que no está no falla, simplemente no sustituye, así que una
plantilla a la que le falte `{{cuerpo}}` enviaría correos vacíos sin un solo error en el log.

Los valores se escapan antes de insertarse (`&`, `<`, `>`, `"`), así que un título de proyecto con
marcado no puede inyectar HTML en el correo.

## Cargar en Redis

**No hace falta instalar `redis-cli`.** Redis no publica binarios nativos para Windows, así que la
vía prevista es un contenedor, y eso implica que **Docker tiene que estar corriendo** antes de
cualquiera de las dos formas de abajo. Si no lo está, el error no habla de Docker sino de un pipe
(`docker API at npipe:...`, o `/var/run/docker.sock` en Linux), y despista.

```sh
docker info > /dev/null 2>&1 && echo "Docker listo" || echo "Docker no está corriendo"
```

### Instancia local (docker-compose)

```sh
docker compose up redis
docker compose up plantillas-loader
```

El primer comando sobra si Redis ya está arriba: `plantillas-loader` depende de él con
`service_healthy` y espera a que responda.

Levantando la pila completa con `docker compose up` la carga ocurre sola: el backend espera al
servicio con `service_completed_successfully`, igual que hace con `catalogo-loader`.

### Cualquier otra instancia (nube, staging)

Con un contenedor de usar y tirar, montando este directorio:

```sh
MSYS_NO_PATHCONV=1 docker run --rm -v "$(pwd)/plantillas:/plantillas" \
    -e REDIS_HOST=... -e REDIS_PORT=6379 -e REDIS_USER=... -e REDIS_PASSWORD=... \
    redis:7-alpine sh /plantillas/cargar.sh
```

Se ejecuta **desde la raíz del repositorio**, no desde `plantillas/`: el `$(pwd)/plantillas` del
volumen lo da por hecho.

Las cuatro variables son las mismas que ya usa la aplicación. Con ACL de Redis 6+ hacen falta usuario
**y** contraseña, no solo una: pasar solo `--user` deja la autenticación a medias y el servidor
responde `NOAUTH`, que desde fuera se ve como «Redis no responde».

El prefijo `MSYS_NO_PATHCONV=1` es para Git Bash en Windows, que si no reescribe **los dos** lados:
el `/plantillas` del volumen y el `/plantillas/cargar.sh` del argumento, este último a algo como
`C:/Program Files/Git/plantillas/cargar.sh`. En Linux y macOS la variable sobra y no molesta, así que
el comando es el mismo en todas partes.

> El runtime de msys mira si la variable **está definida**, no qué vale: `MSYS_NO_PATHCONV=`,
> `=1` y `=0` desactivan por igual la conversión. Se documenta con `=1` porque `=0` se lee como
> «desactivado» y significa lo contrario de lo que parece.

### Qué actualiza exactamente

Un `SET` por plantilla declarada, con el fichero entero entrando por stdin — sin escapes, a
diferencia del catálogo, porque el HTML lleva comillas, saltos de línea y llaves que cualquier otra
vía rompería. Es re-ejecutable y sobrescribe siempre.

Lo que **no** hace es borrar: nunca ejecuta `FLUSHDB` —esta instancia comparte espacio con el
catálogo, los buckets de rate limit y los tokens invalidados— ni un `DEL` de lo que sobre. Para eso
está `podar.sh`.

Verifica con `EXISTS` que lo escrito está presente y sale con error si no, para que el fallo aparezca
en la carga y no en el arranque.

> **La carga es obligatoria antes de desplegar.** Sin la plantilla en Redis la aplicación no levanta:
> el bean que la lee se construye al arrancar y aborta el contexto si la clave falta o está vacía.

## Podar lo que sobra

`cargar.sh` nunca borra, así que una plantilla renombrada o retirada se queda en Redis. `podar.sh` es
la otra mitad: compara lo que hay con lo declarado y borra la diferencia.

Va en un script aparte a propósito, como en el catálogo: cargar es un paso obligatorio del despliegue
y debe poder ejecutarse sin pensarlo; borrar es destructivo y merece una decisión explícita.

**Solo toca claves `plantilla.*`**, y eso no es un detalle cosmético. El catálogo vive en
`<contexto>.` y `catalogo/podar.sh` escanea ese espacio: si la plantilla colgara de
`notificaciones.`, la poda del catálogo la borraría como sobrante en la primera pasada y la
aplicación dejaría de arrancar sin que nadie relacionara las dos cosas. El resto de la instancia usa
el prefijo `arquisoft:`, así que no hay más solapes. Recorre con `SCAN`, no con `KEYS`: la instancia
es compartida y `KEYS` la bloquea entera.

### En seco, primero

```sh
DRY_RUN=1 docker compose --profile mantenimiento up plantillas-podador
```

Lista lo que borraría sin borrar nada.

### Local

```sh
docker compose --profile mantenimiento up plantillas-podador
```

El perfil `mantenimiento` mantiene el servicio fuera de `docker compose up`, para que levantar la
pila no borre nada por su cuenta.

### Cualquier otra instancia

```sh
MSYS_NO_PATHCONV=1 docker run --rm -v "$(pwd)/plantillas:/plantillas" \
    -e REDIS_HOST=... -e REDIS_PORT=6379 -e REDIS_USER=... -e REDIS_PASSWORD=... \
    redis:7-alpine sh /plantillas/podar.sh
```

### Salvaguardas

El riesgo real de un script que borra es apuntarlo al directorio equivocado: sin nada declarado, «no
sobra nada» y «sobra todo» son la misma lectura. Hay dos cortes antes de tocar Redis:

1. Si falta el `.html` de una plantilla declarada, aborta nombrándolo.
2. Si no queda ninguna declarada, aborta diciendo que continuar las borraría todas.

`PODA_TOTAL=1` salta la comparación y borra todas las claves `plantilla.*`. No es mantenimiento:
deja la aplicación sin poder arrancar hasta volver a ejecutar `cargar.sh`. Existe para el ensayo
manual de degradación.

Es re-ejecutable: una segunda pasada informa de 0 sobrantes y sale con éxito.

## Cómo la lee la aplicación

**Se puede ajustar la plantilla y verlo reflejado sin desplegar.** `MonitorPlantillaCorreo` la relee
cada `notificacion.plantilla-refresco.intervalo` (por defecto `PT5M`), así que el ciclo de trabajo es:

```sh
# 1. editar plantillas/correo-base.html
# 2. cargarla
docker compose up plantillas-loader
# 3. esperar el intervalo — el siguiente correo ya sale con el maquetado nuevo
```

En el log aparece cuándo entró en vigor:

```
[NOTIFICACION:PLANTILLA] Plantilla de correo actualizada desde Redis — clave=plantilla.correo-base
```

Para verlo al instante durante una prueba, arranca con el intervalo bajado:
`--notificacion.plantilla-refresco.intervalo=PT10S`. Y para desactivar el refresco por completo,
`notificacion.plantilla-refresco.habilitado=false` (`NOTIFICACION_PLANTILLA_REFRESCO=false`): la
plantilla queda congelada en la versión del arranque.

### La versión nueva solo se publica si es válida

Antes de sustituir la que está en uso, el refresco pasa la candidata por la misma verificación de
huecos que corre al arrancar (`HuecosPlantillaCorreo.verificar`, un único sitio para las dos rutas).
Si no la pasa —o si Redis no responde— se **descarta y se conserva la anterior**, con un aviso que
nombra el problema:

```
[NOTIFICACION:PLANTILLA] No se pudo actualizar la plantilla, se mantiene la anterior —
    clave=plantilla.correo-base motivo=La plantilla de correo no esta disponible o no contiene {{cuerpo}}
```

Sin esa verificación el refresco sería una trampa, no una comodidad: `String.replace` de un hueco
ausente no falla, así que un `SET` mal hecho pasaría a enviar correos sin cuerpo y sin un solo error.
Un fallo del refresco **nunca** deja la aplicación peor de lo que estaba: en el peor caso sigue con
la plantilla que ya funcionaba.

Al arrancar, en cambio, no hay «anterior» a la que caer: una plantilla ausente, vacía o sin sus
huecos **aborta el contexto**. Es el mismo fail-fast del catálogo.

### Qué sigue congelado y qué no

| | Catálogo de mensajes | Plantilla de correo |
|---|---|---|
| Cuándo consulta Redis | En **cada** `obtener`/`formatear` | Al arrancar y cada `plantilla-refresco.intervalo` |
| Si Redis cae después | Sirve desde la caché en memoria | No le afecta: sigue con la última válida |
| Cambiar el contenido | Recargar, efecto inmediato | Recargar, efecto en el siguiente ciclo |

La diferencia que queda es de frecuencia, no de mecanismo, y responde a que la envoltura está en el
camino del envío: es un blob de ~1 KB que se usaría una vez por destinatario, mientras que el texto
son claves pequeñas. Releerla en cada correo no aportaría nada que un ciclo de minutos no dé ya.

**Y conviene saber que el texto nunca estuvo congelado.** El asunto, el cuerpo y el pie salen del
catálogo y se consultan en cada envío: corregir una errata en Redis surte efecto en el correo
siguiente, sin esperar ningún ciclo.

## Añadir una plantilla

Hoy solo hay una, y el caso normal es editar `correo-base.html`, no crear otra. Si de verdad hace
falta una segunda (un correo con una estructura distinta, no un texto distinto):

**1.** Crear el `.html` en este directorio, con los huecos que su render vaya a sustituir.

**2.** Declararla en la variable `PLANTILLAS` de **`cargar.sh` y `podar.sh`**, que es una lista
explícita y no un `*.html`: lo que Redis debe tener es lo que el arranque exige, así que se declara,
no se deduce de lo que haya en el directorio. Olvidar `podar.sh` hace que la poda la borre como
sobrante.

**3.** Apuntar la propiedad que la use (`notificacion.plantilla` para la base) a su clave
`plantilla.<nombre>`, y actualizar `.env.example`.

**4.** `./gradlew :notificaciones:infrastructure:test`

**5.** Cargar en Redis antes de desplegar (ver arriba).

## Qué NO va aquí

- **Texto dirigido a una persona.** Va al catálogo (`catalogo/notificaciones.properties`). Aquí solo
  el esqueleto y sus huecos.
- **Imágenes.** Un `<img>` a un archivo del repositorio no llega al correo: hay que adjuntarlo como
  recurso embebido o servirlo desde una URL pública. Hoy la plantilla no usa ninguna, a propósito.
- **CSS externo.** Los clientes de correo no cargan hojas de estilo; el estilo va en línea, como está
  ahora.
