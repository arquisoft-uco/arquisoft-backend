# PLAN: Catálogo de mensajes distribuido sobre Redis con blindaje y fail-fast

## Metadata
- **ID Historia:** HT-012
- **Bounded Context:** transversal — `shared:message`, `shared:web`, `shared:exception`
- **Módulos Gradle afectados:** los anteriores más la raíz (`build.gradle`, `application*.yml`)
- **Fecha de plan:** 2026-08-06 (v5)
- **Rama sugerida:** `feature/HT-012-catalogo_mensajes_redis`
- **Base:** commit `e22be0e` — `shared:exception`, `shared:validation` y `shared:util` ya extraídos

### Historial de revisiones
| v | Cambio | Motivo |
|---|---|---|
| v1 | Módulo nuevo `shared:message-redis` | — |
| v2 | Todo dentro de `shared:message`; excepciones `extends RuntimeException` | Verificado que `implementation` aísla Redis del dominio (§2.1) |
| v3 | Análisis del ciclo `message → domain` | `shared:exception` resultó ser un *paquete*, no un módulo |
| v4 | Extracción de `shared:exception` + nomenclatura en español | Decisión del usuario: reutilizar la jerarquía existente |
| **v5** | **Fase 0a ejecutada — fueron tres módulos, no uno** (§2.2). **Mensaje de usuario y técnico en `BaseError`** (§2.5) | La extracción destapó dos dependencias mutuas más. El diseño de dos audiencias de §4.2 se generaliza a toda la jerarquía |

---

## 1. Resumen

Redis pasa a ser la fuente distribuida del catálogo, para que un cambio de texto se propague en
tiempo real sin redespliegue. La estructura actual **no se reemplaza**: los 27 enums de `key.*`,
las constantes de `constant.*` y los `.properties` siguen tal cual. Redis se añade **encima** como
capa de distribución, no debajo como sustituto.

Condiciones de blindaje:

1. Mensajes mínimos disponibles al arrancar — fail-fast si no.
2. La clave se valida antes de resolverla.
3. Mensajes quemados que permitan **explicar que el catálogo mismo no funciona**.
4. Clave inexistente lanza excepción, no degrada en silencio.

### 1.1 Estado real de la validación de clave

El catálogo ya no se indexa por `String`: el puerto está tipado sobre `ClaveMensaje`. Eso cambia el
peso de la condición 2.

| Sub-requisito | Estado |
|---|---|
| Clave no vacía | **Ya garantizado.** `CatalogoMensajesClavesTest` falla en build si una constante no tiene texto. |
| Clave soportada | **Ya garantizado.** Solo compilan las declaradas en `key.*`. |
| Clave no nula | **Hueco real.** `obtener(null)` compila y revienta con `NullPointerException` sin contexto. |

El valor de validar se desplaza al borde nuevo — **Redis → catálogo** — donde sí entran `String`
sin verificar. Ahí la validación es indispensable, no defensiva (§5.3).

### 1.2 Fuera de alcance

- **`ValidationMessages.properties`** — lo lee Hibernate Validator directamente para interpolar
  `message="{clave}"`. Distribuirlo exige un `MessageInterpolator` propio.
- **`messages/fichas-api.properties`** — lo consume springdoc al construir el `OpenAPI` en el
  arranque. Es estático por naturaleza.

Quedan **5 de 7 paquetes de mensajes**: `app`, `fichas`, `seguridad`, `usuarios`, `notificaciones`
— los direccionables por `ClaveMensaje`.

---

## 2. Ubicación y jerarquía de excepciones

### 2.1 Redis dentro de `shared:message` (verificado)

`shared:message` aplica `java-library`. Sus dependencias `implementation` **no se propagan al
compile classpath de los consumidores**. Comprobación ejecutada sobre el repo:

```
:shared:message  compileClasspath  → spring-data-redis presente   ✅ puede compilar Redis
:shared:domain   compileClasspath  → ausente                      ✅ el dominio no lo ve
:fichas:domain   compileClasspath  → ausente                      ✅ la regla se sostiene
:fichas:domain   runtimeClasspath  → presente                     ⚠️ residuo aceptado
```

No hay ciclo de módulos: la dependencia es al artefacto Maven `spring-boot-starter-data-redis`, no
a `project(':shared:redis')`. **No se reutiliza `RedisClient` de `shared:redis`** — eso sí crearía
el ciclo `message → redis → domain → message`. Se usa `StringRedisTemplate` directo.

**RD-01:** las dependencias Spring de `shared:message` se declaran `implementation`. Un `api`
accidental rompería el aislamiento en silencio; el test de §12 lo vigila.

Los módulos de dominio tendrán spring-data-redis en su **runtime** classpath. No pueden compilar
contra él, que es donde está la garantía, y en la aplicación ensamblada ya estaba presente vía
`shared:redis`.

### 2.2 Extracción de módulos — fase 0a ✅ EJECUTADA (commit `e22be0e`)

#### El problema

`shared:message` necesitaba extender `InfrastructureException`, pero esa jerarquía era un **paquete
Java dentro de `shared:domain`**, no un módulo. Depender de `shared:domain` cerraba el ciclo, porque
`shared:domain` ya declara `api project(':shared:message')`.

Gradle no degrada, **rechaza el build**: `Circular dependency between the following tasks:`. Y
ningún scope lo evita — el ciclo es entre tareas de compilación, no entre configuraciones:

| Scope probado | ¿Evita el ciclo? |
|---|---|
| `implementation project(':shared:domain')` | ❌ |
| `compileOnly project(':shared:domain')` | ❌ |
| `compileOnlyApi project(':shared:domain')` | ❌ |

#### El corte real: tres módulos, no uno

El plan v4 preveía extraer un solo módulo. La ejecución destapó **dos dependencias mutuas más**,
invisibles hasta ese momento porque Java permite ciclos entre paquetes de un mismo módulo y Gradle
no los ve.

**Cadena 1 — el trío de validación.** Sacar solo `ValidationResult` a `shared:exception` no
funciona, porque las dos excepciones de validación la importan y ella las lanza:

```
ValidationResult               → lanza   DomainValidationException, ApplicationValidationException
DomainValidationException      → recibe  ValidationResult
ApplicationValidationException → recibe  ValidationResult
```

