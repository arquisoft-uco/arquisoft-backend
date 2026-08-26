---
name: arquisoft-arquitectura
description: Arquitectura hexagonal + DDD + CQRS real de Arquisoft Backend — capas, paquetes, convención de sufijos, puertos, eventos y aislamiento CQRS. Cargar antes de planificar, implementar, testear o validar cualquier HU/HT. El contexto de referencia es siempre fichas/fichaperfil.
---

# Skill: arquisoft-arquitectura

Fuente de verdad concisa para agentes. Detalle extendido en `docs/ARQUITECTURA_Y_ESTRUCTURA.md` y
`CLAUDE.md` (rutas relativas a la raíz del repo). Para nomenclatura, validación, mensajes,
excepciones, Checkstyle y testing, ver la skill `arquisoft-estandares`.

**Regla de esta skill:** ningún ejemplo se pega como bloque de código. Cada fila apunta al archivo
real de `fichas` — el único contexto de negocio completo del proyecto y el patrón a copiar. Ábrelo
con `Read` cuando necesites el detalle exacto.

Los otros tres contextos con código ya se alinearon. Cada uno sirve de referencia para algo distinto,
y cada uno tiene un límite que hay que conocer antes de copiar:

- **`seguridad`** — referencia del paquete `command/result/` (+ su `mapper/`) y del layout de
  excepciones por capa dentro del slice. Controllers partidos uno por acción, DTO de request `record`
  desnudos con `RequestMapper`, `AppLogger`. *Límite:* no tiene base de datos (Keycloak + Redis), así
  que ahí no hay `JpaEntity`, `Flyway` ni `@Transactional`; y sus cuatro `*ResponseDTO` son clases
  Lombok en vez de `record` — ese detalle no se copia.
- **`notificaciones`** — referencia del `Consumer` AMQP y, sobre todo, del comando **sin `Validator`**:
  su única consulta es un corte de idempotencia resuelto con `Finder` + `if/return`, no con una `Rule`.
- **`usuarios`** — referencia de un flujo de comando completo y correcto:
  `CrearUsuarioController` → `CrearUsuarioInteractor` (`@Transactional`) → `UseCase` → `Finder` →
  `Validator` puro → `Rule` → `OutputPort` que habla `Entity`. *Límite, y es grande:* **el flujo no
  funciona**. `UsuarioCommandOutputAdapter` está inerte a propósito — solo loguea, no escribe, y por
  eso no existen `UsuarioJpaEntity`/`UsuarioJpaMapper`/`UsuarioCommandRepository`. Consecuencia
  concreta: `existePorEmail` siempre devuelve `false`, así que `UsuarioEmailUnicoRule` no se dispara
  nunca. Copia su **forma**, no su comportamiento, y no lo cites como prueba de que un endpoint
  funciona. Le queda además una desviación real: `CrearUsuarioRequestDTO` con anotaciones Jakarta y
  `toCommand()` propio, y un `CrearUsuarioCommand` sin fábrica `crear(...)` — nada valida el formato.

## Dirección de dependencias (no negociable)

`domain ← application ← infrastructure`. Los 9 bounded contexts (`seguridad`, `usuarios`, `fichas`,
`notificaciones` con implementación real; `proyectos`, `artefactos`, `repositorio_artefactos`,
`entregables`, `evaluaciones` en scaffolding) **nunca** se importan entre sí — solo se comunican vía
eventos de dominio en RabbitMQ (`shared:amqp`).

Dentro de un contexto sí hay tráfico entre features (`fichaperfil` consulta `asesorficha`), pero
siempre a través del **puerto de application** de la otra feature, nunca de su `domain/` ni de su
adaptador.

## Estructura de una feature — el árbol real de `fichaperfil`

Rutas abreviadas desde `fichas/{capa}/src/main/java/com/arquisoft/fichas/{capa}/fichaperfil/`.

### domain

