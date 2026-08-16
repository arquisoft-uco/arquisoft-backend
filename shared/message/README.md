# shared:message — Catálogo central de mensajes

Módulo Gradle que centraliza **todos los textos del proyecto** (mensajes de error,
validaciones, logs) en archivos `.properties` resueltos por `ResourceBundle`, más las
**constantes** que no pueden salir del código (códigos de error, límites, nombres de campo).

Java puro: sin dependencias de Spring, Jakarta, ni ningún framework. Cualquier capa
(`domain`, `application`, `infrastructure`) de cualquier contexto puede importarlo sin violar
la regla "domain sin framework".

Se expone transitivamente a través de `shared:domain` (configuración `api` de java-library),
por lo que todos los módulos del proyecto lo tienen disponible sin declarar la dependencia.

---

## Por qué el catálogo está partido en dos

El texto vive en `.properties`; el resto vive en constantes Java. La frontera no es de gusto,
la marca el compilador:

> **JLS §9.7.1** — el valor de un atributo de anotación debe ser una expresión constante.

Un `ResourceBundle` se resuelve en tiempo de ejecución, así que nada que se consuma desde una
anotación puede salir al bundle. De ahí el reparto:

| Qué | Dónde vive | Por qué |
|---|---|---|
| Mensajes de error, logs | `messages/*.properties` | Texto puro, resuelto en runtime |
| Textos de validación Jakarta | `ValidationMessages.properties` | Los resuelve Hibernate Validator vía `message="{clave}"` |
| Textos OpenAPI (`@Operation`, `@ApiResponse`) | `messages/fichas-api.properties` | springdoc resuelve `${clave}` contra el `Environment` |
| Códigos de error (`FICHA_TITULO_DUPLICADO`) | `*Codes.java` | Contrato de la API: viajan en `ErrorResponseDTO.errorCode` y los asientan los tests |
| Límites (`TITULO_MAX = 100`) | `*Limits.java` | Se usan en `@Size(max = …)` — imposible externalizar |
| Nombres de campo (`asesorFicha`) | `*Fields.java` | Identifican el campo en `fieldErrors[]`; también se usan en anotaciones |
| Textos de `@Tag` | `FichasApiKeys.TAG_*` | springdoc **no** resuelve `${…}` en `@Tag` (verificado en 2.8.8) |

---

## Estructura del módulo

```
shared/message/
├── build.gradle
├── README.md
└── src/main/
    ├── java/com/arquisoft/shared/message/
    │   ├── CatalogoMensajes.java               ← Puerto: obtener / formatear / contiene
    │   ├── CatalogoMensajesResourceBundle.java ← Implementación sobre ResourceBundle
    │   ├── Mensajes.java                       ← Fachada estática (solo para dominio)
    │   ├── PaquetesMensajes.java                ← Registro de bundles
    │   ├── constant/
    │   │   ├── AppCodes, FichasCodes, NotificacionesCodes, SeguridadCodes, UsuariosCodes
    │   │   └── FichasFields, FichasLimits, NotificacionesFields, NotificacionesLimits
    │   ├── key/{contexto}/{Concepto}Key.java   ← una clase por concepto, no una por contexto
    │   │   ├── app/       → ConsultaKey, MensajeriaKey, AlmacenamientoKey, PaginacionKey, ValidadorKey, HttpKey, NotificacionKey
    │   │   ├── fichas/    → FichaPerfilKey, EstadoFichaKey, EstadoFichaPerfilKey, ItemFichaPerfilKey, EstudianteKey, EstudianteFichaPerfilKey, EvaluacionFichaPerfilKey, EstadoEvaluacionFichaKey, RepresentanteComiteKey, UsuarioKey, MinioGuiaKey
    │   │   ├── seguridad/ → AutenticacionKey, CredencialesKey, IdentidadKey, IniciarSesionKey, LimiteSolicitudesKey, RolKey, SesionKey, TokenKey, TokenInvalidadoKey, ConfiguracionKey
    │   │   ├── notificaciones/ → NotificacionKey, ConsumidorKey, PlantillaKey
    │   │   └── usuarios/  → UsuarioKey
    │   └── annotation/
    │       ├── ValidationKeys                 ← Referencias "{clave}" para Jakarta
    │       └── FichasApiKeys                  ← Referencias "${clave}" para springdoc
    └── resources/
        ├── ValidationMessages.properties      ← Lo lee Hibernate Validator
        └── messages/
            ├── app.properties, fichas.properties, fichas-api.properties
            ├── seguridad.properties, usuarios.properties, notificaciones.properties
```

Granularidad de las claves: **una clase por concepto** dentro del paquete de su contexto
(`key/fichas/FichaPerfilKey`, `key/fichas/EstadoFichaKey`, …), no una única clase-contenedor
por contexto con subclases anidadas — así una constante mal ubicada no obliga a tocar un
archivo gigante compartido por todo el contexto. Granularidad de los `.properties`: **un
archivo por contexto**, más uno extra cuando el volumen lo justifica (`fichas-api`, que no
comparte ciclo de vida con los mensajes de negocio de `fichas`).

---

## Esquema de claves

```
contexto.capa.objeto.tipo.descripcion
```

| Segmento | Valores |
|---|---|
| `contexto` | `app`, `fichas`, `seguridad`, `usuarios`, … |
| `capa` | `dominio`, `aplicacion`, `infraestructura` |
| `objeto` | agregado o componente (`fichaperfil`, `ratelimit`, `validador`) |
| `tipo` | `error`, `log`, `validacion`, `api` |
| `descripcion` | kebab-case |

