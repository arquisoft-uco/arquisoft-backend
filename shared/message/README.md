# shared:message — Catálogo central de mensajes

Módulo Gradle que centraliza **todos los textos del proyecto** (mensajes de error,
validaciones, logs, códigos de error, nombres de campo, límites de negocio) en un
único lugar — implementación del patrón **Message Catalog** mediante constantes
Java type-safe.

Java puro: sin dependencias de Spring, Jakarta, ni ningún framework.
Cualquier capa (`domain`, `application`, `infrastructure`) de cualquier contexto
puede importarlo sin violar la regla "domain sin framework".

Se expone transitivamente a través de `shared:domain` (configuración `api` de
java-library), por lo que todos los módulos del proyecto lo tienen disponible
sin necesidad de declarar la dependencia explícita.

---

## Estructura del módulo

```
shared/message/
├── build.gradle
├── README.md
└── src/main/java/com/arquisoft/shared/message/
    ├── AppMessages.java                    ← Mensajes transversales (DomainValidator, paginación)
    ├── FichasMessages.java                 ← Bounded context: fichas
    ├── SeguridadMessages.java              ← Bounded context: seguridad
    ├── ProyectosMessages.java              ← Bounded context: proyectos
    ├── ArtefactosMessages.java             ← Bounded context: artefactos
    ├── RepositorioArtefactosMessages.java  ← Bounded context: repositorio_artefactos
    ├── EntregablesMessages.java            ← Bounded context: entregables
    └── EvaluacionesMessages.java           ← Bounded context: evaluaciones
```

**Una clase por bounded context.** Dentro de cada clase, una `nested static final class`
por cada aggregate / entidad / agrupador funcional del contexto.

---

## Convención de IDs por tipo de constante

Dentro de cada nested class, las constantes se agrupan en **5 secciones** con
prefijos consistentes que las hacen identificables a simple vista:

| # | Tipo | Prefijo / Patrón | Tipo Java | Ejemplo |
|---|---|---|---|---|
| 1 | **Campos** | `CAMPO_{NOMBRE}` | `String` | `CAMPO_TITULO = "tituloProyecto"` |
| 2 | **Límites** | `{NOMBRE}_{TIPO}` (sin prefijo) | `int` / `long` | `TITULO_MAX = 100` |
| 3 | **Códigos de error** | `{ENTIDAD}_{DESCRIPCIÓN}` (UPPER_SNAKE) | `String` | `FICHA_TITULO_REQUERIDO = "FICHA_TITULO_REQUERIDO"` |
| 4 | **Mensajes de error** | sin prefijo, descriptivo | `String` (con `%s`/`%d`) | `TITULO_DUPLICADO = "El título ya existe: %s"` |
| 5 | **Logs** | `LOG_{ACCIÓN}` | `String` (con `{}` SLF4J) | `LOG_REGISTRADA = "Ficha registrada — id={}"` |

### 1. Campos — `CAMPO_*`

Nombre del campo de la entidad, usado para reporting de validación
(parámetro `fieldName` de `DomainValidator`).

```java
public static final String CAMPO_TITULO          = "tituloProyecto";
public static final String CAMPO_ASESOR_FICHA_ID = "asesorFichaId";
```

- **Formato:** camelCase del nombre Java del campo.
- **Valor:** el mismo nombre del campo en la entidad — facilita correlación con DTOs.
- **No incluye prefijo del contexto** (ya está implícito por la nested class).

### 2. Límites — sin prefijo

Constantes numéricas de negocio (tamaños máximos, mínimos, umbrales).

```java
public static final int TITULO_MAX  = 100;
public static final int NOMBRE_MIN  = 3;
public static final int RETRY_MAX   = 5;
```

- **Formato:** `{CAMPO}_{TIPO_LIMITE}` en UPPER_SNAKE.
- **Tipo Java primitivo** (`int`, `long`, `double`) — no `Integer`.

### 3. Códigos de error — `{ENTIDAD}_{DESCRIPCIÓN}`

Identificadores estables, en mayúsculas, consumidos por el cliente para
disparar lógica condicional (mostrar mensaje localizado, redirigir, etc.).
Se usan en `ApplicationException.getErrorCode()`, `ValidationResult.addError()`
y en los `ErrorResponseDTO` de la capa web.

```java
public static final String FICHA_TITULO_REQUERIDO       = "FICHA_TITULO_REQUERIDO";
public static final String FICHA_TITULO_DEMASIADO_LARGO = "FICHA_TITULO_DEMASIADO_LARGO";
public static final String FICHA_TITULO_DUPLICADO       = "FICHA_TITULO_DUPLICADO";
```

- **Formato:** `{ENTIDAD}_{CAMPO}_{CONDICION}` en UPPER_SNAKE.
- **El valor literal es igual al nombre de la constante** — facilita búsqueda global.
- **Incluye el prefijo del contexto/entidad** (`FICHA_`, `USUARIO_`, etc.) porque
  estos códigos viajan al cliente y deben ser únicos globalmente.

### 4. Mensajes de error — sin prefijo, descriptivo

Texto humano que ve el usuario final. Usa marcadores `%s` (string) y `%d`
(entero) para parametrizar mediante `String.formatted(...)`.

```java
public static final String TITULO_DUPLICADO = "El título ya existe: %s";
public static final String LIMITE_EXCEDIDO  = "El campo '%s' no puede superar %d caracteres.";
```

