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

Los otros cuatro contextos con código ya se alinearon. Cada uno sirve de referencia para algo distinto,
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
  `toCommand()` propio, en vez de un record desnudo con `CrearUsuarioRequestMapper`. Su
  `CrearUsuarioCommand` ya tiene fábrica `crear(...)`, y el `RolUsuarioDTO` anidado dejó de importar
  `UsuarioRole`: lleva su propio código y la conversión ocurre en `crear(...)` vía `desdeCodigo(...)`,
  que es lo que permite a infrastructure no ver el dominio.
- **`evaluaciones`** — el slice más reciente del repo (`itemcualitativojurado`, agosto 2026) y por eso
  el más limpio como molde de un **contexto que arranca**: `Controller` → `RequestMapper` →
  `Command.crear(...)` → `Interactor` (`@Transactional`) → `UseCase` → `Finder` → `Validator` →
  `Rule` → `OutputPort` que habla `Entity` → `JpaMapper` → `CommandRepository`, con su migración
  timestamp y su `EvaluacionesAuthorities`. *Límite:* es un solo comando de escritura — no tiene
  lado `query/`, ni eventos, ni consumidores; para eso sigue siendo `fichas` la referencia.


### La dirección de dependencias la verifica el build

`domain ← application ← infrastructure` no es una convención que se recuerde: es el grafo de módulos.
`{contexto}/infrastructure` **no declara `:{contexto}:domain` en `implementation`** — solo en
`testImplementation`, porque los slices arman agregados en el *arrange* — así que un import del
dominio desde código de producción de infrastructure **no compila**. Esa es la barrera real, y es la
razón por la que los puertos hablan `Entity` y nunca `Domain`.

Como esa barrera se reabre en silencio con una línea en un `build.gradle`, la tarea
`verificarCapasHexagonales` del build raíz la vuelve a comprobar y **cuelga de `check`**. Inspecciona
el `compileClasspath` *resuelto*, así que también detecta una fuga transitiva (un `shared:*` que
reexporte con `api`). Reglas que aplica a todo contexto de negocio:

| Capa | No puede alcanzar |
|---|---|
| `domain` | `application` e `infrastructure` de su contexto, y `shared:application` |
| `application` | `infrastructure` de su contexto |
| `infrastructure` | `domain` de su contexto (en `main`; en `test` sí) |

Si `verificarCapasHexagonales` falla, **el arreglo nunca es añadir la dependencia al `build.gradle`**:
es que el tipo al que se está llegando está en la capa equivocada. Un enum de dominio que un adaptador
necesita nombrar viaja como `String` y se convierte en el `Command.crear(...)` con su `desde(...)`;
un agregado que un adaptador quiere construir es señal de que el puerto debería hablar `Entity`.

Los **nueve** contextos están en `contextosHexagonales`, `notificaciones` incluido. Su consumidor no
nombra el enum de dominio: `AsesorFichaCambiadoConsumer` usa `TipoNotificacionEvento` (espejo propio
de infraestructura) y pasa `getCodigo()`, y `EnviarNotificacionCommand.crear(...)` lo resuelve con
`TipoNotificacion.desde(...)`. Ese es el patrón cuando un adaptador necesita nombrar un valor de
catálogo: un espejo en su capa + `String` cruzando la frontera, nunca el enum del dominio.

## Los planes y reportes de `.workspace/` NO son referencia de convención

`.workspace/h-plan/PLAN-*.md` y `.workspace/validator/validator-*.md` son el **registro de trabajo
ya entregado** entre abril y agosto de 2026. Están versionados y se conservan como historia, pero
**todos** son anteriores a los refactors que fijaron las convenciones actuales, y describen un
código que ya no existe:

| Lo que dicen esos archivos | Lo que hay hoy |
|---|---|
| `{Entidad}Aggregate`, carpeta `domain/{feature}/aggregate/` | `{Entidad}Domain`, directo bajo `domain/{feature}/` |
| Factories `build(...)` / `rebuild(...)` | `crear(...)` / `reconstruir(...)` |
| Un agregado que acumula eventos y un use case que los drena | El agregado es plano; el `UseCase` publica por `EventPublisher` |
| `DomainValidator.notNull(...)` | Familia `Validator*` de `shared:validation` (`ValidatorObjeto.noNulo`, …) |
| `FichasMessages.*` | Catálogo Redis (`{Feature}Key`) + `FichasApiMessages` solo para Swagger |
| Migraciones `V1.0`, `V1.9` | Timestamp `V{yyyyMMddHHmmss}` |
| Un `{Entidad}QueryOutputPort` para chequeos de existencia | Va en el `OutputPort` de `command/`, vía `Finder` |
| DTO con `@NotBlank` y `toCommand()` propio | `record` desnudo + `RequestMapper` → `Command.crear(...)` |
| `UUID.randomUUID()` / puertos que hablan `Domain` | `UtilUUID` / puertos que hablan `Entity` |