| Paquete | Qué vive ahí | Ejemplo real |
|---|---|---|
| `{feature}/` (directo, sin subcarpeta) | Aggregate root, sufijo `Domain`, Notification Pattern (`VACIO`, `esVacio()`, setters privados) | `FichaPerfilDomain.java` |
| `{feature}/` — **objeto de acción** | Nominalización del verbo cuando la acción arrastra más que el agregado; vive al lado del agregado, sin subpaquete | `RegistroFichaPerfilDomain.java`, `CambioAsesorFichaDomain.java`, `ModificacionFichaPerfilDomain.java` |
| `{feature}/model/` | Value objects y el record de entrada de cada `Rule` | `ExistenciaAsesorFicha.java`, `DisponibilidadTituloFicha.java` |
| `{feature}/rules/` (+`impl/`) | Regla pura: sin Spring, sin Lombok, **sin dependencias de constructor** | `rules/FichaPerfilTituloUnicoRule.java` + `rules/impl/FichaPerfilTituloUnicoRuleImpl.java` |
| `{feature}/event/` | Eventos de dominio (extienden `DomainEvent`) | `event/AsesorFichaCambiadoEvent.java` |
| `{feature}/exception/` | Excepciones de dominio (→ 422) | `exception/FichaTituloDuplicadoException.java` |

### application — lado command

| Paquete | Qué vive ahí | Ejemplo real |
|---|---|---|
| `command/primaryport/interactor/` (+`impl/`) | Contrato primario, dueño de `@Transactional` | `RegistrarFichaPerfilInteractor.java` + `impl/RegistrarFichaPerfilInteractorImpl.java` |
| `command/primaryport/model/` | `Command` — `record` con factoría `crear(...)` que valida formato | `RegistrarFichaPerfilCommand.java` |
| `command/primaryport/mapper/` | `Command` → objeto de acción de dominio (existe cuando la acción no mapea al agregado) | `RegistrarFichaPerfilMapper.java` |
| `command/usecase/` (+`impl/`) | Colaborador interno — **NO** bajo `primaryport/`, sin transacción | `usecase/RegistrarFichaPerfilUseCase.java` + `usecase/impl/...UseCaseImpl.java` |
| `command/validator/` (+`impl/`) | Puro: construye sus `Rule`s con `new` en un constructor sin argumentos; sin `OutputPort`, sin `Finder`, **sin un solo `if`** | `validator/impl/RegistrarFichaPerfilValidatorImpl.java` |
| `command/finder/` (+`impl/`) | Uno por consulta; siempre devuelve valor (`Boolean`/`Long`/`Optional`), nunca lanza por "no encontrado" | `finder/impl/TituloFichaPerfilExisteFinderImpl.java` |
| `command/secondaryport/` (+`entity/`, `mapper/`) | Puerto de salida — habla `Entity`, nunca `Domain` | `FichaPerfilOutputPort.java`, `entity/FichaPerfilEntity.java`, `mapper/FichaPerfilMapper.java` |
| `command/result/` (+`mapper/`) | Salida del comando cuando **no** es `UUID` ni `void` — ver abajo | (hoy solo en `seguridad`) `auth/command/result/AutenticacionResult.java` + `result/mapper/AutenticacionResultMapper.java` |

### application — lado query

| Paquete | Qué vive ahí | Ejemplo real |
|---|---|---|
| `query/primaryport/interactor/` (+`impl/`) | `@Transactional(readOnly = true, transactionManager = "fichasTransactionManager")` | `ConsultarFichasPerfilInteractorImpl.java` |
| `query/primaryport/model/` | `{Consult}{Entidad}Query` — **solo** si la consulta trae entrada más allá del criteria (path variable, subject del JWT). Si no, el `Criteria` **es** el objeto de consulta | (hoy ninguno en `fichas`) |
| `query/usecase/` (+`impl/`) | Colaborador interno | `ConsultarFichasPerfilUseCaseImpl.java` |
| `query/criteria/` | Entrada de la consulta (filtros/orden/paginación), extiende `QueryCriteria` de `shared:query` | `FichaPerfilCriteria.java` |
| `query/readmodel/` | Proyección plana. **Nunca se serializa directo** — sin anotaciones Jackson, sin Lombok | `FichaPerfilReadModel.java` |
| `query/secondaryport/` | Puerto de lectura, retorna `PaginatedResult<ReadModel>` | `FichaPerfilQueryOutputPort.java` |