- **Formato del nombre:** descripción concisa en UPPER_SNAKE.
- **Marcadores:** `%s`, `%d`, `%f` (formato de `String.formatted`).
- **Idioma:** español (cambiar a `.properties` si se necesita i18n a futuro).

### 5. Logs — `LOG_*`

Plantillas de mensajes para `log.info(...)`, `log.warn(...)`, `log.debug(...)`,
`log.error(...)`. Usan marcadores `{}` del formato SLF4J.

```java
public static final String LOG_REGISTRADA            = "Ficha de perfil registrada — id={}";
public static final String LOG_CONSULTA_COMPLETADA   = "Consulta fichas-perfil completada — total={}, pagina={}, tamanio={}";
public static final String LOG_ORDENAMIENTO_INVALIDO = "Campo de ordenamiento inválido: {}";
```

- **Formato del nombre:** `LOG_{ACCION}` o `LOG_{ACCION}_{DETALLE}` en UPPER_SNAKE.
- **Marcadores:** `{}` (formato SLF4J, no `%s`).
- **Incluyen el nivel implícito por convención:** los `LOG_*` son neutros — el
  nivel (info/warn/error) lo decide el llamador.

---

## Plantilla de archivo por contexto

```java
package com.arquisoft.shared.message;

public final class FichasMessages {

    private FichasMessages() {}

    // ─────────────────────────────────────────────────────────────────────────
    // FichaPerfil
    // ─────────────────────────────────────────────────────────────────────────

    public static final class FichaPerfil {

        private FichaPerfil() {}

        // Campos
        public static final String CAMPO_TITULO = "tituloProyecto";

        // Límites
        public static final int TITULO_MAX = 100;

        // Códigos de error
        public static final String FICHA_TITULO_REQUERIDO       = "FICHA_TITULO_REQUERIDO";
        public static final String FICHA_TITULO_DEMASIADO_LARGO = "FICHA_TITULO_DEMASIADO_LARGO";

        // Mensajes de error
        public static final String TITULO_DUPLICADO = "El título ya existe: %s";

        // Logs
        public static final String LOG_REGISTRADA = "Ficha de perfil registrada — id={}";
    }
}
```

Reglas obligatorias:

- La clase outer es `public final` con constructor privado vacío.
- Cada nested class es `public static final` con constructor privado vacío.
- Todas las constantes son `public static final`.
- **No se permiten JavaDocs** — los nombres y la convención son autoexplicativos.
- Las 5 secciones se separan con comentarios `// Campos`, `// Límites`, etc.
  — solo las secciones que tengan al menos una constante.

---

## Uso desde código

### Validación de dominio

```java
DomainValidator.notBlank(titulo,
        FichasMessages.FichaPerfil.CAMPO_TITULO,
        FichasMessages.FichaPerfil.FICHA_TITULO_REQUERIDO,
        result);

DomainValidator.maxLength(titulo,
        FichasMessages.FichaPerfil.TITULO_MAX,
        FichasMessages.FichaPerfil.CAMPO_TITULO,
        FichasMessages.FichaPerfil.FICHA_TITULO_DEMASIADO_LARGO,
        result);
```

### Excepción con mensaje parametrizado

```java
throw new FichaTituloDuplicadoException(
        FichasMessages.FichaPerfil.TITULO_DUPLICADO.formatted(titulo),
        FichasMessages.FichaPerfil.FICHA_TITULO_DUPLICADO);
```

### Log SLF4J

```java
log.info(FichasMessages.FichaPerfil.LOG_REGISTRADA, ficha.getId());
log.warn(FichasMessages.FichaPerfil.LOG_ORDENAMIENTO_INVALIDO, campoInvalido);
```

---

## Cuándo agregar al catálogo

Cualquier string literal que aparezca en código de producción debe vivir aquí
si entra en alguna de estas categorías:

- Mensaje de excepción (`super(...)` de cualquier `*Exception`).
- Código de error (`getErrorCode()`, `addError(...)`).
- Mensaje de validación de dominio (parámetros 2 y 3 de `DomainValidator.*`).
- Mensaje de log (`log.info`, `log.warn`, `log.error`, `log.debug`).
- Nombre de campo usado para reporting (parámetro `fieldName`).
- Límite numérico de negocio que aparece en validaciones.

**No** entran al catálogo:

- Strings técnicos de configuración (nombres de queues, colas, beans, headers HTTP).
- Constantes propias de un módulo `shared:*` que no representan mensajes
  (ej: nombres de tipos de eventos AMQP — esos viven en `shared:amqp`).
- Literales en tests (los tests pueden usar literales o importar del catálogo
  para evitar duplicación).

---

## Agregar una nueva entidad

1. Identificar el bounded context (`fichas`, `seguridad`, etc.).
2. Abrir `{Contexto}Messages.java`.
3. Agregar nested class `public static final class {NombreEntidad}` con
   constructor privado vacío.
4. Añadir las constantes en el orden de las 5 secciones, separadas por los
   comentarios `// Campos`, `// Límites`, `// Códigos de error`,
   `// Mensajes de error`, `// Logs`.

---

## Agregar un nuevo bounded context

1. Crear `{Contexto}Messages.java` en `com.arquisoft.shared.message`.
2. Estructura mínima:

   ```java
   package com.arquisoft.shared.message;

   public final class {Contexto}Messages {

       private {Contexto}Messages() {}
   }
   ```
3. Agregar nested classes a medida que aparezcan entidades con strings que
   centralizar.