**Regla:** donde un archivo de `.workspace/` y esta skill discrepen, **gana la skill, siempre** —
no es un empate a resolver ni una desviación que reportar. Nunca abras un plan viejo como ejemplo de
formato ni de contenido: si necesitas ver cómo se hace algo, abre el **código real** de `fichas`,
que es lo que estas skills citan. Y si te piden retomar una de esas HU, di explícitamente que el
plan está desactualizado y qué partes hay que rehacer antes de tocar nada.

## Dirección de dependencias (no negociable)

`domain ← application ← infrastructure`. Los 9 bounded contexts (`seguridad`, `usuarios`, `fichas`,
`notificaciones` y `evaluaciones` con código; `proyectos`, `artefactos`, `repositorio_artefactos` y
`entregables` solo con su `{Contexto}DataSourceConfig`) **nunca** se importan entre sí — solo se comunican vía
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
| `{feature}/` — **objeto de acción** | Nominalización del verbo cuando la acción arrastra más que el agregado; vive al lado del agregado, sin subpaquete. Condicional (el `{Accion}{Entidad}Mapper` existe siempre; esto solo cuando hay *bundle*) | `RegistroFichaPerfilDomain.java`, `CambioAsesorFichaDomain.java`, `ModificacionFichaPerfilDomain.java` |
| `{feature}/model/` | Value objects y el record de entrada de cada `Rule` | `ExistenciaAsesorFicha.java`, `DisponibilidadTituloFicha.java` |
| `{feature}/rules/` (+`impl/`) | Regla pura: sin Spring, sin Lombok, **sin dependencias de constructor** | `rules/FichaPerfilTituloUnicoRule.java` + `rules/impl/FichaPerfilTituloUnicoRuleImpl.java` |
| `{feature}/event/` | Eventos de dominio (extienden `DomainEvent`) | `event/AsesorFichaCambiadoEvent.java` |
| `{feature}/exception/` | Excepciones de dominio (→ 422) | `exception/FichaTituloDuplicadoException.java` |

#### El objeto de acción lleva solo lo que la acción necesita

No es el agregado cargado ni una copia suya: es lo mínimo con lo que la acción se puede decidir y
ejecutar. La forma dominante — 8 de los 10 que existen — es **ids planos y escalares**:
`CambioAsesorFichaDomain` son dos `UUID` (ficha y nuevo asesor), `ModificacionFichaPerfilDomain` son
`UUID` + título + `UUID` del estudiante. No cargues `FichaPerfilDomain` entero para cambiar su
asesor; lo que hace falta para eso son dos identificadores.

Solo cuando la acción crea de verdad varios objetos a la vez el objeto de acción **contiene otros
`Domain`**, y entonces el orden de construcción es de menor a mayor jerarquía — el compuesto se arma
al final, porque los de abajo necesitan el id del de arriba... que ya existe porque se creó primero.
`RegistrarFichaPerfilMapper` es el único caso hoy y se lee entero:

1. `FichaPerfilDomain.crear(...)` — el agregado, que genera su propio id.
2. `AsignarEstadoInicialFichaPerfilMapper.toDomain(ficha.getId())` y
   `AsignarEstudiantesFichaPerfilMapper.toDomain(ficha.getId(), ...)` — cada pieza la construye el
   mapper **de su propia feature**, no el de `fichaperfil`.
3. `RegistroFichaPerfilDomain.crear(ficha, estadoInicial, estudiantes)` — compone y valida que las
   tres estén presentes, nada más.

Ese `crear(...)` del compuesto no reimplementa las validaciones de sus partes: cada `Domain` ya validó
lo suyo al construirse, así que el de arriba solo comprueba `noNulo` de cada componente.

### application — lado command

| Paquete | Qué vive ahí | Ejemplo real |
|---|---|---|
| `command/primaryport/interactor/` (+`impl/`) | Contrato primario, dueño de `@Transactional` | `RegistrarFichaPerfilInteractor.java` + `impl/RegistrarFichaPerfilInteractorImpl.java` |
| `command/primaryport/model/` | `Command` — `record` con factoría `crear(...)` que valida formato | `RegistrarFichaPerfilCommand.java` |
| `command/primaryport/mapper/` | `Command` → dominio (`final`, constructor privado, `static toDomain`): construye el objeto de acción, o el agregado directo (`toDomain(command)` → `{Entidad}Domain.crear(...)`) si el `Command` mapea 1-a-1. **Obligatorio en toda escritura**; lo invoca el `Interactor` antes de delegar. (`usuarios/CrearUsuario` llama `crear(...)` directo desde el use case — desviación previa, no se copia) | `RegistrarFichaPerfilMapper.java` |
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