### infrastructure

| Paquete | Qué vive ahí | Ejemplo real |
|---|---|---|
| `command/primaryadapter/web/` (+`dto/`, `mapper/`) | Un `Controller` por acción — nunca varios endpoints en uno | `RegistrarFichaPerfilController.java`, `dto/RegistrarFichaPerfilRequestDTO.java`, `dto/RegistrarFichaPerfilResponseDTO.java`, `mapper/RegistrarFichaPerfilRequestMapper.java` |
| `command/primaryadapter/amqp/` | `Consumer` AMQP (extiende `AbstractEventConsumer`), payload `record` **local** | ver `notificaciones` |
| `command/secondaryadapter/entity/` (+`mapper/`, `repository/`) | JPA real + `OutputAdapter` + repo Spring Data | `entity/FichaPerfilJpaEntity.java`, `mapper/FichaPerfilJpaMapper.java`, `repository/FichaPerfilCommandOutputAdapter.java`, `repository/FichaPerfilCommandRepository.java` |
| `query/primaryadapter/web/` (+`dto/`, `mapper/`) | `Controller` de lectura + **`{Entidad}ResponseDTO` + `{Entidad}ResponseMapper`** + `RequestMapper` que arma el `Criteria` | `ConsultarFichasPerfilController.java`, `dto/FichaPerfilResponseDTO.java`, `mapper/FichaPerfilResponseMapper.java`, `mapper/ConsultarFichasPerfilRequestMapper.java` |
| `query/secondaryadapter/repository/` (+`mapper/`) | `@Subselect`/`@Immutable`/`@Synchronize`, plana; specification, sort, adapter, repo | `FichaPerfilJpaQueryEntity.java`, `FichaPerfilJpaSpecification.java`, `FichaPerfilSortMapper.java`, `FichaPerfilQueryOutputAdapter.java`, `FichaPerfilQueryRepository.java`, `mapper/FichaPerfilQueryMapper.java` |
| `{feature}/exception/` | Excepciones de infraestructura del feature (→ 503) — **dentro del slice, no a nivel de contexto** | `seguridad/infrastructure/auth/exception/ProveedorIdentidadNoDisponibleException.java` |
| `security/`, `config/`, `filter/` | Transversales del contexto | `security/FichasAuthorities.java`, `config/FichasDataSourceConfig.java` |
| `handler/` | El `@RestControllerAdvice` del contexto, si lo tiene — **nunca en `exception/`** | `seguridad/infrastructure/handler/SeguridadGlobalExceptionHandler.java` (solo `seguridad` tiene uno; `fichas` no) |
| `src/main/resources/db/migration/{contexto}/` | Migraciones Flyway del contexto — subcarpeta propia obligatoria | `db/migration/fichas/V20260504181427__crear_tablas_fichas_perfil.sql` |

## Cuando un comando devuelve un objeto: `command/result/`

Un comando normalmente devuelve `UUID` (el id de lo creado) o `void`, y entonces no necesita tipo
propio. Cuando devuelve algo más rico, ese algo es un **`{Concepto}Result`**: un `record` plano en
`application/{feature}/command/result/`, sin anotaciones, sin Lombok.

Hoy solo existe en `seguridad` (`AutenticacionResult`, `RefrescoTokenResult`,
`ValidacionTokenResult`), porque es el único contexto con comandos que devuelven algo que no es un
id. **Los contextos de negocio no lo tienen todavía, pero cuando una HU lo requiera se crea ahí con
esta misma estructura** — no se inventa una variante nueva ni se devuelve el `Domain`, el `Entity`
o el DTO desde la capa de aplicación.

