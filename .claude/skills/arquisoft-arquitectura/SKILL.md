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
con `Read` cuando necesites el detalle exacto. `seguridad`/`usuarios` son contextos pequeños con
desviaciones conocidas (`@Slf4j`, controller agregado, adapter mock): **no los uses como modelo**.

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
| `security/`, `config/`, `exception/` | Transversales del contexto | `security/FichasAuthorities.java`, `config/FichasDataSourceConfig.java` |
| `src/main/resources/db/migration/{contexto}/` | Migraciones Flyway del contexto — subcarpeta propia obligatoria | `db/migration/fichas/V20260504181427__crear_tablas_fichas_perfil.sql` |

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
JpaQueryEntity · Command · Query · Criteria · ReadModel · RequestDTO/ResponseDTO ·
RequestMapper/ResponseMapper · Controller (web) · Consumer (AMQP) · CommandOutputAdapter ·
QueryOutputAdapter · SortMapper · JpaSpecification`.

Español para el concepto de negocio, inglés para el sufijo técnico. No hay sufijos en español —
`Controller`, no `Controlador`. El paquete de la feature va todo en minúsculas y sin separadores:
`fichaperfil`, nunca `fichaPerfil`.

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

## Eventos de dominio — dos formas, según quién los origina

**Forma por defecto en `fichas` (objeto de acción):** el `UseCase` publica directamente tras
persistir — `eventPublisher.publish(new AsesorFichaCambiadoEvent(...))`. El agregado no extiende
`AggregateRoot` porque el evento no nace de él sino de la acción. Ver
`CambiarAsesorFichaUseCaseImpl.java`.

**Forma con `AggregateRoot` (hoy solo `usuarios`):** el agregado extiende `AggregateRoot`,
`crear(...)` llama `publicarEvento(...)`, y el `UseCase` drena tras persistir con
`aggregate.extraerEventosSinPublicar().forEach(eventPublisher::publish)` — un solo método, no
existe `limpiarEventosSinPublicar()`. `obtenerEventosSinPublicar()` es `protected`.

**Coherencia dura, sea cual sea la forma:** si el plan dice "Eventos: ninguno" → no hay clases en
`event/`, el `UseCase` no inyecta `EventPublisher`, y el agregado no extiende `AggregateRoot`.
Extenderlo "por consistencia" arrastra maquinaria muerta. `FichaPerfilDomain` hoy es
`public final class` sin `AggregateRoot` — ese es el caso normal.

`reconstruir(...)` nunca publica eventos y el `CommandOutputAdapter` siempre lee con
`reconstruir(...)`, nunca con `crear(...)`. Un evento carga todo lo que su consumidor necesita
(`AsesorFichaCambiadoEvent` lleva nombre y email del asesor) para que el consumidor no tenga que
volver a consultar al productor. La publicación está centralizada en `shared:amqp` — nunca se crea
un `{Entidad}EventPublisher` local.

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