### Un fallo que el negocio registra no es una excepción: es un valor

Antes de escribir una excepción para un fallo de un puerto, pregunta **qué hace quien llama con
ella**. Si la captura para seguir adelante —porque el fallo es un estado que hay que persistir, no un
error del flujo— entonces no debía ser excepción. El puerto devuelve una sellada con los desenlaces
y desaparecen a la vez el `try/catch` de application y la excepción:

```java
public sealed interface ResultadoEntrega {
    record Entregada() implements ResultadoEntrega {}
    record Rechazada(String motivo) implements ResultadoEntrega {}
}
```

El adaptador traduce lo que sabe diagnosticar y **registra ahí la traza técnica**, que es donde tiene
la causa en la mano; una avería inesperada (mal configurado, fallo no previsto) **sí** se propaga y
acaba en la DLQ o en un 503. Es el mismo criterio que ya usan los `Finder`: "no encontrado" devuelve
`Optional`, decidir qué significa la ausencia es de quien llama. Referencia:
`EnvioNotificacionOutputPort` + `SmtpEnvioNotificacionOutputAdapter`.

Si aun así application tiene que **nombrar** una excepción que lanza un adaptador, esa excepción vive
junto al puerto (`command/secondaryport/exception/`) y no en `infrastructure/{feature}/exception/`:
es parte del contrato del puerto, y ponerla en infrastructure invertiría la dirección de capas.

## Un módulo `shared:` con un solo consumidor no es compartido

`shared:notification` existía con el puerto de envío, sus dos adaptadores (`Smtp`, `Log`) y su
configuración, y lo consumía **solo** `notificaciones`. Dos consecuencias, y la segunda es la grave:

1. Un "shared" de un solo cliente es un contexto mal ubicado.
2. `notificaciones/application` declaraba ese módulo, y ese módulo traía `JavaMailSender` y
   `MimeMessageHelper` — **infraestructura ejecutable en el classpath de la capa de aplicación**.
   `verificarCapasHexagonales` no lo detecta: razona por nombre de módulo, y ahí no dice
   "infrastructure".

Se disolvió dentro del contexto: el puerto y sus modelos a `application/…/secondaryport/`, los
adaptadores y la config a `infrastructure/…/secondaryadapter/{smtp,logging}` y `config/`.

**Antes de crear un `shared:*` nuevo, exige dos consumidores reales.** Y si un contexto ya sólo puede
alcanzarse por eventos —como `notificaciones`— no va a haber un segundo: la arquitectura lo prohíbe.

## Puertos: hablan `Entity`, nunca `Domain`

`domain/` no declara puertos ni hace I/O. El `OutputPort` recibe y devuelve el `record` plano
`{Entidad}Entity` (`command/secondaryport/entity/`), sin JPA ni Lombok; una relación `@ManyToOne`
viaja como el id desnudo, no como entidad anidada.

Dónde ocurre cada conversión:
- **UseCase**: `Domain → Entity` antes de llamar al puerto (`{Entidad}Mapper.toEntity`).
- **Finder**: `Entity → Domain` al volver.
- **OutputAdapter**: `Entity ↔ JpaEntity` (`{Entidad}JpaMapper`), pegado a cada llamada del repo.

## El `CommandOutputAdapter` es pura delegación

`FichaPerfilCommandOutputAdapter` es la referencia y no tiene un solo `try/catch`. Ningún adaptador
del proyecto captura excepciones de Spring Data (`grep -rl DataAccessException --include=*.java` →
cero resultados en producción). La razón es de orden, no de estilo: cuando la ejecución llega al
adaptador, el orden de validación **ya** garantizó formato, existencia, unicidad e invariantes. No
queda ningún error de negocio que el adaptador pueda descubrir, así que no tiene nada que traducir.
Su único trabajo es `Entity ↔ JpaEntity` y delegar en el repositorio.