La cadena completa, con `seguridad/auth` como referencia:

| Paso | Quién | Qué hace |
|---|---|---|
| 1 | `{Concepto}ResultMapper` (`command/result/mapper/`) | `final`, constructor privado, **`static toResult(...)`**. Convierte lo que devolvió el puerto secundario (`CredencialesProveedor`, de `secondaryport/model/`) en el `Result` |
| 2 | `{Accion}{Entidad}UseCaseImpl` | Es quien **llama** al `ResultMapper` y retorna el `Result` |
| 3 | `{Accion}{Entidad}Interactor` | Solo declara el tipo: `Interactor<{Accion}{Entidad}Command, {Concepto}Result>`, con su `@Transactional` |
| 4 | `{Accion}{Entidad}ResponseMapper` (`infrastructure/.../command/primaryadapter/web/mapper/`) | `static toResponse(result)` → `{Accion}{Entidad}ResponseDTO` |

El paso 2 es el que más se equivoca: como el mapper vive en `application`, tienta llamarlo desde el
`Interactor`. No — el `Interactor` solo declara el tipo y delega, igual que cuando el retorno es un
`UUID` pelado.

Cuando el resultado tiene más de una rama (encontrado / no encontrado), el mapper expone **las dos
fábricas** en vez de recibir un `Optional`: `ValidacionTokenResultMapper` tiene `toResult(identidad)`
y `toResultInvalido()`, y el use case encadena
`.map(ValidacionTokenResultMapper::toResult).orElseGet(ValidacionTokenResultMapper::toResultInvalido)`.
Así el use case sigue sin un solo `if`.

**El `Result` nunca se serializa directo**, exactamente por la misma razón que el `ReadModel` (ver
abajo): el contrato JSON vive en el `ResponseDTO`, no en el tipo de retorno de la capa de
aplicación. Por eso el lado comando tiene su propio `{Accion}{Entidad}ResponseMapper`, simétrico al
`{Entidad}ResponseMapper` del lado lectura.

Para cobertura: `*Result` y `*ResultMapper` **no** están en las exclusiones de JaCoCo (a diferencia
de `*Command` y `*ReadModel`), así que el test del use case que asserta los campos del `Result` es lo
que cubre el mapper.

Simetría que conviene tener presente: en `command/`, `primaryport/model/` es la entrada y `result/`
la salida; en `query/`, `criteria/` es la entrada y `readmodel/` la salida.

## El `ReadModel` nunca sale por HTTP

El `Controller` de lectura mapea `ReadModel` → `{Entidad}ResponseDTO` con
`{Entidad}ResponseMapper` (`final`, constructor privado, `static toResponse`). La política de
serialización (`@JsonInclude`, nombres) vive en el DTO, no en el `ReadModel` — así el contrato JSON
no puede filtrarse al tipo de retorno del puerto secundario. Paginado:
`PageResponseDTO.from(resultado.map({Entidad}ResponseMapper::toResponse))`. Ver
`ConsultarFichasPerfilController.java`.

Un `ReadModel` anidado pertenece a la feature que describe, no a la que lo compone:
`asesorficha` posee `query/readmodel/AsesorFichaReadModel` y su `ResponseDTO` sin tener `UseCase`,
`Controller` ni puerto propios.

## Convención de sufijos

`Domain · Interactor/InteractorImpl · UseCase/UseCaseImpl · Validator/ValidatorImpl ·
Rule/RuleImpl · Finder/FinderImpl · OutputPort · QueryOutputPort · Entity · JpaEntity ·
JpaQueryEntity · Command · Query · Criteria · ReadModel · Result/ResultMapper ·
RequestDTO/ResponseDTO · RequestMapper/ResponseMapper · Controller (web) · Consumer (AMQP) ·
CommandOutputAdapter · QueryOutputAdapter · SortMapper · JpaSpecification`.