Las tres van juntas. Y como `DomainValidationException extends DomainException`, dejarlas en
`shared:validation` hace que la flecha hacia `shared:exception` quede **en un solo sentido**.

**Cadena 2 — los helpers.** `DomainValidator` usa `UtilText`, `UtilObject`, `UtilUUID` y
`UtilCollection`, que vivían en `shared:domain`. Dejarlos ahí reabre `validation → domain →
validation`. Como las seis clases `Util*` son JDK puro sin una sola dependencia, salieron a una
hoja propia.

#### Resultado

| Módulo | Contenido |
|---|---|
| `shared:exception` | `BaseException`, `BaseError`, `DomainException`, `ApplicationException`, `AuthorizationException`, `InfrastructureException` |
| `shared:validation` | `DomainValidator`, `ValidationResult`, `DomainValidationException`, `ApplicationValidationException` |
| `shared:util` | `UtilText`, `UtilObject`, `UtilUUID`, `UtilCollection`, `UtilDate`, `UtilNumber` |

```
shared:util   shared:exception   shared:message   shared:logger      (4 hojas)
      ↑              ↑                  ↑
      └──────── shared:validation ──────┘
                     ↑ api
                shared:domain  →  amqp · minio · notification · postgres · redis · web
```

#### Costo real

La v4 prometía **0 ediciones de código**. No se cumplió, y la razón es la cadena 1: las dos
excepciones de validación cambiaron de paquete Java, de `com.arquisoft.shared.exception` a
`com.arquisoft.shared.validation`.

Se evaluó la alternativa —dejar el paquete `com.arquisoft.shared.exception` partido entre dos
módulos Gradle— y se descartó: compila (se verificó que el proyecto no usa JPMS, así que los split
packages son legales), pero es exactamente el tipo de suciedad oculta que este refactor elimina.

| Cambio | Alcance real |
|---|---|
| `git mv` | 19 archivos |
| `.java` con `import` reescrito | **21** |
| `build.gradle` nuevos | 3 |
| `settings.gradle` / `shared/domain/build.gradle` | 2 modificados |
| README nuevos | 3 |

#### Problemas, con su estado

| # | Problema | Estado |
|---|---|---|
| P1 | `api` obligatorio en `shared:domain` para los cuatro módulos; con `implementation` caen ~160 archivos | ✅ Cubierto, con el porqué en el `build.gradle` |
| P2 | `ValidationResult` separado de `DomainValidator` | ✅ **Resuelto** — van juntos en `shared:validation`, que es más de lo que la v4 lograba |
| P3 | Tres módulos más (12 `shared:*`) | Aceptado |
| P4 | Alguien podría meter lógica de negocio en los módulos nuevos | ✅ Regla explícita en el README de cada uno |
| P5 | CLAUDE.md y AGENTS.md ubican excepciones, validación y utils en `shared:domain` | ⏳ **Pendiente** |
| P6 | JaCoCo excluye `shared:*` | Nulo — heredado |

#### Verificación

- `clean build` completo en verde (238 tareas, todas ejecutadas).
- **El ciclo está muerto**: se añadió temporalmente `implementation project(':shared:exception')` a
  `shared:message` más una clase extendiendo `InfrastructureException` — compiló sin ciclo, y se
  revirtió.
- Dos fallos durante la ejecución, ambos corregidos: recrear el ciclo `exception ↔ validation` al
  mover solo `ValidationResult` (detectado antes de compilar), y dos `import` redundantes en
  `ValidationResult` tras el cambio de paquete (detectado por Checkstyle, `maxWarnings = 0`).

#### Lo que habilita

```java
public class CatalogoMensajesException extends InfrastructureException     // 503
public final class ClaveMensajeAusenteException  extends CatalogoMensajesException
public final class ClaveMensajeInvalidaException extends CatalogoMensajesException
```

**Un matiz que no cambia:** `GlobalAppExceptionHandler` sigue necesitando un `@ExceptionHandler`
dedicado para `CatalogoMensajesException`, porque construye sus respuestas llamando al catálogo, y
usar el catálogo para describir el fallo del catálogo es circular. La extracción arregla la
jerarquía de tipos, no la circularidad del camino de reporte — son problemas distintos (§4.3).

### 2.2b Mensaje de usuario y mensaje técnico en `BaseError`

**Sí es posible, sí lo procesa el handler, y el hueco que cierra es real.** Pero conviene ver
primero qué existe ya, porque la mitad del mecanismo está construido.

#### Lo que ya hay

`BaseError` transporta **tres** cosas, no dos:

```java
private final String codigoError;
private final String message;
private final List<String> traza;   // cadena de causas, construida desde el Throwable
```

Y `ErrorResponseDTO.fromBaseException` ya separa audiencias:

```java
// La traza de causas NO se incluye en la respuesta al cliente — podría exponer
// hostnames, URLs internas o detalles de infraestructura (OWASP A05).
.message(ex.getMessage())    // ← esto SÍ llega al cliente
```

O sea: **el canal técnico existe (`traza`) y ya está excluido de la respuesta.** Lo que falta es
que sea un mensaje que el autor escribe, en vez de una derivación automática del `Throwable`.

#### El hueco

`message` tiene que servir a las dos audiencias a la vez, y **nada obliga a que sea seguro para el
cliente**. La evidencia está en el propio código: `RedisBucketResolver` lleva un comentario escrito
a mano recordándolo —

> `InfrastructureException` con mensaje generico: si llegara a la capa web el cliente ve un mensaje
> sin detalles internos.

— y `ProveedorIdentidadNoDisponibleException(String message, Throwable cause)` acepta lo que le
pasen. Si alguien escribe `new InfrastructureException("No se pudo conectar a redis://10.0.1.5:6379", ...)`,
ese host **sale en el JSON de respuesta**. La protección de hoy es disciplina y un comentario, no
estructura.

#### Dónde gana y dónde estorba

La distinción no vale lo mismo en las cinco bases:

| Base | HTTP | ¿El `message` es seguro por naturaleza? |
|---|---|---|
| `DomainException` | 422 | ✅ Sí — es texto de negocio: *«El título ya existe: X»* |
| `ApplicationException` | 400 | ✅ Sí |
| `AuthorizationException` | 403 | ✅ Sí |
| `DomainValidationException` / `ApplicationValidationException` | 422/400 | ✅ Sí — se expande en `fieldErrors[]` |
| **`InfrastructureException`** | **503** | ❌ **No** — describe un tercero caído: host, puerto, proveedor |

