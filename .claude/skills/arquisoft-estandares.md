---
name: arquisoft-estandares
description: Estándares de código, convenciones y buenas prácticas de Arquisoft Backend — nomenclatura, validación, mensajes, excepciones, Checkstyle, testing y git. Cargar junto con arquisoft-arquitectura antes de implementar, testear o validar cualquier HU/HT.
---

# Skill: arquisoft-estandares

Complementa a `arquisoft-arquitectura` (esa skill cubre capas y paquetes; esta cubre reglas de
código transversales). Detalle profundo en [`../../CLAUDE.md`](../../CLAUDE.md) (ruta relativa a
la raíz del repo). Cada regla referencia un archivo real de `fichas/.../fichaperfil/` en vez de un
snippet — ábrelo con `Read` si necesitas el código exacto.

## Notification Pattern y orden de validación

Los invariantes de un aggregate se acumulan con `ValidationResult` (`addError` +
`lanzarSiTieneErrores`), nunca con `if/throw` disperso ni una excepción por invariante. Ver
`fichas/domain/.../fichaperfil/FichaPerfilDomain.java` (método `crear`).

Orden obligatorio: **1) integridad del dato** (formato/longitud/duplicados en el payload) →
**2) existencia/unicidad en BD** → **3) reglas de negocio del agregado**. Nunca se consulta la BD
sobre un dato cuya integridad no se validó primero.

## Validators, Rules, Finders — quién hace qué

| Componente | Es puro (sin I/O) | Puede lanzar | Ejemplo real |
|---|---|---|---|
| `Validator` | Sí — solo inyecta `Rule`s, nunca `OutputPort`/`Finder`, **cero `if`** | No decide, solo orquesta | `fichas/application/.../fichaperfil/command/validator/impl/RegistrarFichaPerfilValidatorImpl.java` |
| `Rule` | Sí — sin Spring, sin Lombok, sin dependencias de constructor | Sí, sobre un record ya cargado con el dato | `fichas/domain/.../fichaperfil/rules/impl/FichaPerfilTituloUnicoRuleImpl.java` |
| `Finder` | No — delega en un `OutputPort` | Nunca por "no encontrado" (`Boolean`/`Long`/`Optional`) | `fichas/application/.../fichaperfil/command/finder/impl/FichaPerfilFinderImpl.java` |

El I/O de un comando vive entero en el `UseCase`: los `Finder`s consultan todo el estado, el
`Validator` orquesta las `Rule`s con lo ya consultado, el `OutputPort` persiste.

## Identificadores y DTOs

Los IDs en el body HTTP llegan como `String`, nunca `UUID` tipado. Su formato **nunca** se valida
con una anotación Jakarta — siempre en el `Command.crear(...)` vía `ValidatorUUID.uuidValido(...)`
(`shared:validation`). Ver `fichas/application/.../fichaperfil/command/primaryport/model/RegistrarFichaPerfilCommand.java`.

Dos convenciones de DTO coexisten (no mezclar dentro de un mismo contexto):
- Contextos pequeños (`seguridad`, `usuarios`): el DTO lleva `@NotBlank`/`@NotNull` y su propio `toCommand()`.
- Contextos grandes (`fichas`): el DTO es un record sin anotaciones; un `RequestMapper` externo
  llama a `Command.crear(...)`. Ver `fichas/infrastructure/.../fichaperfil/command/primaryadapter/web/mapper/RegistrarFichaPerfilRequestMapper.java`.

## Catálogo de mensajes (`shared:message`)

Ningún string literal de error/log/validación va embebido en código de producción. Constantes en
`{Contexto}Codes`/`{Contexto}Fields`/`{Contexto}Limits`/`{Contexto}Messages` (`shared:message`).
Ver el uso real en `FichaPerfilDomain.crear(...)` (`FichasFields`/`FichasCodes`/`FichasLimits`).

## Excepciones (4 bases, todas en `com.arquisoft.shared.exception`)

| Base | HTTP | Cuándo | Dónde vive |
|---|---|---|---|
| `DomainException` | 422 | Invariante de dominio, "no encontrado", duplicado, propiedad | `domain/{feature}/exception/` |
| `DomainValidationException` | 422 + `fieldErrors[]` | Notification Pattern con varios errores | `domain/{feature}/` (vía `ValidationResult`) |
| `ApplicationException` | 400 | Orquestación de application (filtros inválidos, formato de query) | `application/{feature}/exception/` |
| `InfrastructureException` | 503 | Fallo real de infraestructura (BD caída, timeout) | levantada por los `OutputAdapter` |

Nunca `RuntimeException` directa. Constructor `super(message, errorCode)` — ambos `String`.

## Checkstyle (obligatorio en CI — `config/checkstyle/checkstyle.xml`)

Línea máx. 150 caracteres · archivo máx. 500 líneas (warning) · método máx. 60 líneas (warning) ·
máx. 7 parámetros (warning) · sin tabs · sin wildcard imports · PascalCase tipos/camelCase
métodos-campos/UPPER_SNAKE constantes · `_` permitido solo en nombres de test.

## Testing

JUnit 6 + Mockito + AssertJ, patrón AAA, nombre `debeHacerAlgo_cuandoCondicion()`.

- Unitario: `@ExtendWith(MockitoExtension.class)`, sin contexto Spring.
- Repositorio: `@DataJpaTest` + H2. **`@SpringBootTest` no se usa en ningún test de este repo.**
- Controller: `@WebMvcTest` + `@Import(GlobalAppExceptionHandler.class)`, mocks con `@MockitoBean`
  (no `@MockBean`), auth con `SecurityMockMvcRequestPostProcessors.jwt().authorities(...)`.
- Cobertura mínima 75% (`./gradlew jacocoTestReport`).

Ejemplos reales bajo `fichas/domain/src/test/.../fichaperfil/` y
`fichas/application/src/test/.../fichaperfil/`.

## Inyección y logging

Constructor injection con `@RequiredArgsConstructor` — nunca `@Autowired`, nunca `@Service` (todo
use case es `@Component`). Logging vía el puerto `AppLogger` (`shared:logger`) inyectado por
constructor — no `@Slf4j` (excepción conocida: `seguridad`/`usuarios`, migración pendiente).

## Git y commits

Conventional Commits en español: `feat(contexto): descripción corta`. Ramas desde `main`/`develop`
según el flujo vigente del repo: `<prefijo>/<id>-<descripcion_snake_case>` con prefijos
`feature/ fix/ refactor/ hotfix/ docs/ test/ chore/ spike/`. Ver `../../CONTRIBUTING.md`.