Español para el concepto de negocio, inglés para el sufijo técnico. No hay sufijos en español —
`Controller`, no `Controlador`. El paquete de la feature va todo en minúsculas y sin separadores:
`fichaperfil`, nunca `fichaPerfil`.

**Cada paquete se llama como el sufijo de las clases que contiene** — `filter/` → `*Filter`,
`mapper/` → `*Mapper`, `interactor/` → `*Interactor`, `exception/` → `*Exception`. De ahí sale la
regla que más se equivoca: **un `@RestControllerAdvice` va en `handler/`, no en `exception/`**
(`shared/web/handler/GlobalAppExceptionHandler`,
`seguridad/infrastructure/handler/SeguridadGlobalExceptionHandler`). Un handler no es una excepción,
y meterlo ahí sobrecargaba el único nombre de paquete cuyo significado el resto del repo da por
sentado en ~20 sitios. `advice/` también se descartó: es jerga de Spring y la clase no se llama
`*Advice`.

## Dónde vive cada excepción: en el slice, y en la capa de su clase base

No hay `exception/` a nivel de contexto. Las tres familias viven dentro del slice vertical del
feature, y la capa la decide la clase base que extienden:

| Excepción | Capa y paquete | HTTP |
|---|---|---|
| La que lanza un `Rule` (incluye "no encontrado", duplicado, propiedad) | `domain/{feature}/exception/` | 422 |
| La que lanza la orquestación de application | `application/{feature}/exception/` | 400 |
| Fallo real de infraestructura, lo levanta un `OutputAdapter` | `infrastructure/{feature}/exception/` | 503 |

**Toda la jerarquía de un concepto va junta en una capa.** `AutenticacionException`,
`CredencialesInvalidasException` y `TokenInvalidoException` están las tres en
`seguridad/application/auth/exception/` porque las dos subclases extienden a la primera, que es
`ApplicationException`. En cambio `ProveedorIdentidadNoDisponibleException` (503, Keycloak caído, la
lanza `KeycloakAuthOutputAdapter`) baja a `seguridad/infrastructure/auth/exception/`. Una subclase en
distinta capa que su padre parte una jerarquía en dos módulos; los imports redundantes que reporta
Checkstyle al moverla son el síntoma de que estaba mal ubicada.

## Puertos: hablan `Entity`, nunca `Domain`

`domain/` no declara puertos ni hace I/O. El `OutputPort` recibe y devuelve el `record` plano
`{Entidad}Entity` (`command/secondaryport/entity/`), sin JPA ni Lombok; una relación `@ManyToOne`
viaja como el id desnudo, no como entidad anidada.

Dónde ocurre cada conversión:
- **UseCase**: `Domain → Entity` antes de llamar al puerto (`{Entidad}Mapper.toEntity`).
- **Finder**: `Entity → Domain` al volver.
- **OutputAdapter**: `Entity ↔ JpaEntity` (`{Entidad}JpaMapper`), pegado a cada llamada del repo.

## Aislamiento CQRS (regla dura)

`query/secondaryadapter` **nunca** importa nada de `command/secondaryadapter`, ni siquiera el
`JpaEntity`. El lado lectura declara su propia `{Entidad}JpaQueryEntity` con `@Subselect` (el join
resuelto en SQL → entidad **plana**, sin `@ManyToOne`), `@Immutable` y `@Synchronize({...})`.

`{Entidad}QueryRepository` extiende `QueryRepository`/`SpecificationQueryRepository` (`shared:jpa`),
**nunca `JpaRepository`**: el lado lectura no debe heredar `save`/`delete`. Los repos de escritura
sí extienden `JpaRepository`. Ver `FichaPerfilQueryRepository.java`.

El `QueryOutputAdapter` es pura delegación: `PageableMapper.toPageable(criteria, {Entidad}SortMapper::traducir)`
a la entrada y `PaginationMapper.toResult(page)` a la salida (`shared:jpa/util/`). No construye
`PageRequest`/`Sort` a mano ni captura excepciones de Spring Data para remapearlas a 4xx.