```properties
fichas.dominio.fichaperfil.error.titulo-duplicado=El título ya existe: %s
fichas.aplicacion.fichaperfil.log.registrada=Ficha de perfil registrada — id={}
```

Las constantes espejan el esquema — pero, a diferencia de `*Codes` (que sí anida una clase
interna por objeto, `FichasCodes.FichaPerfil.X`), cada `*Key` es un `enum` **plano** por
concepto, ya en su propio paquete `key.{contexto}`:

```java
FichaPerfilKey.ERROR_TITULO_DUPLICADO   // com.arquisoft.shared.message.key.fichas.FichaPerfilKey
FichaPerfilKey.LOG_REGISTRADA
```

---

## Cómo se consume

### Aplicación e infraestructura — inyección

`CatalogoMensajes` es un bean (`CatalogoMensajesConfig`, en `shared:web`). Se inyecta por
constructor como cualquier otro colaborador:

```java
@Component
@RequiredArgsConstructor
public class RegistrarFichaPerfilUseCaseImpl implements RegistrarFichaPerfilUseCase {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final AppLogger logger;
    private final CatalogoMensajes catalogo;

    @Override
    public UUID ejecutar(RegistrarFichaPerfilCommand command) {
        // ...
        logger.info(catalogo.obtener(FichaPerfilKey.LOG_REGISTRADA), ficha.getId());
        return ficha.getId();
    }
}
```

### Dominio — fachada estática

Los agregados, reglas y excepciones se construyen con factorías estáticas y `new`, nunca como
beans: no hay punto de inyección. Para ese caso existe `Mensajes`, que delega en la **misma
instancia** que recibe el resto de capas:

```java
public class TituloDuplicadoException extends DomainException {

    public TituloDuplicadoException(String titulo) {
        super(Mensajes.formatear(FichaPerfilKey.ERROR_TITULO_DUPLICADO, titulo),
              FichasCodes.FichaPerfil.FICHA_TITULO_DUPLICADO);
    }
}
```

### DTOs — interpolación nativa de Jakarta

```java
public record RegistrarFichaPerfilRequestDTO(

        @NotBlank(message = ValidationKeys.FichaPerfil.TITULO_OBLIGATORIO)
        @Size(max = FichasLimits.FichaPerfil.TITULO_MAX,
              message = ValidationKeys.FichaPerfil.TITULO_MAXIMO)
        String tituloProyecto) { }
```

El número del mensaje sale del propio `{max}` de la restricción, así que cambiar
`FichasLimits.FichaPerfil.TITULO_MAX` actualiza también el texto que ve el usuario.

---

## Detalles del mecanismo

**`obtener` vs `formatear`.** `formatear` sustituye parámetros con la sintaxis de
`java.util.Formatter` (`%s`, `%d`), **no** la de `MessageFormat`: los textos del proyecto llevan
comillas simples (`El campo '%s' no puede ser nulo`) que `MessageFormat` interpretaría como
carácter de escape y eliminaría. Los patrones de log se recuperan con `obtener` y conservan sus
marcadores `{}` — los sustituye SLF4J, no el catálogo.

**Codificación.** Desde Java 9 `ResourceBundle` lee los `.properties` en UTF-8, así que los
mensajes conservan tildes y eñes sin secuencias `\uXXXX`. El `@PropertySource` de springdoc sí
necesita `encoding = "UTF-8"` explícito, porque Spring asume ISO-8859-1.

**Locale.** Se fija `Locale.ROOT` para que la resolución no dependa de la configuración regional
de la máquina. Añadir traducciones es cuestión de crear `app_en.properties` y pasar otro locale.

**Clave ausente.** Devuelve el marcador `??clave??` en lugar de lanzar: un texto mal referenciado
degrada el mensaje, no tumba la petición.

---

## Red de seguridad

Externalizar texto cuesta la verificación del compilador: una clave mal escrita ya no rompe el
build. `CatalogoMensajesClavesTest` devuelve esa garantía y **falla el build** si:

- una constante de `*Keys` / `ValidationKeys` / `FichasApiKeys` no resuelve a ningún texto;
- un texto del `.properties` queda huérfano, sin constante que lo referencie.

Ese test es la razón por la que se puede confiar en el catálogo. Si se añade una clave, hay que
añadir su constante — y al revés.

---

## Añadir un contexto al catálogo

1. Crear `src/main/resources/messages/{contexto}.properties`.
2. Registrar su base name en `PaquetesMensajes.TODOS`.
3. Crear `key/{contexto}/{Concepto}Key.java` (una por concepto, no una sola para todo el
   contexto) y `constant/{Contexto}Codes.java` (códigos de error, anidando una clase interna
   por objeto).
4. Añadir cada nueva clase de claves a `CLASES_DE_CLAVES` en `CatalogoMensajesClavesTest`.
5. Ejecutar `./gradlew :shared:message:test`.

---

## Pruebas

En pruebas unitarias con `@InjectMocks`, usar el catálogo **real**, no un mock: varios mensajes
acaban en la excepción o en el resultado, y un mock los dejaría en `null`.

```java
@Spy
private CatalogoMensajes catalogo = CatalogoMensajesResourceBundle.porDefecto();
```

En slices `@WebMvcTest` hay que importar el bean, porque `GlobalAppExceptionHandler` lo recibe
por constructor:

```java
@Import({GlobalAppExceptionHandler.class, CatalogoMensajesConfig.class})
```

Para afirmar sobre un texto de validación Jakarta, resolverlo desde el catálogo en lugar de
repetirlo:

```java
.value(Mensajes.obtener(ValidationKeys.sinLlaves(ValidationKeys.FichaPerfil.ASESOR_OBLIGATORIO)))
```
