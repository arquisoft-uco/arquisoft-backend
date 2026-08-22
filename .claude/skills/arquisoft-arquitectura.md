---
name: arquisoft-arquitectura
description: Arquitectura hexagonal + DDD + CQRS real de Arquisoft Backend — capas, convención de sufijos, AggregateRoot y eventos. Cargar antes de planificar, implementar, testear o validar cualquier HU/HT. Referencia siempre el código real de fichas/fichaperfil (el más completo) en vez de snippets embebidos.
---

# Skill: arquisoft-arquitectura

Fuente de verdad concisa para agentes. Para el detalle extendido con más ejemplos, ver
[`docs/ARQUITECTURA_Y_ESTRUCTURA.md`](../../docs/ARQUITECTURA_Y_ESTRUCTURA.md) (ruta relativa a la
raíz del repo). Para nomenclatura, testing, Checkstyle y git, ver la skill `arquisoft-estandares`.

**Regla de esta skill:** ningún ejemplo se pega como bloque de código. Cada fila apunta al archivo
real bajo `fichas/{domain,application,infrastructure}/src/main/java/com/arquisoft/fichas/{capa}/fichaperfil/`
— el contexto más completo del proyecto. Ábrelo con `Read` cuando necesites el detalle exacto.

## Dirección de dependencias (no negociable)

`domain ← application ← infrastructure`. Los 9 bounded contexts (`seguridad`, `usuarios`, `fichas`,
`notificaciones` con implementación real; `proyectos`, `artefactos`, `repositorio_artefactos`,
`entregables`, `evaluaciones` en scaffolding) **nunca** se importan entre sí — solo se comunican vía
eventos de dominio en RabbitMQ (`shared:amqp`).

## Estructura de una feature — un ejemplo real por capa

| Capa/paquete | Qué vive ahí | Ejemplo real (`fichaperfil`) |
|---|---|---|
| `domain/{feature}/` (directo, sin subcarpeta) | Aggregate root, sufijo `Domain`, Notification Pattern (`VACIO`, setters privados) | `fichas/domain/.../fichaperfil/FichaPerfilDomain.java` |
| `domain/{feature}/` — objeto de acción | Nominalización del verbo cuando la acción arrastra más que el agregado | `RegistroFichaPerfilDomain.java`, `CambioAsesorFichaDomain.java`, `ModificacionFichaPerfilDomain.java` |
| `domain/{feature}/model/` | Value objects / entrada de una Rule | `ExistenciaAsesorFicha.java`, `DisponibilidadTituloFicha.java` |
| `domain/{feature}/rules/` (+`impl/`) | Regla pura: sin Spring, sin Lombok, sin dependencias de constructor | `FichaPerfilTituloUnicoRule.java` + `rules/impl/FichaPerfilTituloUnicoRuleImpl.java` |
| `domain/{feature}/event/` | Eventos de dominio (extienden `DomainEvent`) | `event/AsesorFichaCambiadoEvent.java` |
| `domain/{feature}/exception/` | Excepciones de dominio (→ 422) | `exception/FichaTituloDuplicadoException.java` |
| `application/{feature}/command/primaryport/interactor/` (+`impl/`) | Contrato primario del comando, dueño de `@Transactional` | `RegistrarFichaPerfilInteractor.java` + `.../impl/RegistrarFichaPerfilInteractorImpl.java` |
| `application/{feature}/command/usecase/` (+`impl/`) | Colaborador interno, NO bajo `primaryport/` | `usecase/RegistrarFichaPerfilUseCase.java` + `usecase/impl/RegistrarFichaPerfilUseCaseImpl.java` |
| `application/{feature}/command/validator/` (+`impl/`) | Puro: solo inyecta Rules, sin `OutputPort`, sin `Finder`, sin `if` | `validator/impl/RegistrarFichaPerfilValidatorImpl.java` |
| `application/{feature}/command/finder/` (+`impl/`) | Siempre devuelve valor, nunca lanza por "no encontrado" | `finder/impl/FichaPerfilFinderImpl.java` |
| `application/{feature}/command/secondaryport/` | Puerto de salida — habla `Entity`, nunca `Domain` | `secondaryport/FichaPerfilOutputPort.java` + `secondaryport/entity/FichaPerfilEntity.java` |
| `application/{feature}/query/readmodel/` | Proyección plana de solo lectura | `query/readmodel/FichaPerfilReadModel.java` |
| `application/{feature}/query/criteria/` | Entrada de la consulta (filtros/orden/paginación) | `query/criteria/FichaPerfilCriteria.java` |
| `infrastructure/{feature}/command/primaryadapter/web/` | Un `Controller` por acción — nunca varios endpoints en uno | `command/primaryadapter/web/RegistrarFichaPerfilController.java` |
| `infrastructure/{feature}/command/secondaryadapter/entity/` (+`mapper/`, `repository/`) | JPA real + `OutputAdapter` | `secondaryadapter/entity/FichaPerfilJpaEntity.java`, `secondaryadapter/repository/FichaPerfilCommandOutputAdapter.java` |
| `infrastructure/{feature}/query/secondaryadapter/repository/` | `@Subselect`/`@Immutable`/`@Synchronize`, plana, aislada de `command/secondaryadapter` | `FichaPerfilJpaQueryEntity.java`, `FichaPerfilJpaSpecification.java`, `FichaPerfilQueryOutputAdapter.java` |

