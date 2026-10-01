# shared:message — Catálogo central de mensajes

Módulo Gradle que centraliza **todos los textos del proyecto** (mensajes de error, validaciones,
logs), más las **constantes** que no pueden salir del código (códigos de error, límites, nombres de
campo).

El texto no vive aquí: vive en `catalogo/*.properties`, en la raíz del repositorio, y se carga en
Redis como paso previo del despliegue (ADR-013). Este módulo aporta el **puerto**, la **fachada** y
el **registro de claves** que el arranque necesita para comprobar que no falta ninguna. Ver
[catalogo/README.md](../../catalogo/README.md) para el procedimiento de añadir un mensaje.

Java puro: sin dependencias de Spring, Jakarta, ni ningún framework. Cualquier capa (`domain`,
`application`, `infrastructure`) de cualquier contexto puede importarlo sin violar la regla "domain
sin framework". Esa restricción es dura y estructural: `shared:domain` lo expone transitivamente con
`api`, así que **toda dependencia que se añada aquí acaba en el classpath del dominio de los cuatro
contextos** — por eso el adaptador de Redis vive en `shared:redis` y no aquí.

---

## Por qué el catálogo está partido en dos

El texto vive en `.properties`; el resto vive en constantes Java. La frontera no es de gusto,
la marca el compilador:

> **JLS §9.7.1** — el valor de un atributo de anotación debe ser una expresión constante.

El catálogo se resuelve en tiempo de ejecución, así que nada que se consuma desde una anotación
puede salir a él. De ahí el reparto:

| Qué | Dónde vive | Por qué |
|---|---|---|
| Mensajes de error, validaciones, logs | `catalogo/{contexto}.properties` | Texto puro, resuelto en runtime |
| Textos OpenAPI (`@Tag`, `@Operation`, `@ApiResponse`) | `{Contexto}ApiMessages` | Valor de anotación: tiene que ser constante. Y la especificación se congela al arrancar, así que no gana nada con estar fuera |
| Códigos de error (`FICHA_TITULO_DUPLICADO`) | `*Codes.java` | Contrato de la API: viajan en `ErrorResponseDTO.errorCode` y los asientan los tests |
| Límites (`TITULO_MAX = 100`) | `*Limits.java` | Se pasan como argumento a `ValidatorLongitud`/`ValidatorColeccion` dentro de `{Command}.crear()` — nunca en una anotación Jakarta |
| Nombres de campo (`asesorFicha`) | `*Fields.java` | Identifican el campo en `fieldErrors[]` que produce el `Command` |

> No hay una fila "textos de validación Jakarta" porque no hay ninguna anotación Jakarta que
> valide *formato* de datos en este proyecto — ni un `@UuidValido` propio ni uno de librería.
> Esa validación vive siempre en el `Command` (`{Command}.crear(...)` o `toCommand()`, según la
> convención de DTO del contexto — ver más abajo) y sus textos son mensajes normales de este
> mismo catálogo, no interpolación de Hibernate Validator. `seguridad`/`usuarios` sí usan
> `@NotBlank`/`@Email`/`@NotNull` para presencia/forma, pero hoy con el mensaje literal en la
> anotación, no resuelto contra el catálogo — es deuda preexistente, no el mecanismo descrito aquí.

---

## Estructura del módulo

```
shared/message/
├── build.gradle
├── README.md
└── src/
    ├── main/java/com/arquisoft/shared/message/
    │   ├── CatalogoMensajes.java        ← Puerto: obtener / formatear / contiene
    │   ├── Mensajes.java                ← Fachada estática: el único camino, en todas las capas
    │   ├── ClaveMensaje.java            ← clave() + parametros() (aridad del patrón)
    │   ├── ClavesCatalogo.java          ← Registro de los enums; el arranque lo recorre para el fail-fast
    │   ├── ContextosCatalogo.java       ← Los 5 prefijos de contexto
    │   ├── CategoriaMensaje.java        ← ERROR / VALIDACION / LOG / API, deducida de la clave
    │   ├── respaldo/
    │   │   ├── CatalogoMensajesRespaldo.java  ← Catálogo por defecto, sin estado ni E/S
    │   │   └── MensajesRespaldo.java          ← Texto genérico por categoría
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
    │       └── {Contexto}ApiMessages     ← Textos OpenAPI incrustados, uno por contexto (no van a Redis)
    └── testFixtures/java/…/prueba/
        ├── CatalogoMensajesPrueba.java     ← Lee los catalogo/*.properties reales
        └── InstaladorCatalogoPrueba.java   ← Los instala en la fachada al abrir la sesión de tests
```

No hay `src/main/resources`: el texto es dato de despliegue, no recurso de la aplicación.