**RD-10.** El campo se añade a `BaseError` para todas, pero **es la forma normal solo en
`InfrastructureException`**. En las demás, un segundo mensaje añade ruido sin cerrar ningún hueco.

#### La forma

```java
// BaseError — el campo es opcional; las factorías de 2 argumentos siguen existiendo
public static BaseError of(String codigoError, String mensajeUsuario) { … }
public static BaseError of(String codigoError, String mensajeUsuario, Throwable cause) { … }
public static BaseError of(String codigoError, String mensajeUsuario, String mensajeTecnico) { … }
public static BaseError of(String codigoError, String mensajeUsuario, String mensajeTecnico, Throwable cause) { … }

public String getMensajeTecnico() { … }   // null cuando no se declaró
```

```java
// BaseException
public String getMensajeTecnico() {
    return error.getMensajeTecnico();
}
```

**Aditivo por construcción:** las firmas de 2 argumentos no cambian, así que **las 35 excepciones
que extienden estas bases no se tocan**.

#### El handler sí puede procesarlo, y casi no cambia

`GlobalAppExceptionHandler.handleBaseException` ya construye el log y el cuerpo en sentencias
separadas — es justo la separación que hace falta:

```java
// hoy
log.error("Exception [{}] in {}: [{}] {}", …, ex.getMessage(), ex);

// con RD-10 — degrada solo si no hay técnico, así que nada se rompe
log.error("Exception [{}] in {}: [{}] {}", …,
        ex.getMensajeTecnico() != null ? ex.getMensajeTecnico() : ex.getMessage(), ex);
```

El cuerpo sigue usando `ex.getMessage()`. **`ErrorResponseDTO` no gana ningún campo**, y eso es
deliberado: un mensaje técnico en el JSON sería exactamente la fuga que se quiere evitar.

#### Cómo llega el mensaje técnico al usuario técnico

Por el `traceId`, que ya existe. `TraceIdFilter` lo pone en el MDC, viaja en el header
`X-Correlation-Id` y en cada `ErrorResponseDTO`. El cliente reporta el id; quien opera busca por él
en los logs y encuentra el mensaje técnico completo. No hace falta inventar canal.

Para el catálogo hay además un segundo canal ya previsto: `IndicadorSaludCatalogo` (RD-07), que
expone modo y causa sin pasar por una petición fallida.

#### Encaje con el catálogo

`CatalogoMensajesException` es el caso de libro:

| | Texto |
|---|---|
| Usuario | `MensajesRespaldo.Usuario.SERVICIO_NO_DISPONIBLE` |
| Técnico | `MensajesRespaldo.Tecnico.REDIS_NO_DISPONIBLE.formatted(host)` |

Y ahí está lo que hace que esto valga la pena más allá del catálogo: **la división
`Usuario` / `Tecnico` de §4.2 deja de ser un invento local del catálogo y pasa a ser una propiedad
de toda la jerarquía de excepciones.** El catálogo la usa; no la posee.

#### Riesgo

Un campo opcional que nadie rellena. Se mitiga por dos vías: el handler degrada a `message` cuando
es `null` (no hay regresión posible), y `InfrastructureException` documenta la forma de 3 argumentos
como la normal. No se propone hacerlo obligatorio: forzarlo en las cinco bases rompería las 35
excepciones existentes por un beneficio que solo aplica a una.

#### Alcance

**Va en fase propia (0c), no mezclada con Redis.** Toca `shared:exception`, que es shared kernel de
los 9 contextos; el catálogo es su primer consumidor, no su justificación.

### 2.3 Nomenclatura

`shared:message` quedó en español tras `5ae1f19`. El patrón observado:

- **Concepto en español al frente:** `CatalogoMensajes`, `ClaveMensaje`, `PaquetesMensajes`, `Mensajes`
- **Calificador técnico al final, cuando nombra la tecnología:** `CatalogoMensajesResourceBundle`
- **Sufijos que se conservan:** `Config`, `Properties`, `Exception`, `Test`

Las clases nuevas de este plan siguen ese patrón. **Aviso de terminología:** «paquete» quedó
sobrecargado — significa *paquete Java* y también *ResourceBundle* (`PaquetesMensajes`,
`ClaveMensaje.paquete()`). En este documento se dice «paquete Java» cuando hay riesgo de confusión.

### 2.4 Paquetes Java dentro de `shared:message`

```
com.arquisoft.shared.message
├── (raíz)      CatalogoMensajes · ClaveMensaje · PaquetesMensajes · Mensajes
│               CatalogoMensajesResourceBundle · CatalogoMensajesEnCascada
├── key.*       27 enums                          ← SIN CAMBIOS
├── constant.*  Codes / Fields / Limits           ← SIN CAMBIOS
├── annotation.*ValidationKeys / FichasApiKeys    ← SIN CAMBIOS
├── respaldo    MensajesRespaldo                  ← NUEVO: texto que sobrevive sin mecanismo
├── redis       FuenteMensajesRedis · config · escucha ← NUEVO: el mecanismo remoto
└── exception   CatalogoMensajesException y derivadas  ← NUEVO
```

**Spanglish que sobrevive en el módulo** (fuera del alcance de esta HT, se señala para decidir
aparte): los paquetes Java `key`, `constant`, `annotation`; el sufijo `Key` de los 27 enums;
`ValidationKeys`, `FichasApiKeys`, `*Codes`, `*Fields`, `*Limits`. Además, CLAUDE.md:179 sigue
diciendo «English for technical suffixes» sin recoger que `shared:message` pasó a español.

---

## 3. Arquitectura: cadena de resolución

«Respaldo local» y «lanzar excepción» no se contradicen porque operan en planos distintos: que
Redis no responda es fallo de **transporte** → se degrada; que la clave no exista en ninguna parte
es fallo de **contrato** → se lanza.