## Convención de sufijos (resumen)

`Domain · Interactor/InteractorImpl · UseCase/UseCaseImpl · Validator/ValidatorImpl ·
Rule/RuleImpl · Finder/FinderImpl · OutputPort · QueryOutputPort · Entity · JpaEntity ·
Command · ReadModel · Criteria · Controller (web) · Consumer (AMQP) · CommandOutputAdapter ·
QueryOutputAdapter`. Detalle línea por línea en `docs/ARQUITECTURA_Y_ESTRUCTURA.md#convenciones-de-nomenclatura`.

Español para el concepto de negocio, inglés para el sufijo técnico. No hay sufijos en español —
`Controller`, no `Controlador`.

## Aislamiento CQRS (regla dura)

`query/secondaryadapter` nunca importa nada de `command/secondaryadapter`, ni siquiera el
`JpaEntity`. Un `existePor`/`obtener` que solo alimenta un `Validator`/`Rule` del lado comando
pertenece al `OutputPort` de `command/`, consumido por un `Finder` — no se duplica bajo `query/` sin
un `primaryport` real detrás. Ver `fichas/infrastructure/.../fichaperfil/query/secondaryadapter/repository/FichaPerfilQueryRepository.java`
(extiende `QueryRepository`, no `JpaRepository` — no hereda `save`/`delete`).

## AggregateRoot y eventos

Una entidad raíz extiende `AggregateRoot` (`shared:domain`) **solo si la feature emite eventos de
dominio** — no "por consistencia". `FichaPerfilDomain` hoy **no** extiende `AggregateRoot` (no
emite eventos); `CambioAsesorFichaDomain`/`AsesorFichaCambiadoEvent` sí ilustran el flujo con
eventos en el mismo contexto (ver `fichas/domain/.../fichaperfil/event/AsesorFichaCambiadoEvent.java`).
`crear(...)` valida invariantes y genera IDs con los `Util*` de `shared:domain`/`shared:util`
(nunca `UUID.randomUUID()`/`Instant.now()` directo en dominio); `reconstruir(...)` no valida ni
genera nada nuevo. Ningún invariante lanza su propia clase de excepción: se acumula con
`ValidationResult.addError/lanzarSiTieneErrores` (Notification Pattern) — ver el propio
`FichaPerfilDomain.crear(...)`.

## Cuándo NO existe un paquete `query/`

Solo cuando la feature tiene una lectura real alcanzada por un `primaryport` (`UseCase` + su
`Controller`). Una comprobación de existencia que solo necesita un `Validator`/`Rule` del lado
comando va en el `OutputPort` de `command/`, nunca duplicada bajo `query/`. Detalle y casos reales
(`estudiante`, `representantecomite`, `estadofichaperfil`) en
`docs/ARQUITECTURA_Y_ESTRUCTURA.md#cuándo-existe-un-paquete-query`.