## Cuándo NO existe un paquete `query/`

Solo se crea `query/` cuando la feature tiene una **lectura real alcanzada por un `primaryport`**
(un `UseCase` con su `Controller`). Una comprobación de existencia o de estado que solo alimenta un
`Validator`/`Rule` del lado comando va en el `OutputPort` de `command/`, consumida por un `Finder`
— aunque el dato pertenezca a **otra feature**.

Ejemplo real: `RegistrarFichaPerfilUseCaseImpl` confirma que el asesor existe con
`AsesorFichaExisteFinder`, que delega en `AsesorFichaOutputPort.existePorId(...)` — el puerto de
**command** de `asesorficha`. No hay `AsesorFichaQueryOutputPort` y no debe haberlo.
`estudiante`, `representantecomite` y `evaluacionfichaperfil` no tienen paquete `query/` por lo
mismo.


## `shared:domain` vs `shared:application` — la frontera la sostiene el compilador

`shared:domain` solo tiene `DomainEvent` (`com.arquisoft.shared.events`) y `DomainRule`
(`com.arquisoft.shared.rules`). Todo lo demás vive en `shared:application`: `UseCase`/`VoidUseCase`
(`com.arquisoft.shared.usecase`), `Interactor`/`VoidInteractor` (`com.arquisoft.shared.interactor`),
`Finder` (`com.arquisoft.shared.finder`) y el puerto `EventPublisher`
(`com.arquisoft.shared.publisher`).

Antes eran un solo módulo llamado `domain`, así que `{contexto}/domain` recibía `UseCase` e
`Interactor` en su classpath y un agregado podía implementarlos sin que nada fallara. Hoy no
compila. Es la misma clase de garantía que da tener cero Spring en el classpath del dominio: "el
dominio no orquesta" pasó de convención a hecho.

Qué declara cada capa:

| Módulo | Declara | Por qué |
|---|---|---|
| `{contexto}/domain` | `shared:domain` | Trae por `api` la cadena `message`/`exception`/`validation`/`util` |
| `{contexto}/application` | `shared:application` | Trae `shared:domain` por `api` — no hace falta declararlo también |
| `{contexto}/infrastructure` | ambos | El controller inyecta el `Interactor` (`shared:application`) y el consumer AMQP toca `DomainEvent` (`shared:domain`) |

**Un `{contexto}/domain/build.gradle` nunca declara `shared:application`.** Si compilando el dominio
falta `UseCase`, `Interactor`, `Finder` o `EventPublisher`, la corrección **no** es agregar la
dependencia: es mover ese tipo a la capa de aplicación, que es donde pertenece. Agregarla devuelve
exactamente el agujero que el split cerró.

## Eventos de dominio — una sola forma

El `UseCase` inyecta el puerto `EventPublisher` (`com.arquisoft.shared.publisher`, en
`shared:application`) y publica directamente tras persistir —
`eventPublisher.publish(new AsesorFichaCambiadoEvent(...))`. Ver `CambiarAsesorFichaUseCaseImpl.java`
(fichas) y `CrearUsuarioUseCaseImpl.java` (usuarios), que siguen la misma forma.

**El agregado es una clase plana y no participa en la publicación**: no extiende ninguna clase base
para esto, no acumula eventos en memoria y no expone ningún método para emitirlos o drenarlos. Un
agregado que declare algo así no compila — el tipo base que lo permitía no existe en el repo, y el
`{contexto}/domain` ni siquiera tiene `EventPublisher` en su classpath (`shared:domain` no lo trae).
Si un plan, un ejemplo o tu memoria proponen que la entidad acumule y el use case drene, es material
viejo: la forma es una sola y es la de arriba.