| Anti-patrón | Por qué está prohibido |
|---|---|
| `catch (DataIntegrityViolationException)` → `throw {X}DuplicadoException(...)` | Esa excepción vive en `domain/{feature}/exception/` e **infrastructure no ve el dominio en absoluto** — es la razón de que los puertos hablen `Entity` y no `Domain`. Además duplica la regla: la unicidad ya la declara `{X}UnicoRule` alimentada por su `Finder` sobre `existePor...`, en el paso 2 del orden de validación. La garantía real de integridad es el `UNIQUE` de la migración Flyway, no el `catch` |
| `catch (DataAccessException)` → `errorPersistencia(...)` envolviendo en `InfrastructureException` | Sobra. `GlobalAppExceptionHandler` no mapea Spring Data, así que cae en su catch-all → 500 con log de error, que es exactamente el resultado correcto para "BD caída" o "bug de mapeo". El `try/catch` añade ruido por método y esconde la causa raíz tras un mensaje genérico |
| `saveAndFlush(...)` en el adaptador | `flush` no cierra la transacción (el commit sigue siendo del interactor), pero al saltar una violación de constraint deja la transacción en *rollback-only* y el `EntityManager` en estado indefinido: capturar ahí y continuar produce un `UnexpectedRollbackException` en el commit, lejos del origen. Solo existía para adelantar el error al `catch`; eliminado el `catch`, pierde su razón de ser. Usa `save`. (En el *arrange* de un `@DataJpaTest` sí es legítimo, para forzar el insert) |
| `Boolean existePorX(...)` | El repo es uniforme en `boolean` primitivo, puerto y adaptador (`existePorId`, `existePorTituloProyecto`, `existeTituloEnOtraFicha`). El envuelto introduce un `null` posible que nadie comprueba y un unboxing silencioso dentro de la `Rule` |
| Método de escritura sin log | Los de escritura registran `logger.debug(Mensajes.obtener({Feature}Key.LOG_GUARDADA), id)` con el `AppLogger` inyectado por constructor. Los de lectura **no** logean. Es un eslabón de la estructura de logs del flujo de escritura — la estructura completa (dos `INFO` por petición, `debug` de finders antes del validator, caso anidado) está en `arquisoft-estandares` |

**Matiz que no es excepción a lo anterior:** un adaptador sí puede lanzar una `InfrastructureException`
**propia**, desde `infrastructure/{feature}/exception/`, para fallos que solo él diagnostica —
proveedor externo caído (`ProveedorIdentidadNoDisponibleException`), objeto ausente en MinIO. Lo
prohibido es envolver Spring Data y, sobre todo, lanzar excepciones de dominio.

Forma canónica:

```java
@Override
public void registrar({Feature}Entity entity) {
    repository.save({Feature}JpaMapper.toJpaEntity(entity));
    logger.debug(Mensajes.obtener({Feature}Key.LOG_GUARDADA), entity.id());
}

@Override
public boolean existePorNombreIgnorandoMayusculas(String nombre) {
    return repository.existsByNombreIgnoreCase(nombre);
}
```

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


### Transición de estado ⇒ notificación

Un caso de uso que **crea o cambia un estado** — un campo de catálogo (`EstadoFicha`,
`EstadoEvaluacion`, …), una asignación de responsable, una aprobación o un rechazo — tiene consumidor
conocido: `notificaciones`. Emite el evento salvo que la HU diga explícitamente lo contrario. Ese es
el trabajo que el contexto `notificaciones` existe para hacer, y `AsesorFichaCambiadoEvent` →
`AsesorFichaCambiadoConsumer` es el camino completo que se copia.

El evento publicado no envía ningún correo por sí solo: sin nadie enganchado a esa routing key se
queda en el exchange. Son **seis** piezas en dos contextos:

| # | Módulo | Archivo |
|---|---|---|
| 1 | `shared:message/constant/` | constante nueva en `EventTopics.{Contexto}` con la routing key |
| 2 | `{contexto}/domain` | `{feature}/event/{Entidad}{Accion}Event.java`, `EVENT_TOPIC = EventTopics.{Contexto}.{X}` |
| 3 | `notificaciones/infrastructure/config/` | `Queue` + `Binding` en `Notificaciones{Contexto}QueueConfig` |
| 4 | `notificaciones/.../primaryadapter/amqp/{contextoProductor}/` | `{Evento}Payload.java`, `record` propio del adaptador — **nunca** la clase de evento del productor |
| 5 | `notificaciones/.../primaryadapter/amqp/{contextoProductor}/` | `{Evento}Consumer.java` extiende `AbstractNotificacionConsumer` — aquí se elige el texto, no en el use case |
| 6 | `notificaciones/domain/notificacion/model/` | constante nueva en `TipoNotificacion` (columna `VARCHAR`: **sin migración**) |
| 7 | `notificaciones/.../primaryadapter/amqp/` | la misma constante en `TipoNotificacionEvento` — `TipoNotificacionEventoTest` falla si falta |
| 8 | `shared:message` + `catalogo/notificaciones.properties` | `PlantillaKey.ASUNTO_*` / `CUERPO_*` con su aridad, más el texto |