```
obtener(ClaveMensaje clave)
   │
   ├─ 0. Validación de clave
   │        nula · clave() en blanco · paquete() no registrado
   │        → ClaveMensajeInvalidaException
   │
   ├─ 1. Instantánea en memoria, hidratada desde Redis   ← propagación en tiempo real
   ├─ 2. CatalogoMensajesResourceBundle (.properties)    ← respaldo si Redis falla
   ├─ 3. MensajesRespaldo (constantes quemadas)          ← solo claves críticas (§4)
   └─ 4. ClaveMensajeAusenteException                    ← el camino no feliz, ahora ruidoso
```

**RD-02 — Redis no está en el camino caliente.** `obtener()` nunca hace I/O. Lee de la instantánea
en memoria. Redis se toca en el arranque, al recibir una invalidación y en la reconciliación
periódica. Sin esto, cada mensaje de error costaría un round-trip y el catálogo dejaría de ser una
constante para volverse una dependencia de disponibilidad — lo contrario del blindaje pedido.

**RD-03 — Los `.properties` siguen siendo la línea base completa.** Redis distribuye
modificaciones; no es la fuente de verdad. La alternativa destruiría la garantía de build: si el
texto vive solo en Redis, nadie puede afirmar en compilación que toda clave tiene texto, y el nivel
4 pasaría de inalcanzable a riesgo real de producción.

Con RD-03, el nivel 4 solo se alcanza si alguien añade una constante a un enum sin añadir el texto
— justo lo que `CatalogoMensajesClavesTest` bloquea. La excepción es red de último recurso, no
comportamiento esperado.

---

## 4. Mensajes quemados: el texto que sobrevive a todo

### 4.1 Son de otra naturaleza, no una copia

Hay preguntas que el sistema debe poder responder **cuando el catálogo mismo está roto**:

- Al usuario final: *«el servicio no está disponible»*
- Al usuario técnico: *«Redis no responde y el catálogo opera en modo respaldo»*
- Al operador en el arranque: *«el catálogo no pudo inicializarse: faltan N claves»*

Son **meta-mensajes**: describen el fallo del mecanismo que los resolvería. No pueden vivir en
Redis por razones obvias, y —el punto fino— **tampoco deben vivir en los `.properties`**, porque un
fallo de paquete de mensajes (ausente, corrupto, classpath mal armado) es justamente uno de los
escenarios que tienen que describir.

Por eso no son `ClaveMensaje`. No tienen clave, ni paquete, ni resolución. Son `String` compilados
dentro del jar: el único texto cuya existencia garantiza el compilador.

Conceptualmente pertenecen al mismo grupo que `constant.*` — *lo que está obligado a ser constante*
— pero por una razón distinta (disponibilidad en runtime, no JLS §9.7.1). De ahí el paquete Java
propio, `respaldo`.

### 4.2 Qué entra y qué no

**RD-04 — `MensajesRespaldo` cubre el mínimo crítico, no un espejo del catálogo.**

```java
public final class MensajesRespaldo {

    /** Para el cliente HTTP. Genérico y sin detalle técnico (OWASP A05). */
    public static final class Usuario {
        public static final String SERVICIO_NO_DISPONIBLE =
            "El servicio no está disponible en este momento. Intente más tarde.";
        public static final String ERROR_INTERNO =
            "Ocurrió un error inesperado. Contacte al administrador con el identificador de traza.";
        public static final String NO_AUTENTICADO = "No autenticado.";
        public static final String SIN_PERMISOS   = "No tiene permisos para esta operación.";
    }

    /** Para logs, health y arranque. Nombra host, paquete y clave: es diagnóstico. */
    public static final class Tecnico {
        public static final String REDIS_NO_DISPONIBLE =
            "Catálogo: Redis no responde en %s. Operando en modo DEGRADADO sobre .properties.";
        public static final String REDIS_RESTABLECIDO =
            "Catálogo: conexión con Redis restablecida. Modo NORMAL.";
        public static final String PAQUETE_NO_CARGADO =
            "Catálogo: el paquete de mensajes '%s' no pudo cargarse de ninguna fuente.";
        public static final String CLAVES_FALTANTES =
            "Catálogo: %d clave(s) declaradas sin texto en ninguna fuente: %s";
        public static final String CLAVE_DESCARTADA =
            "Catálogo: campo '%s' del hash '%s' descartado — %s.";
        public static final String PATRON_INVALIDO =
            "Catálogo: el texto de '%s' tiene un patrón de formato inválido. Se sirve sin sustituir.";
        public static final String MODO_CRITICO =
            "Catálogo: ninguna fuente disponible. Operando solo con mensajes quemados.";
    }
}
```

**Lo que NO entra:** una copia de los 26 textos de `HttpKey`. Los `.properties` viajan dentro del
jar; si no se leen, el problema no es el catálogo sino que el artefacto está roto, y ahí
`MensajesRespaldo.Usuario` ya cubre lo necesario para responder algo coherente.

### 4.3 El handler dedicado

`GlobalAppExceptionHandler` recibe un `@ExceptionHandler(CatalogoMensajesException.class)` que
construye `ErrorResponseDTO` **sin tocar el catálogo**, con `MensajesRespaldo.Usuario` y el
`traceId` del MDC. Es el único handler del archivo que no depende de `catalogo`, y esa es su razón
de ser.

---

## 5. Redis: modelo, propagación, ingestión

### 5.1 Modelo de datos

| Clave Redis | Tipo | Contenido |
|---|---|---|
| `arquisoft:catalogo:paquete:{paquete}` | Hash | field = clave completa, value = texto |
| `arquisoft:catalogo:version` | String (contador) | `INCR` en cada modificación |
| `arquisoft:catalogo:eventos` | Canal Pub/Sub | `{paquete}` o `{paquete}\|{clave}` |
| `arquisoft:catalogo:siembra` | String (cerrojo) | `SET NX EX` para que solo una instancia siembre |

Hash y no claves sueltas: `HGETALL` hidrata un paquete entero en un round-trip, y `HSET` modifica
un texto sin tocar los demás.

### 5.2 Propagación

Pub/Sub sobre `arquisoft:catalogo:eventos` con `RedisMessageListenerContainer`. Al recibir un
evento se desaloja la entrada de la instantánea y se rehidrata.

**Riesgo estructural:** Redis Pub/Sub es *at-most-once*. Una instancia desconectada en el momento
de la publicación pierde el evento y se queda con el texto viejo indefinidamente.

Mitigación: `@Scheduled` cada 60s hace `GET` de `arquisoft:catalogo:version`; si difiere de la
versión local, rehidrata todo. Un `GET` por minuto por instancia cierra el agujero.