**Coherencia dura:** si el plan dice "Eventos: ninguno" → no hay clases en `event/` y el `UseCase`
no inyecta `EventPublisher`. `DomainEvent` sí sigue vigente: es la clase base de cada evento
(`AsesorFichaCambiadoEvent`, `UsuarioCreadoEvent`), aporta `idEvento`/`ocurridoEn`/`tipoEvento` y
valida que `EVENT_TOPIC` tenga el formato `{contexto}.{entidad}.{accion}` — que es además la
routing key de RabbitMQ.

`reconstruir(...)` nunca publica eventos y el `CommandOutputAdapter` siempre lee con
`reconstruir(...)`, nunca con `crear(...)`. Un evento carga todo lo que su consumidor necesita
(`AsesorFichaCambiadoEvent` lleva nombre y email del asesor) para que el consumidor no tenga que
volver a consultar al productor.

La publicación está centralizada en `shared:amqp` y **nunca se crea un `{Entidad}EventPublisher`
local**. Hay dos implementaciones del puerto y no son intercambiables: `SpringModulithEventPublisher`
(`@Component @Primary`) pasa por el outbox — inserta en `event_publication` dentro de la misma
transacción del `Interactor` — y `RabbitMQEventPublisher` publica directo al broker, declarado
respaldo con `@ConditionalOnMissingBean`. El `@Primary` es lo que hace determinista cuál gana: esa
condición, sobre un `@Component` escaneado, no la garantiza Spring fuera de una autoconfiguración, y
si ganara el directo los eventos irían al broker **saltándose el outbox**, sin fila en
`event_publication` y sin atomicidad. Consecuencia práctica: una implementación nueva de
`EventPublisher` no es una extensión inocente — rompe esa garantía. El use case inyecta siempre la
**interfaz**, nunca una de las dos clases.

## Aislamiento de persistencia: una base de datos por contexto

No son schemas dentro de una base común: `init-db.sql` crea una **base por bounded context**
(`usuarios`, `fichas_perfil`, `proyectos_grado`, `notificaciones`, …). Cada
`{Contexto}DataSourceConfig` levanta su propio `DataSource`, `EntityManagerFactory`,
`TransactionManager` y bean de `Flyway`. `seguridad` no aparece: se apoya en Keycloak + Redis.

Consecuencias que se notan al escribir código:

- **Cada base tiene su propio `flyway_schema_history`.** Por eso el bean de Flyway apunta a
  `.locations("classpath:db/migration/{contexto}")` y las migraciones viven en esa subcarpeta: una
  migración suelta en `db/migration/` la recogerían todos los contextos y cada uno la aplicaría en
  su propia base.
- **`baselineOnMigrate` está en `false`** en los tres contextos con implementación. Flyway ya no
  acepta en silencio una base con objetos preexistentes ni una versión fuera de orden — falla el
  arranque, que es justo lo que se quiere para no corromper el historial.
- **La versión es un timestamp `VyyyyMMddHHmmss`** tomado al crear el archivo
  (`V20260504181427__crear_tablas_fichas_perfil.sql`), no una secuencia. Dos migraciones de la misma
  entrega se separan por un segundo. Nunca se retrocede un timestamp ni se renombra/edita una
  migración ya aplicada: se agrega otra.
- **Una FK hacia otro contexto no es posible** — son bases distintas. Se modela como tabla réplica
  local poblada por eventos AMQP (`asesor_ficha`, `estudiante` en `fichas`).
- `@Table` no lleva `schema` ni catálogo, y el SQL no prefija nombres de base: la conexión ya apunta
  a la base correcta.

## Rutas y autorización viven en constantes, no en literales

- `@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")` — placeholder de propiedad
  con default; las rutas vienen del yml. **No existe una clase `{Contexto}Routes`.**
- `@PreAuthorize(FichasAuthorities.Expresiones.HAS_FICHA_PERFIL_CREATE)` — nunca la cadena literal
  `"hasAuthority('...')"`. `FichasAuthorities` (`infrastructure/security/`) declara el client role
  crudo (para los tests) y su expresión SpEL.
- Nunca el prefijo `/api` en la ruta: ya es el `context-path` global.