El evento carga **nombre y correo del destinatario** más el dato legible del asunto, aunque duplique
lo que el productor ya tiene: un evento delgado obliga a `notificaciones` a llamar de vuelta, que es
el acoplamiento que los eventos eliminan. La dirección es de un solo sentido — el contexto productor
nunca depende de `notificaciones`, y `notificaciones` solo consume, nunca emite.


### La routing key se declara una vez, en `EventTopics`

La leen dos módulos que no se ven entre sí: el evento del productor y el `Binding` del consumidor.
Si cada lado la escribe por su cuenta y una cambia, **el binding deja de recibir sin que nada falle**
— no hay excepción, simplemente no llegan mensajes. Por eso vive en `shared:message/constant/EventTopics`,
agrupada por contexto productor, que es el único módulo que ambos lados ya alcanzan:

```java
public static final String EVENT_TOPIC = EventTopics.Fichas.FICHA_PERFIL_ASESOR_CAMBIADO;   // productor
public static final String ASESOR_CAMBIADO_ROUTING_KEY = EventTopics.Fichas.FICHA_PERFIL_ASESOR_CAMBIADO;
```

**El nombre de la cola se deriva, no se escribe.** La convención es `{contextoConsumidor}.{routingKey}`
y se compone con constantes, así que el compilador la resuelve y sigue sirviendo en `@RabbitListener`,
que exige una expresión constante (JLS §9.7.1):

```java
public static final String ASESOR_CAMBIADO_QUEUE =
        NotificacionesQueues.PREFIJO + EventTopics.Fichas.FICHA_PERFIL_ASESOR_CAMBIADO;
```

Ningún `*QueueConfig` escribe literales propios. El prefijo del contexto vive en `{Contexto}Queues`
(`infrastructure/config/`), compartido por todos los `*QueueConfig` de ese contexto; los nombres de
argumento y el sufijo de dead letter viven en `RabbitMQConfig` (`shared:amqp`), porque son del
protocolo: `ARG_DEAD_LETTER_EXCHANGE`, `ARG_DEAD_LETTER_ROUTING_KEY`, `SUFIJO_DEAD_LETTER`,
`SEPARADOR_COLA`.

**Nada de esto va al catálogo de Redis.** RabbitMQ compara la routing key carácter a carácter, y
`Mensajes.obtener(...)` es una llamada a método: en `@RabbitListener` ni siquiera compila.


### Consumidores: un subpaquete por contexto productor

`primaryadapter/amqp/` se subdivide por **quién produce**, no por entidad:

```
primaryadapter/amqp/
├── AbstractNotificacionConsumer.java     # base común
├── TipoNotificacionEvento.java           # espejo del enum de dominio
├── fichas/{Evento}Consumer.java + {Evento}Payload.java
└── evaluaciones/…
```

Es el eje que agrupa lo que varía junto: todo lo que se consume de un productor comparte su espacio
de routing keys. Bajar a entidad (`amqp/fichas/fichaperfil/`) da paquetes de dos archivos y rompe la
regla de que un paquete se llama por el sufijo de lo que contiene.

Los `*QueueConfig` **se quedan en `config/`**: ya hay uno por contexto productor y cada uno agrupa
todas sus colas, así que el paquete no se satura.

**Lo que todos los consumidores hacen igual sube a la base.** `AbstractNotificacionConsumer extends
AbstractEventConsumer` posee el `AppLogger` (`protected final`) y el `registrar(EnvioNotificacionResult)`
con el `switch` exhaustivo del desenlace. La subclase solo pone su `@RabbitListener`, su log de
entrada y su plantilla. Ventaja real: cuando aparezca un desenlace nuevo, el compilador falla en un
sitio y no en seis.
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
- **`setPackagesToScan` recibe un solo paquete:
  `em.setPackagesToScan("com.arquisoft.{contexto}.infrastructure")`.** Nunca dos, y en particular
  nunca `"com.arquisoft.{contexto}.application"`. Las `@Entity` viven todas en infrastructure desde
  que los puertos hablan `Entity`: en `application` está el `record` plano del `secondaryport`, que
  no lleva una sola anotación JPA. Los cuatro configs del repo (`fichas`, `notificaciones`,
  `usuarios` y el andamio de los contextos vacíos) ya son de una línea; si copias uno viejo con la
  lista de dos paquetes, estás escaneando un paquete sin entidades y sugiriendo que `application`
  sabe de JPA, que es justo lo que la migración de `Entity`/`JpaEntity` eliminó.
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