Redis Streams (*at-least-once*, posición por consumidor) es la alternativa robusta, pero exige
grupos de consumidores y trimming. **Recomendación: Pub/Sub + versión ahora**, Streams si 60s de
ventana resulta insuficiente.

### 5.3 Ingestión: Redis es un borde no confiable

**RD-05.** Cada field leído de un hash se valida contra `RegistroClavesMensaje` antes de entrar a
la instantánea. Se descarta con WARN si:

- No corresponde a ninguna `ClaveMensaje` declarada *(clave retirada del código, o basura)*
- Corresponde a una clave que declara **otro** paquete *(la versión Redis de las claves duplicadas)*
- El valor es nulo o vacío
- El valor tiene un patrón de formato incompatible con el original (§5.4)

El resto del paquete se hidrata normalmente. Un campo malo no puede tumbar la carga.

### 5.4 El riesgo que introduce editar textos en caliente

`formatear()` hace `patron.formatted(args)` sin guarda
(`CatalogoMensajesResourceBundle:74`). Hoy es seguro porque los patrones son estáticos y se revisan
en PR. **Con Redis editable deja de serlo:**

- Alguien añade un `%s` a un texto → `MissingFormatArgumentException` en **todos** los call sites
- Alguien escribe `"100% completado"` → `UnknownFormatConversionException`

Ninguna la detecta `CatalogoMensajesClavesTest`, que verifica existencia de texto, no validez del
patrón. Y ambas se propagarían instantáneamente a todas las instancias.

**RD-06 — doble guarda:**
1. **En ingestión:** se compara el número de conversiones del texto entrante con el del texto base
   en `.properties`. Si no coincide, se descarta el campo con WARN. La edición mala no entra.
2. **En runtime:** `formatear()` envuelve la sustitución en try/catch de `IllegalFormatException` y
   devuelve el patrón crudo con WARN. Un texto malo degrada el mensaje, no la petición.

Es la principal deuda que crea la funcionalidad, y no estaba en el requerimiento original.

### 5.5 Siembra

Si `arquisoft:catalogo:version` no existe, la instancia publica los `.properties` a Redis,
protegida por `SET arquisoft:catalogo:siembra NX EX 30`. Las demás esperan y luego hidratan.

Sin esto, un Redis vacío en el primer despliegue dejaría todo sirviendo del nivel 2 — funciona,
pero anula el propósito del ejercicio sin que nadie se entere.

---

## 6. Los cuatro modos de operación

| Modo | Fuentes vivas | Se llega… | Usuario final | Health |
|---|---|---|---|---|
| **NORMAL** | Redis + properties + respaldo | arranque correcto | mensajes normales | `UP` |
| **DEGRADADO** | properties + respaldo | Redis cae, o no arranca con `requerido=false` | mensajes normales | `UP` con `catalogo.modo=DEGRADADO` |
| **CRITICO** | solo respaldo | los `.properties` no cargan | `MensajesRespaldo.Usuario` | `DOWN` |
| **ABORTADO** | — | fail-fast en arranque | la app no acepta tráfico | proceso no levanta |

- **DEGRADADO no afecta al usuario final.** Los `.properties` contienen las 156 claves; lo único que
  se pierde es la propagación en caliente. Por eso el health lo reporta pero no marca `DOWN`: es un
  problema del operador, no del cliente.
- **CRITICO sí lo afecta**, y es donde `MensajesRespaldo` gana su existencia.
- DEGRADADO → NORMAL es automático: la reconciliación detecta que Redis volvió, rehidrata y loguea
  `REDIS_RESTABLECIDO`.

**RD-07 — `IndicadorSaludCatalogo`.** El proyecto ya tiene `spring-boot-starter-actuator`. Expone
modo, última hidratación, versión local vs. remota, claves por paquete y campos descartados. **Es
la respuesta operativa a «cómo le digo al desarrollador que Redis no funciona»**: un endpoint, no
un grep de logs.

---

## 7. Camino no feliz completo

Severidad: **A** = aborta el arranque · **D** = degrada con log · **R** = rechaza el dato · **X** = lanza excepción

### 7.1 Arranque

| # | Escenario | Detección | Comportamiento | Sev |
|---|---|---|---|---|
| 1 | Redis inalcanzable | PING falla | `requerido=true` → aborta nombrando el host<br>`requerido=false` → DEGRADADO + `Tecnico.REDIS_NO_DISPONIBLE` | A/D |
| 2 | Redis responde pero AUTH falla | excepción de autenticación | igual que #1, motivo diferenciado en el log | A/D |
| 3 | Redis vacío (primer despliegue) | `version` ausente | siembra desde `.properties` bajo cerrojo (§5.5) | — |
| 4 | Falta un paquete entero en Redis | `HGETALL` vacío | ese paquete se sirve del nivel 2 + `Tecnico.PAQUETE_NO_CARGADO` | D |
| 5 | Paquete incompleto en Redis | comparación con el registro | las ausentes caen al nivel 2, sin ruido: es el diseño | — |
| 6 | Campo de Redis sin `ClaveMensaje` declarada | registro | descarta + `Tecnico.CLAVE_DESCARTADA` | R |
| 7 | Campo en el hash del paquete equivocado | registro (clave→paquete) | descarta + WARN. **Cierra la versión Redis de las claves duplicadas** | R |
| 8 | Valor vacío o nulo | ingestión | descarta; cae al nivel 2 | R |
| 9 | Patrón de formato incompatible con el base | conteo de conversiones (§5.4) | descarta + `Tecnico.PATRON_INVALIDO` | R |
| 10 | Timeout a media hidratación | timeout de Lettuce | conserva lo hidratado, el resto al nivel 2, marca DEGRADADO | D |
| 11 | `.properties` ausente o corrupto | `MissingResourceException` | modo **CRITICO** + `Tecnico.MODO_CRITICO` | D |
| 12 | `ClaveMensaje` declarada sin texto en ninguna fuente | verificación total | **aborta listando TODAS**, no la primera | A |
| 13 | Dos instancias sembrando a la vez | `SET NX` | una siembra, la otra espera y luego hidrata | — |
| 14 | `RegistroClavesMensaje` desincronizado del código | test de §9 | falla el build, no el arranque | A |