Granularidad de las claves: **una clase por concepto** dentro del paquete de su contexto
(`key/fichas/FichaPerfilKey`, `key/fichas/EstadoFichaKey`, …), no una única clase-contenedor
por contexto con subclases anidadas — así una constante mal ubicada no obliga a tocar un
archivo gigante compartido por todo el contexto. Granularidad de los `.properties`: **un archivo por
contexto**, y ninguno más — la lista de `ContextosCatalogo.TODOS`, la del script de carga y la de
los archivos en disco son la misma, y el test lo comprueba.
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

### Todas las capas — la fachada estática

`Mensajes` es el **único** camino al catálogo, en dominio, aplicación e infraestructura por igual.
No hay bean de `CatalogoMensajes` que inyectar:

```java
@Component
@RequiredArgsConstructor
public class RegistrarFichaPerfilUseCaseImpl implements RegistrarFichaPerfilUseCase {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(RegistrarFichaPerfilCommand command) {
        // ...
        logger.info(Mensajes.obtener(FichaPerfilKey.LOG_REGISTRADA), ficha.getId());
        return ficha.getId();
    }
}
```

```java
public class TituloDuplicadoException extends DomainException {

    public TituloDuplicadoException(String titulo) {
        super(Mensajes.formatear(FichaPerfilKey.ERROR_TITULO_DUPLICADO, titulo),
              FichasCodes.FichaPerfil.FICHA_TITULO_DUPLICADO);
    }
}
```

El dominio no podría usar inyección aunque se quisiera: sus agregados, reglas y excepciones se
construyen con `new` y factorías estáticas, nunca como beans. Y en aplicación e infraestructura la
inyección resultó ser ceremonia — de los 53 puntos que recibían el catálogo, **ningún test lo
sustituyó nunca**, mientras que tener dos caminos de resolución sí produjo un fallo real: una
respuesta HTTP que mezclaba texto del catálogo con texto de respaldo en el mismo cuerpo.

El puerto sigue siendo una interfaz, así que instalar otra implementación no toca ningún punto de
llamada — `Mensajes.instalar(...)`. Esto no cierra la puerta al i18n: el locale es **por petición**,
no por componente, así que un singleton inyectado sería igual de ciego que la fachada. El mecanismo
que hace falta es un locale ambiental, como `shared:tracing` ya hace con el contexto de traza, y ese
es indiferente a fachada-o-bean porque lo lee la implementación, no quien llama.

### DTOs — el `Command` valida su propio formato, nunca una anotación de identificador

`fichas` (contexto grande, con validación pesada) deja el DTO como un record sin anotaciones y
mueve toda la validación de formato al factory estático `crear(...)` del `Command`, vía
`shared:validation` — este es el mecanismo obligatorio para cualquier campo de identificador
(`String` con forma de `UUID`), en todo contexto:

```java
public record RegistrarFichaPerfilCommand(String tituloProyecto, UUID asesorFicha, List<UUID> estudiantes) {

    public static RegistrarFichaPerfilCommand crear(String tituloProyecto, String asesorFicha, List<String> estudiantes) {
        var result = new ValidationResult();

        if (ValidatorTexto.noEnBlanco(tituloProyecto, FichasFields.FichaPerfil.TITULO,
                FichasCodes.FichaPerfil.TITULO_REQUERIDO, result)) {
            ValidatorLongitud.longitudMaxima(tituloProyecto, FichasLimits.FichaPerfil.TITULO_MAX,
                    FichasFields.FichaPerfil.TITULO, FichasCodes.FichaPerfil.TITULO_DEMASIADO_LARGO, result);
        }
        ValidatorUUID.uuidValido(asesorFicha, FichasFields.FichaPerfil.ASESOR_FICHA,
                FichasCodes.FichaPerfil.ASESOR_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RegistrarFichaPerfilCommand(tituloProyecto,
                UtilUUID.generarUUIDDesdeTexto(asesorFicha),
                estudiantes.stream().map(UtilUUID::generarUUIDDesdeTexto).toList());
    }
}
```

`ValidatorUUID.uuidValido(...)` es el único mecanismo sancionado para validar formato de
identificador — nunca `@UuidValido` (existió como anotación Jakarta en `shared:web`, se eliminó
precisamente para no dejar dos sitios donde mirar la misma regla) ni una anotación equivalente de
otra librería. `seguridad`/`usuarios` (contextos pequeños) siguen la otra convención — el DTO
lleva `@NotBlank`/`@Email`/`@NotNull` y expone su propio `toCommand()` — pero eso es válido solo
para presencia/forma de campos que **no** son identificadores; ninguno de sus DTO tiene hoy un
campo de identificador, y el día que lo tenga, el formato se valida igual: dentro de `toCommand()`
llamando a `ValidatorUUID.uuidValido(...)`, no con una anotación.

---

## Detalles del mecanismo

**`obtener` vs `formatear`.** `formatear` sustituye parámetros con la sintaxis de
`java.util.Formatter` (`%s`, `%d`), **no** la de `MessageFormat`: los textos del proyecto llevan
comillas simples (`El campo '%s' no puede ser nulo`) que `MessageFormat` interpretaría como
carácter de escape y eliminaría. Los patrones de log se recuperan con `obtener` y conservan sus
marcadores `{}` — los sustituye SLF4J, no el catálogo.