Sobre **#12**: listar todas de una vez es la diferencia entre un arreglo y cinco reinicios.

### 7.2 Runtime

| # | Escenario | Detección | Comportamiento | Sev |
|---|---|---|---|---|
| 15 | Redis cae después de arrancar | excepción en invalidación/reconciliación | **ninguna petición falla** — la instantánea sigue en memoria. Pasa a DEGRADADO | D |
| 16 | Redis vuelve | reconciliación (60s) | rehidrata, `Tecnico.REDIS_RESTABLECIDO`, vuelve a NORMAL | — |
| 17 | Evento Pub/Sub perdido | divergencia de `version` | rehidratación completa en la siguiente reconciliación (§5.2) | D |
| 18 | Evento Pub/Sub con payload malformado | parseo | descarta el evento + WARN; no desaloja nada | R |
| 19 | Invalidación de una clave borrada en Redis | `HGET` vacío | desaloja de la instantánea; pasa a servirse del nivel 2 | — |
| 20 | `obtener(null)` | validación nivel 0 | `ClaveMensajeInvalidaException` con mensaje explícito, no NPE | X |
| 21 | `ClaveMensaje` con `paquete()` no registrado | validación nivel 0 | `ClaveMensajeInvalidaException`. Solo alcanzable con una implementación externa de la interfaz | X |
| 22 | `formatear()` con args que no casan el patrón | `IllegalFormatException` | devuelve el patrón crudo + `Tecnico.PATRON_INVALIDO`. **No propaga** (RD-06) | D |
| 23 | Clave ausente en los 3 niveles | fin de cadena | `ClaveMensajeAusenteException` | X |

### 7.3 Cómo llega cada fallo al cliente

| Origen | Status | Cuerpo |
|---|---|---|
| `ClaveMensajeAusenteException` | 503 *(hereda de `InfrastructureException`)* | `MensajesRespaldo.Usuario.SERVICIO_NO_DISPONIBLE` + `traceId` |
| `ClaveMensajeInvalidaException` | 503 | igual |
| DEGRADADO | — | transparente: el cliente no nota nada |
| CRITICO | 503 | `MensajesRespaldo.Usuario.SERVICIO_NO_DISPONIBLE` |

Ninguna de estas respuestas consulta el catálogo. Es la condición que hace el diseño consistente:
**el camino que reporta el fallo del catálogo no puede depender del catálogo.**

---

## 8. Claves duplicadas: estado verificado

| Tipo de duplicado | Estado |
|---|---|
| Misma clave en dos paquetes `.properties` | **0** — corregido en `1046505`, con test que lo bloquea |
| Mismo valor `clave()` en dos enums | **0** |
| Mismo **nombre simple** de enum en distinto paquete Java | **2 casos vivos** |
| Misma clave en dos hashes de Redis | **sin cubrir hoy** → lo cierra RD-05 / escenario #7 |

Los dos casos vivos:

```
key/app/NotificacionKey.java   ←→  key/notificaciones/NotificacionKey.java
key/fichas/UsuarioKey.java     ←→  key/usuarios/UsuarioKey.java
```

Hoy compilan porque **ningún archivo importa ambos**. Deja de ser cierto con este plan:
`RegistroClavesMensaje` enumera los 27 enums en un solo archivo y necesariamente importa los
cuatro, obligando a nombres cualificados en ese punto.

**Recomendación:** renombrar los dos de menor uso — `key.app.NotificacionKey` →
`NotificacionAppKey`, `key.fichas.UsuarioKey` → `UsuarioFichaKey`. Mecánico y de bajo riesgo, pero
**churn ajeno a esta HT**: commit separado previo (fase 0b), no mezclado.

---

## 9. El escaneo del classpath no sobrevive al jar

`CatalogoMensajesClavesTest` descubre los enums caminando el directorio de `.class` con
`Files.walk`. Dentro de un jar ejecutable **no hay directorio que caminar**: falla en producción,
que es justo donde el fail-fast debe funcionar.

**RD-08.** Se introduce `RegistroClavesMensaje` con la lista explícita de los 27 enums, más un test
que lo contrasta contra el escaneo del classpath y falla si divergen. El registro no puede quedarse
corto en silencio, y el escaneo sigue siendo la fuente de verdad en build.

---

## 10. Fail-fast en el arranque

`VerificadorArranqueCatalogo` implementa `InitializingBean`, **no** `ApplicationRunner`. La
diferencia importa: `afterPropertiesSet()` corre durante el refresh del contexto, así que un fallo
aborta antes de que el servidor acepte tráfico. `ApplicationRunner` corre con el puerto ya abierto,
y habría una ventana sirviendo peticiones con el catálogo a medias.