**Aridad.** Cada clave declara cuántos parámetros lleva su patrón (`ClaveMensaje.parametros()`).
No es redundante con el propio patrón: `String.formatted` falla de forma **asimétrica** — lanza
`MissingFormatArgumentException` si faltan argumentos, y los ignora en silencio si sobran. La aridad
declarada es lo que convierte los dos casos en un fallo visible, y se comprueba en dos momentos: en
el build contra el `.properties`, y en el arranque contra lo que Redis tiene de verdad.

**Codificación.** Los `.properties` se leen siempre en UTF-8 explícito, tanto desde el script de
carga (`LC_ALL=C.UTF-8`) como desde Java (`InputStreamReader` con `StandardCharsets.UTF_8`). Sin
eso, las tildes llegan corruptas.

**Clave ausente.** Nunca lanza y **nunca expone la clave** al cliente: devuelve el texto genérico
que corresponde a la categoría de la clave (`CategoriaMensaje`, deducida del cuarto segmento). Un
texto mal referenciado degrada el mensaje, no tumba la petición. En el arranque, en cambio, es
fail-fast: si a Redis le falta una clave declarada, la aplicación no levanta.

**Degradación en caliente.** Si Redis cae con la aplicación ya arrancada, `CatalogoMensajesRedis`
(en `shared:redis`) sirve desde su caché en memoria, marca el estado en `/actuator/health` y recarga
solo cuando el monitor detecta que Redis volvió.

---

## Red de seguridad

Externalizar texto cuesta la verificación del compilador: una clave mal escrita ya no rompe el
build. `CatalogoCargaTest` devuelve esa garantía y **falla el build** si:

- una constante de `*Key` no resuelve a ningún texto del catálogo;
- un texto del `.properties` queda huérfano, sin constante que lo referencie;
- la aridad declarada no coincide con los parámetros que el patrón lleva de verdad;
- una misma clave está declarada en dos archivos (el script emite un `SET` por par, así que el
  último ganaría en silencio);
- un enum de `key/` no está registrado en `ClavesCatalogo.ENUMS` — y por tanto el arranque no
  pediría sus claves a Redis, dejándolas pasar sin texto.

Los enums se descubren **escaneando el árbol de fuentes**, no listándolos a mano: la lista manual
está en producción porque el arranque la necesita, y este escaneo es precisamente lo que garantiza
que esté completa.

Ese test es la razón por la que se puede confiar en el catálogo, y es la compuerta barata: detecta
en segundos lo que en despliegue sería un arranque fallido.

---

## Añadir un contexto al catálogo

1. Crear `catalogo/{contexto}.properties` en la raíz del repositorio.
2. Añadir su prefijo a `ContextosCatalogo.TODOS` y a `CONTEXTOS` en `catalogo/cargar.sh`.
3. Crear `key/{contexto}/{Concepto}Key.java` (una por concepto, no una sola para todo el
   contexto) y `constant/{Contexto}Codes.java` (códigos de error, anidando una clase interna
   por objeto).
4. Registrar cada nuevo enum de claves en `ClavesCatalogo.ENUMS`.
5. Ejecutar `./gradlew :shared:message:test`.

Para añadir un mensaje a un contexto que ya existe, ver [catalogo/README.md](../../catalogo/README.md).

---

## Pruebas

No hace falta hacer nada: `InstaladorCatalogoPrueba` instala el catálogo real en la fachada al abrir
la sesión de tests, vía `ServiceLoader` del JUnit Platform. Cualquier test de cualquier módulo
afirma sobre el **texto de verdad**, sin Redis, sin contexto de Spring y sin anotación propia.

La fuente es la misma que carga el despliegue: Gradle copia los `catalogo/*.properties` a los
recursos del artefacto de test fixtures de este módulo, así que no hay segunda copia de los datos y
las dos vistas no pueden divergir.

No hay bean que importar en los slices `@WebMvcTest`, ni campo `CatalogoMensajes` que declarar en
los tests con `@InjectMocks`. Para afirmar sobre un texto de error de formato producido por un
`Command` (por ejemplo el que levanta `ValidatorUUID.uuidValido(...)` o `ValidatorTexto.noEnBlanco(...)`
desde `shared:validation`), resolverlo desde el catálogo en lugar de repetirlo:

```java
.value(Mensajes.formatear(ValidadorKey.NO_EN_BLANCO, FichasFields.FichaPerfil.ASESOR_FICHA))
```

Y para las excepciones de validación acumulada, afirmar sobre la **estructura** en vez de sobre el
texto renderizado, que es más resistente a un cambio de redacción:

```java
assertThat(excepcion.getValidationResult().tieneErroresDeCampo(FichasFields.FichaPerfil.TITULO)).isTrue();
```