Secuencia: PING (#1) → siembra (§5.5) → hidratación con validación por campo (§5.3) → verificación
total contra `RegistroClavesMensaje` (#12) → instalación en `Mensajes` → suscripción al canal.

**RD-09.** `Mensajes.instalar()` se llama con el catálogo en cascada al final de la secuencia, y se
afirma que la instancia instalada es esa. `Mensajes` es estático y mutable; si el orden de beans
cambiara, dominio y aplicación resolverían contra fuentes distintas sin que nada lo delate.

---

## 11. Archivos

### `shared:exception` / `shared:validation` / `shared:util` — ✅ hechos (commit `e22be0e`)

Ver §2.2. Los tres módulos existen, con `build.gradle` y README propios, y `shared:domain` los
reexpone con `api`.

### `shared:exception` — modificados (fase 0c, §2.2b)

| Ruta | Cambio |
|---|---|
| `BaseError.java` | Campo `mensajeTecnico` (nullable) + dos factorías `of(...)` nuevas. Las de 2 argumentos no cambian |
| `BaseException.java` | `getMensajeTecnico()` delegando en `error` |
| `InfrastructureException.java` | Javadoc: la forma de 3 argumentos es la normal aquí (RD-10) |
| `README.md` | Documentar la separación de audiencias |

### `shared:message` — nuevos

| Ruta | Responsabilidad |
|---|---|
| `CatalogoMensajesEnCascada.java` | La cadena de 4 niveles |
| `ValidadorClaveMensaje.java` | Validación nivel 0 |
| `RegistroClavesMensaje.java` | Registro explícito de los 27 enums (RD-08) |
| `VerificadorArranqueCatalogo.java` | Fail-fast (§10) |
| `respaldo/MensajesRespaldo.java` | Meta-mensajes quemados (§4.2) |
| `redis/FuenteMensajesRemota.java` | Puerto JDK puro — permite testear la cascada sin Redis |
| `redis/FuenteMensajesRedis.java` | `StringRedisTemplate`: HGETALL / HGET / PING / siembra |
| `redis/EscuchaInvalidacionCatalogo.java` | `MessageListener` del canal |
| `redis/IndicadorSaludCatalogo.java` | Modo, versión, campos descartados (RD-07) |
| `redis/config/CatalogoRedisConfig.java` | Beans + `RedisMessageListenerContainer` |
| `redis/config/CatalogoRedisProperties.java` | `@ConfigurationProperties("arquisoft.catalogo.redis")` |
| `exception/CatalogoMensajesException.java` | Base — `extends InfrastructureException` |
| `exception/ClaveMensajeAusenteException.java` | Nivel 4 |
| `exception/ClaveMensajeInvalidaException.java` | Nivel 0 |

### `shared:message` — modificados

| Ruta | Cambio |
|---|---|
| `build.gradle` | `implementation` de spring-data-redis, spring-context, actuator, `project(':shared:exception')`. Actualizar el comentario de cabecera, que aún afirma «no tiene dependencias externas» |
| `CatalogoMensajesResourceBundle.java` | Cachear **solo aciertos** — hoy `:65` cachea también el marcador `??clave??` sin desalojo posible, que con fuente mutable congela un fallo transitorio para siempre. Guarda de `IllegalFormatException` en `formatear` (RD-06). Exponer `clavesDe(String paquete)` para la siembra |
| `PaquetesMensajes.java` | `DISTRIBUIBLES` (los 5 elegibles). Corregir el Javadoc de `TODOS`, que aún describe la resolución por recorrido secuencial sustituida en `1046505` |
| `constant/AppCodes.java` | Clase anidada `Catalogo`: `CLAVE_AUSENTE`, `CLAVE_INVALIDA`, `ARRANQUE_INCOMPLETO`, `NO_DISPONIBLE` |
| `README.md` | Documentar cascada, modos y modelo Redis |

### `shared:web` — modificados

| Ruta | Cambio |
|---|---|
| `config/CatalogoMensajesConfig.java` | `@ConditionalOnMissingBean(CatalogoMensajes.class)`. Sin esto, dos beans del mismo tipo hacen ambigua la inyección en 40+ clases. Con esto, los 14 `@WebMvcTest` que ya lo importan siguen funcionando **sin tocarlos** |
| `exception/GlobalAppExceptionHandler.java` | `@ExceptionHandler(CatalogoMensajesException.class)` que no consulta el catálogo (§4.3). En `handleBaseException`, loguear `getMensajeTecnico()` con degradación a `getMessage()` (§2.2b). `ErrorResponseDTO` **no** gana campo |

### Raíz

`settings.gradle` (+1 módulo) · `application.yml` + perfiles:

```yaml
arquisoft:
  catalogo:
    redis:
      habilitado: ${CATALOGO_REDIS_ENABLED:true}
      requerido: ${CATALOGO_REDIS_REQUIRED:false}   # true en prod
      prefijo: "arquisoft:catalogo"
      sembrar-al-arrancar: true
      reconciliacion: 60s
```

`habilitado: false` desactiva todo y deja el catálogo simple — la vía de escape si algo sale mal en
producción. `dev` → `requerido: false`; `prod` → `true`.

---

## 12. Casos de prueba

**`CatalogoMensajesEnCascadaTest`** (Mockito sobre `FuenteMensajesRemota`) — un test por nivel de la
cascada y por escenario #20, #21, #22, #23; más
`debeNoCachearElFallo_cuandoLaClaveApareceTrasUnaInvalidacion`.

**`ValidadorClaveMensajeTest`** — nula / `clave()` en blanco / paquete desconocido / válida.

**`RegistroClavesMensajeTest`** — `debeCoincidirConElEscaneoDelClasspath_cuandoSeComparanAmbos`.

**`MensajesRespaldoTest`** — que ninguna constante esté vacía y que los patrones con `%s` sean
formateables. Es texto que solo se usa cuando todo lo demás falló; un fallo ahí no tiene red.

**`BaseErrorTest`** (fase 0c) — `debeDegradarAMensajeUsuario_cuandoNoHayMensajeTecnico` y
`debeConservarAmbos_cuandoSeDeclaranLosDos`. Más un test sobre `GlobalAppExceptionHandler` que
afirma que **el mensaje técnico no aparece en el `ErrorResponseDTO`**: es la garantía de que RD-10
no se convierte en la fuga que pretende evitar.

**`VerificadorArranqueCatalogoTest`** — escenarios #1, #6, #7, #9, #12 (verificando que lista
*todas*).

**`FuenteMensajesRedisTest`** — mock de `StringRedisTemplate`. **No se introduce Testcontainers**:
este repo no usa `@SpringBootTest` en ningún sitio y los slices de persistencia van con H2. Meter
Docker en el build por un módulo rompe esa homogeneidad y el tiempo de CI. La verificación contra
Redis vivo va en el `docker-compose` local, manual.

**`AislamientoCatalogoMensajesTest`** — afirma que `shared:message` no expone Spring en su `api`
(RD-01). Un `implementation` cambiado a `api` rompería el aislamiento sin que nada más lo note.

---

## 13. Fases

| Fase | Contenido | Verificación |
|---|---|---|
| ~~**0a**~~ | ~~Extraer `shared:exception`~~ → resultó ser `exception` + `validation` + `util` (§2.2) | ✅ **HECHA** — commit `e22be0e`, `clean build` en verde |
| **0b** *(previa, opcional)* | Renombrar los dos enums colisionantes (§8) | `clean build` |
| **0c** *(previa, recomendada)* | `mensajeTecnico` en `BaseError` + log en el handler (§2.2b). Aditivo: las 35 excepciones existentes no se tocan | `clean build` — y verificar que ningún `ErrorResponseDTO` expone el técnico |
| **1** | `ValidadorClaveMensaje`, `RegistroClavesMensaje`, `MensajesRespaldo`, `AppCodes.Catalogo`, `PaquetesMensajes.DISTRIBUIBLES`, fix de caché y guarda de `formatear` | `clean build` |
| **2** | `CatalogoMensajesEnCascada`, excepciones, handler dedicado. **Sin Redis**: `FuenteMensajesRemota` con implementación vacía | `clean build` — cascada en niveles 2→3→4 |
| **3** | `FuenteMensajesRedis`, config, `@ConditionalOnMissingBean`, YAML | `bootRun` con y sin Redis |
| **4** | `VerificadorArranqueCatalogo` + siembra + hidratación validada | Arranque con Redis vacío, poblado y caído |
| **5** | `EscuchaInvalidacionCatalogo` + reconciliación + `IndicadorSaludCatalogo` | `HSET` + `PUBLISH` manual |
| **6** | README + ADR en el repo de documentación | — |

**Recomendación: parar tras la fase 2 y validar.** Ahí ya están los cuatro requisitos del asesor
—blindaje, validación, respaldo quemado y excepción— sin una línea de Redis escrita.

---

## 14. Riesgos y decisiones abiertas

| # | Riesgo | Estado |
|---|---|---|
| R1 | Extracción de módulos | ✅ **Cerrado** — commit `e22be0e`. Fueron tres módulos, no uno (§2.2) |
| R8 | `mensajeTecnico` opcional que nadie rellena (§2.2b) | Mitigado: el handler degrada a `message` si es `null`, y `InfrastructureException` documenta la forma de 3 argumentos. No se hace obligatorio — rompería las 35 excepciones por un beneficio que solo aplica a una |
| R9 | CLAUDE.md y AGENTS.md ubican excepciones, validación y utils en `shared:domain` (P5) | ⏳ **Pendiente** — desactualizados desde `e22be0e` |
| R2 | Quién escribe a Redis | **Decisión del usuario.** La propagación en tiempo real no vale nada sin escritor, y ese escritor es una superficie de seguridad nueva: un texto malo llega a producción al instante. ¿Endpoint administrativo con auditoría y rol `ADMINISTRADOR`, o solo por despliegue? Cerrar antes de la fase 5 |
| R3 | Patrones de formato editables (§5.4) | Mitigado con doble guarda (RD-06) |
| R4 | Pub/Sub pierde eventos | Mitigado con reconciliación por versión (§5.2) |
| R5 | Escaneo de classpath en jar | Mitigado con `RegistroClavesMensaje` (RD-08) |
| R6 | spring-data-redis en runtime del dominio | Aceptado; compile classpath limpio, verificado |
| R7 | Spanglish restante en `shared:message` (§2.3) | **Decisión del usuario** — paquetes Java `key`/`constant`/`annotation`, sufijo `Key`, `*Codes`/`*Fields`/`*Limits`. Fuera del alcance de esta HT |

---

## 15. Checklist

- [x] `shared:domain` declara con `api` los cuatro módulos hoja — **no `implementation`** (P1)
- [x] `shared:exception` y `shared:util` sin dependencias `com.arquisoft` y sin Lombok
- [ ] CLAUDE.md / AGENTS.md actualizados: excepciones, validación y utils ya no viven en `shared:domain` (R9)
- [ ] `BaseError.mensajeTecnico` no aparece en ningún `ErrorResponseDTO` (§2.2b)
- [ ] `shared:message` declara Spring como `implementation`, nunca `api`
- [ ] `:fichas:domain:dependencies --configuration compileClasspath` sin spring-data-redis
- [ ] Ningún `import org.springframework` fuera de `redis/` dentro de `shared:message`
- [ ] `key.*`, `constant.*`, `annotation.*` sin cambios
- [ ] Clases nuevas con nomenclatura en español (§2.3)
- [ ] `CatalogoMensajesConfig` con `@ConditionalOnMissingBean`
- [ ] Los 14 `@WebMvcTest` pasan **sin editar sus `@Import`**
- [ ] `@ExceptionHandler(CatalogoMensajesException.class)` no llama a `catalogo`
- [ ] `CatalogoMensajesClavesTest` en verde
- [ ] `checkstyleMain checkstyleTest` en verde · cobertura ≥ 75%
- [ ] **`./gradlew clean build`** — no incremental *(lección de `1046505`: tras mover o borrar clases, el build incremental resuelve contra `.class` obsoletos y oculta imports rotos)*
- [ ] `bootRun` verificado: Redis vacío · poblado · caído · caído a mitad de operación
- [ ] Health expone el modo correcto en los 4 estados

---

## 16. Trazabilidad

| Requisito | Dónde |
|---|---|
| Redis como caché de distribución | §3, §5.1 |
| Propagación en tiempo real | §5.2 — Pub/Sub + reconciliación por versión |
| Mantener enums y constantes actuales | §1, §2.4 — `key.*`, `constant.*`, `annotation.*` intactos |
| Alojar Redis en `shared:message`, sin módulo nuevo | §2.1 — verificado empíricamente |
| Reutilizar la jerarquía de excepciones existente | §2.2 — extracción ✅ hecha (`e22be0e`); `extends InfrastructureException` |
| Mensaje de usuario y técnico en las excepciones base | §2.2b — `BaseError.mensajeTecnico`, aditivo; el handler lo loguea y **no** lo expone en la respuesta |
| Nomenclatura en español | §2.3 — patrón derivado de `5ae1f19`, aplicado a las 14 clases nuevas |
| Fail-fast con mensajes mínimos al arrancar | §10 — `InitializingBean`, verificación total |
| Validar la clave antes de resolverla | §3 nivel 0, RD-05 — el borde real es Redis, no Java (§1.1) |
| Mensajes quemados para errores de catálogo y configuración | §4 — `MensajesRespaldo`, dos audiencias |
| Cómo decir «no hay acceso» sin catálogo | §4.2 `Usuario.*` + §4.3 handler dedicado + §6 modo CRITICO |
| Cómo decir «Redis no funciona» al técnico | §4.2 `Tecnico.*` + RD-07 `IndicadorSaludCatalogo` |
| Claves duplicadas | §8 — el hueco vivo es el de Redis, lo cierra RD-05 |
| Nulls | §1.1, §7.2 #20 |
| Camino no feliz completo | §6 (4 modos) + §7 (23 escenarios) |
