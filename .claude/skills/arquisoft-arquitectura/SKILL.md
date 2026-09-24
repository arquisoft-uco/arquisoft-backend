---
name: arquisoft-arquitectura
description: Arquitectura hexagonal + DDD + CQRS real de Arquisoft Backend — capas, paquetes, convención de sufijos, puertos, eventos y aislamiento CQRS. Cargar antes de planificar, implementar, testear o validar cualquier HU/HT. El contexto de referencia es siempre fichas/fichaperfil.
---

# Skill: arquisoft-arquitectura

**Esta skill es la fuente de verdad de arquitectura para agentes.** `CLAUDE.md` es un índice
operativo (comandos, entorno local, stack, seguridad, tracing) y remite aquí; si discrepan, gana esta
skill. El detalle largo de lectura humana está en `docs/ARQUITECTURA_Y_ESTRUCTURA.md`. Para
nomenclatura, validación, mensajes, excepciones, Checkstyle y testing, ver la skill
`arquisoft-estandares`.

**Regla de esta skill:** ningún ejemplo se pega como bloque de código. Cada fila apunta al archivo
real de `fichas` — el único contexto de negocio completo del proyecto y el patrón a copiar. Ábrelo
con `Read` cuando necesites el detalle exacto.

Además de `fichas`, estos contextos sirven de referencia para algo distinto, y cada uno tiene un
límite que hay que conocer antes de copiar:

- **`seguridad`** — referencia del paquete `command/result/` (+ su `mapper/`) y del layout de
  excepciones por capa dentro del slice. Controllers partidos uno por acción, DTO de request `record`
  desnudos con `RequestMapper`, `AppLogger`. *Límite:* no tiene base de datos (Keycloak + Redis), así
  que ahí no hay `JpaEntity`, `Flyway` ni `@Transactional`; y sus cuatro `*ResponseDTO` son clases
  Lombok en vez de `record` — ese detalle no se copia.
- **`notificaciones`** — referencia del `Consumer` AMQP y, sobre todo, del comando **sin `Validator`**:
  su única consulta es un corte de idempotencia resuelto con `Finder` + `if/return`, no con una `Rule`.
- **`usuarios`** — referencia del **lado dueño de una réplica** y de un adaptador secundario hacia un
  proveedor externo. `RegistrarUsuarioUseCaseImpl` persiste, da de alta la identidad en Keycloak
  (`secondaryadapter/keycloak/KeycloakProveedorIdentidadOutputAdapter`) y encadena
  `Agregar{Estudiante,Coordinador,Asesor,AsesorFicha}` según el rol, todos colgando del orquestador;
  cada uno publica el evento `*Agregado` que `fichas` y `proyectos` replican.
  `RemoverEstudianteUseCaseImpl` es la baja lógica con su evento `*Removido`. *Límite:* no tiene lado
  `query/`; para lecturas la referencia sigue siendo `fichas`.
- **`evaluaciones`** — el slice más reciente del repo (`itemcualitativojurado`, agosto 2026) y por eso
  el más limpio como molde de un **contexto que arranca**: `Controller` → `RequestMapper` →
  `Command.crear(...)` → `Interactor` (`@Transactional`) → `UseCase` → `Finder` → `Validator` →
  `Rule` → `OutputPort` que habla `Entity` → `JpaMapper` → `CommandRepository`, con su migración
  timestamp y su `EvaluacionesAuthorities`. Es además la referencia de la **consulta con política de
  acceso**: `ConsultarEvaluacionesCualitativasJuradoUseCaseImpl` valida con un `Validator` de consulta
  y una `Rule` de dominio antes de leer (`arquisoft-estandares` → *Validación en una consulta*).
  *Límite:* no tiene eventos ni consumidores, y sus consultas no paginan ni filtran — para eso sigue
  siendo `fichas` la referencia.
- **`proyectos`** — referencia del **lado réplica**: solo consume los eventos `*Agregado`/`*Removido`
  de `usuarios` (`primaryadapter/amqp/usuarios/{entidad}/`) y mantiene sus tablas espejo de
  `asesor`, `coordinador` y `estudiante`. Cada caso de uso devuelve una sellada de desenlace
  (`AgregacionAsesorResult` → `Agregada`/`Duplicada`/`Descartada`): `Descartada` es el evento más
  viejo que el `ocurridoEn` guardado, que la réplica ignora en vez de sobrescribir. *Límite:* no
  tiene controllers, ni lado `query/`, ni eventos propios.
- **`solicitudes`** — referencia de **varios comandos hermanos que comparten piezas** y del camino
  completo hacia `notificaciones`. Los cuatro `EnviarSolicitud*UseCaseImpl` reutilizan
  `RegistrarRemitente` y `RegistrarDestinatario` en vez de duplicarlos, validan con `Rule`s de
  existencia, unicidad y asignación, y publican un evento `*Enviada` que `notificaciones` consume
  (`amqp/solicitudes/solicitud/`). Mantiene además una réplica de `usuario` alimentada por los
  eventos de `usuarios`, y es el primer caso de *Consulta síncrona entre contextos*
  (`asignacionproyecto/`). *Límites:* el adaptador `webclient/AsignacionProyectoOutputAdapter` es un
  stub documentado en `CLAUDE.md` → *Desviaciones conocidas*, así que `DestinatarioAsignadoRule`
  todavía no rechaza nada; `UsuarioSolicitudesCommandOutputAdapter` y
  `UsuarioSolicitudesCommandRepository` llevan el contexto en el nombre, que es la convención
  retirada para réplicas (no se copia); y no tiene lado `query/`.


### La dirección de dependencias la verifica el build

`domain ← application ← infrastructure` no es una convención que se recuerde: es el grafo de módulos.
`{contexto}/infrastructure` **no declara `:{contexto}:domain` en `implementation`** — solo en
`testImplementation`, porque los slices arman domains en el *arrange* — así que un import del
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
un domain que un adaptador quiere construir es señal de que el puerto debería hablar `Entity`.

Los **diez** contextos están en `contextosHexagonales`, `notificaciones` incluido. Su consumidor no
nombra el enum de dominio: `AsesorFichaCambiadoConsumer` usa `TipoNotificacionEvento` (espejo propio
de infraestructura) y pasa `getCodigo()`, y `EnviarNotificacionCommand.crear(...)` lo resuelve con
`TipoNotificacion.desde(...)`. Ese es el patrón cuando un adaptador necesita nombrar un valor de
catálogo: un espejo en su capa + `String` cruzando la frontera, nunca el enum del dominio.

## Los planes y reportes de `.workspace/` NO son referencia de convención

`.workspace/h-plan/PLAN-*.md` y `.workspace/validator/validator-*.md` son el **registro de trabajo
ya entregado**, local a cada máquina (`.workspace/` está en `.gitignore`; la copia que perdura se
publica en `arquisoft-docs`). Cada uno refleja las convenciones de su fecha, y los más antiguos
describen un código que ya no existe. Esta tabla sirve de indicador: un plan que
dice cualquiera de las cosas de la izquierda está caducado; uno que no dice ninguna puede estar al
día, pero sigue sin ser una referencia de convención:

| Lo que dicen esos archivos | Lo que hay hoy |
|---|---|
| `{Entidad}Aggregate`, carpeta `domain/{feature}/aggregate/` | `{Entidad}Domain`, directo bajo `domain/{feature}/` |
| Factories `build(...)` / `rebuild(...)` | `crear(...)` / `reconstruir(...)` |
| Un domain que acumula eventos y un use case que los drena | El domain es plano; el `UseCase` publica por `EventPublisher` |
| `DomainValidator.notNull(...)` | Familia `Validator*` de `shared:validation` (`ValidatorObjeto.noNulo`, …) |
| `FichasMessages.*` | Catálogo Redis (`{Feature}Key`) + `FichasApiMessages` solo para Swagger |
| Migraciones `V1.0`, `V1.9` | Timestamp `V{yyyyMMddHHmmss}` |
| Un `{Entidad}QueryOutputPort` para chequeos de existencia | Va en el `OutputPort` de `command/`, vía `Finder` |
| DTO con `@NotBlank` y `toCommand()` propio | `record` desnudo + `RequestMapper` → `Command.crear(...)` |
| `UUID.randomUUID()` / puertos que hablan `Domain` | `UtilUUID` / puertos que hablan `Entity` |

**Regla:** donde un archivo de `.workspace/` y esta skill discrepen, **gana la skill, siempre** —
no es un empate a resolver ni una desviación que reportar. Nunca abras un plan viejo como ejemplo de
formato ni de contenido: si necesitas ver cómo se hace algo, abre el **código real** de `fichas`,
que es lo que estas skills citan. Y si te piden retomar una HU cuyo plan muestra esos indicadores,
di explícitamente que el plan está desactualizado y qué partes hay que rehacer antes de tocar nada.

## Dirección de dependencias (no negociable)

`domain ← application ← infrastructure`. Los 10 bounded contexts (`seguridad`, `usuarios`, `fichas`,
`notificaciones`, `proyectos`, `evaluaciones` y `solicitudes` con código; `artefactos`,
`repositorio_artefactos` y `entregables` solo con su `{Contexto}DataSourceConfig`) **nunca** se
importan entre sí. Se comunican
por eventos de dominio en RabbitMQ (`shared:amqp`), con **una única excepción acotada**: una consulta
síncrona de solo lectura a otro contexto, hecha por HTTP (nunca un import) — ver
`references/consultas-sincronas.md`.

Dentro de un contexto sí hay tráfico entre features (`fichaperfil` consulta `asesorficha`), pero
siempre a través del **puerto de application** de la otra feature, nunca de su `domain/` ni de su
adaptador.

## Estructura de una feature — el árbol real de `fichaperfil`

Rutas abreviadas desde `fichas/{capa}/src/main/java/com/arquisoft/fichas/{capa}/fichaperfil/`.

### domain

| Paquete | Qué vive ahí | Ejemplo real |
|---|---|---|
| `{feature}/` (directo, sin subcarpeta) | El domain (sufijo `Domain`), Notification Pattern (`VACIO`, `esVacio()`, setters privados) | `FichaPerfilDomain.java` |
| `{feature}/` — **objeto de acción** | Nominalización del verbo cuando la acción arrastra más que el domain; vive al lado del domain, sin subpaquete. Condicional (el `{Accion}{Entidad}Mapper` existe siempre; esto solo cuando hay *bundle*) | `RegistroFichaPerfilDomain.java`, `CambioAsesorFichaDomain.java`, `ModificacionFichaPerfilDomain.java` |
| `{feature}/model/` | Value objects y el record de entrada de cada `Rule` | `ExistenciaAsesorFicha.java`, `DisponibilidadTituloFicha.java` |
| `{feature}/rules/` (+`impl/`) | Regla pura: sin Spring, sin Lombok, **sin dependencias de constructor** | `rules/FichaPerfilTituloUnicoRule.java` + `rules/impl/FichaPerfilTituloUnicoRuleImpl.java` |
| `{feature}/event/` | Eventos de dominio (extienden `DomainEvent`) | `event/AsesorFichaCambiadoEvent.java` |
| `{feature}/exception/` | Excepciones de dominio (→ 422) | `exception/FichaTituloDuplicadoException.java` |

#### El objeto de acción lleva solo lo que la acción necesita

No es el domain cargado ni una copia suya: es lo mínimo con lo que la acción se puede decidir y
ejecutar. La forma dominante — 8 de los 10 que existen — es **ids planos y escalares**:
`CambioAsesorFichaDomain` son dos `UUID` (ficha y nuevo asesor), `ModificacionFichaPerfilDomain` son
`UUID` + título + `UUID` del estudiante. No cargues `FichaPerfilDomain` entero para cambiar su
asesor; lo que hace falta para eso son dos identificadores.

Solo cuando la acción crea de verdad varios objetos a la vez el objeto de acción **contiene otros
`Domain`**, y entonces el orden de construcción es de menor a mayor jerarquía — el compuesto se arma
al final, porque los de abajo necesitan el id del de arriba... que ya existe porque se creó primero.
`RegistrarFichaPerfilMapper` es el único caso hoy y se lee entero:

1. `FichaPerfilDomain.crear(...)` — el domain, que genera su propio id.
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
| `command/primaryport/mapper/` | `Command` → dominio (`final`, constructor privado, `static toDomain`): construye el objeto de acción, o el domain directo (`toDomain(command)` → `{Entidad}Domain.crear(...)`) si el `Command` mapea 1-a-1. **Obligatorio en toda escritura**; lo invoca el `Interactor` antes de delegar. | `RegistrarFichaPerfilMapper.java` |
| `command/usecase/` (+`impl/`) | Colaborador interno — **NO** bajo `primaryport/`, sin transacción | `usecase/RegistrarFichaPerfilUseCase.java` + `usecase/impl/...UseCaseImpl.java` |
| `command/validator/` (+`impl/`) | Puro: construye sus `Rule`s con `new` en un constructor sin argumentos; sin `OutputPort`, sin `Finder`, **sin un solo `if`** | `validator/impl/RegistrarFichaPerfilValidatorImpl.java` |
| `command/finder/` (+`impl/`) | Uno por consulta; siempre devuelve valor, nunca `Optional` ni `null` ni lanza por "no encontrado": `Boolean`/`Long`, el `Domain` (o su `VACIO`), o el `UUID` (o `UtilUUID.obtenerUUIDPorDefecto()`) | `finder/impl/TituloFichaPerfilExisteFinderImpl.java` |
| `command/finder/model/` | Entrada del `Finder` cuando **no** es un tipo de dominio ni un escalar — un criterio propio de la consulta. Condicional: la mayoría de finders reciben un `UUID`, un `String` o el domain | `notificaciones/.../finder/model/CriterioReintento.java` |
| `command/secondaryport/` (+`entity/`, `mapper/`) | Puerto de salida — habla `Entity`, nunca `Domain` | `FichaPerfilOutputPort.java`, `entity/FichaPerfilEntity.java`, `mapper/FichaPerfilMapper.java` |
| `command/secondaryport/model/` | Los tipos que el puerto usa en su firma y no son la `Entity`: lo que devuelve un sistema externo y las selladas de desenlace | `notificaciones/.../secondaryport/model/{MensajeNotificacion,ResultadoEntrega}.java`, `seguridad/.../secondaryport/model/CredencialesProveedor.java` |
| `command/result/` (+`mapper/`) | Salida del comando cuando **no** es `UUID` ni `void` — ver abajo | `notificaciones/.../command/result/EnvioNotificacionResult.java` (sellada) + `result/mapper/EnvioNotificacionResultMapper.java`; también `seguridad/auth/command/result/AutenticacionResult.java` |

### application — lado query

| Paquete | Qué vive ahí | Ejemplo real |
|---|---|---|
| `query/primaryport/interactor/` (+`impl/`) | `@Transactional(readOnly = true, transactionManager = "fichasTransactionManager")`. Recibe **siempre** un `Query` (nunca el `Criteria` directo); el `impl` llama al `primaryport/mapper.toCriteria(entrada)` y delega | `ConsultarFichasPerfilCoordinadorInteractorImpl.java` |
| *(entrada del interactor)* | Sin dato validado extra: el genérico `ConsultaCriteriaQuery` (`shared:query`) — no se declara tipo propio | `ConsultaCriteriaQuery` |
| `query/primaryport/model/` | `{Consult}{Entidad}Query` — **solo** si la consulta trae entrada validada más allá del criteria (path variable, subject del JWT, filtro forzado). Es un `record` con `crear(...)` que valida ese dato y **compone** `ConsultaCriteriaQuery` (`UUID x, ConsultaCriteriaQuery criterio`), nunca re-declara `pagina`/`tamanio`/`ordenamiento`/`raiz` | `ConsultarFichasPerfilAsesoradasQuery.java` |
| `query/primaryport/mapper/` | `Consultar{Entidad}[{Rol}]Mapper` — `final`, ctor privado, `static toCriteria(query) → {Entidad}Criteria`. Aquí corre la validación `camposFiltrables()`/`camposOrdenables()`. Simétrico al `command/primaryport/mapper/` | `ConsultarFichasPerfilCoordinadorMapper.java`, `ConsultarFichasPerfilAsesoradasMapper.java` |
| *(sin entrada)* | Si no hay `Query` **ni** `Criteria` — catálogo cerrado que se devuelve entero — el interactor extiende `SupplierInteractor<O>` y el caso de uso `SupplierUseCase<O>`, con `ejecutar()` sin parámetros. **Nunca `Interactor<Void, O>`** | `ConsultarEstadosFichaInteractor.java` |
| `query/usecase/` (+`impl/`) | Colaborador interno — recibe el `{Entidad}Criteria`, no el `Query` | `ConsultarFichasPerfilCoordinadorUseCaseImpl.java` |
| `query/criteria/` | Entrada de la consulta (filtros/orden/paginación), extiende `QueryCriteria` de `shared:query` | `FichaPerfilCriteria.java` |
| `query/readmodel/` | Proyección plana. **Nunca se serializa directo** — sin anotaciones Jackson, sin Lombok | `FichaPerfilReadModel.java` |
| `query/secondaryport/` | Puerto de lectura, retorna `PaginatedResult<ReadModel>` | `FichaPerfilQueryOutputPort.java` |
| `query/validator/` (+`impl/`), `query/finder/` (+`impl/`) | **Solo si la HU pone una política de acceso sobre la instancia consultada** (existencia, pertenencia, estado). `Consultar{…}Validator` propio de la consulta, `{X}QueryFinder` y su `{X}AccesoQueryOutputPort` en la feature consultada; las `Rule`s son del `domain/` y se comparten con los comandos. Detalle en `arquisoft-estandares` → *Validación en una consulta* | `evaluaciones/.../evaluacioncualitativajurado/query/validator/`, `evaluacionjurado/query/finder/` |

### infrastructure

| Paquete | Qué vive ahí | Ejemplo real |
|---|---|---|
| `command/primaryadapter/web/` (+`dto/`, `mapper/`) | Un `Controller` por acción — nunca varios endpoints en uno | `RegistrarFichaPerfilController.java`, `dto/RegistrarFichaPerfilRequestDTO.java`, `dto/RegistrarFichaPerfilResponseDTO.java`, `mapper/RegistrarFichaPerfilRequestMapper.java` |
| `command/primaryadapter/amqp/{productor}/{entidad}/` | `Consumer` AMQP (extiende `AbstractEventConsumer`, o `AbstractNotificacionConsumer` en `notificaciones`) + su payload `record` **local**, agrupados por contexto productor y después por entidad de ese productor | `notificaciones/.../amqp/fichas/asesorficha/AsesorFichaCambiadoConsumer.java` |
| `command/secondaryadapter/entity/` (+`mapper/`, `repository/`) | JPA real + `OutputAdapter` + repo Spring Data | `entity/FichaPerfilJpaEntity.java`, `mapper/FichaPerfilJpaMapper.java`, `repository/FichaPerfilCommandOutputAdapter.java`, `repository/FichaPerfilCommandRepository.java` |
| `query/primaryadapter/web/` (+`dto/`, `mapper/`) | `Controller` de lectura + **`{Entidad}ResponseDTO` + `{Entidad}ResponseMapper`** + `Consultar{Entidad}[{Rol}]RequestMapper` con `toQuery(dto[, datoDelJwt])` que arma el **`Query`** (`ConsultaCriteriaQuery` o `{Consult}{Entidad}Query`), nunca el `Criteria` | `ConsultarFichasPerfilCoordinadorController.java`, `dto/FichaPerfilResponseDTO.java`, `mapper/FichaPerfilResponseMapper.java`, `mapper/ConsultarFichasPerfilCoordinadorRequestMapper.java` |
| `query/secondaryadapter/repository/` (+`mapper/`) | `@Subselect`/`@Immutable`/`@Synchronize`, plana; specification, sort, adapter, repo | `FichaPerfilJpaQueryEntity.java`, `FichaPerfilJpaSpecification.java`, `FichaPerfilSortMapper.java`, `FichaPerfilQueryOutputAdapter.java`, `FichaPerfilQueryRepository.java`, `mapper/FichaPerfilQueryMapper.java` |
| `{feature}/exception/` | Excepciones de infraestructura del feature (→ 503) — **dentro del slice, no a nivel de contexto** | `seguridad/infrastructure/auth/exception/ProveedorIdentidadNoDisponibleException.java` |
| `security/`, `config/`, `filter/` | Transversales del contexto | `security/FichasAuthorities.java`, `config/FichasDataSourceConfig.java` |
| `handler/` | El `@RestControllerAdvice` del contexto, si lo tiene — **nunca en `exception/`** | `seguridad/infrastructure/handler/SeguridadGlobalExceptionHandler.java` (solo `seguridad` tiene uno; `fichas` no) |
| `src/main/resources/db/migration/{contexto}/` | Migraciones Flyway del contexto — subcarpeta propia obligatoria | `db/migration/fichas/V20260504181427__crear_tablas_fichas_perfil.sql` |

## Quién orquesta a quién: el que llama compone, el llamado es un solo paso

Un caso de uso de escritura **puede** invocar a otro. `RegistrarFichaPerfil` encadena
`AsignarEstadoInicialFichaPerfil` y después `AsignarEstudiantesFichaPerfil`;
`RegistrarEvaluacionFichaPerfil` encadena `AsignarEstadoInicialEvaluacion`. Lo que no puede es
encadenar un paso **colgado de un hermano**: todos los pasos de la transacción de negocio cuelgan
del mismo orquestador.

```java
// MAL — el estado inicial arrastra un paso que no le incumbe
public void ejecutar(RegistroFichaPerfilDomain registro) {   // recibe el bundle entero…
    var estadoInicial = registro.getEstadoInicial();
    // … valida y persiste el estado …
    asignarEstudiantesFichaPerfilUseCase.ejecutar(registro.getEstudiantes());  // ← paso ajeno
}

// BIEN — cada llamado recibe exactamente su parte
public void ejecutar(EstadoFichaPerfilDomain estadoInicial) { ... }   // AsignarEstadoInicial

// RegistrarFichaPerfilUseCaseImpl
fichaPerfilOutputPort.registrarFicha(FichaPerfilMapper.toEntity(ficha));
asignarEstadoInicialFichaPerfilUseCase.ejecutar(registro.getEstadoInicial());
asignarEstudiantesFichaPerfilUseCase.ejecutar(registro.getEstudiantes());
eventPublisher.publish(new FichaPerfilRegistradaEvent(...));
```

**La señal es la firma.** Un caso de uso encadenado recibe **el objeto de dominio más estrecho que
realmente lee**. Si pide el objeto de acción completo y solo usa una parte, lo pide para
alimentar un paso siguiente: ese paso pertenece al que llama. El objeto de acción existe
justamente para que el orquestador alcance cada pieza (`registro.getEstadoInicial()`,
`registro.getEstudiantes()`) y entregue a cada llamado la suya.

Enterrar un paso dentro de un hermano tiene dos costos concretos: el orden real de la transacción
deja de leerse en el orquestador, y el test del paso intermedio termina verificando un
encadenamiento que no es asunto suyo.

## El `UseCase` de escritura nunca recibe un `Command`

`UseCase<{Algo}Domain, R>`, siempre. El `Command` es el tipo del **primary port**: pertenece al
interactor y muere ahí, convertido por el `{Accion}{Entidad}Mapper`.

Esto vale **también cuando el comando no crea ningún domain**.
`ReintentarNotificacionesFallidas` es un job por lotes cuya entrada son dos números, y aun así
nominaliza en `ReintentoNotificacionesDomain` (`domain/notificacion/`). Que no haya domain no
autoriza a pasar el `Command`; solo decide si el objeto de dominio es el `{Entidad}Domain` o un objeto de
acción.

La validación duplicada entre `Command` y dominio **no es redundancia**: responden a llamadores
distintos — petición malformada → 400 (`ApplicationValidationException`), estado de dominio
imposible → 422 (`DomainValidationException`).

La excepción vive en el lado de lectura: el interactor de query pasa su `Criteria` directo al caso
de uso, y solo declara `{Consulta}{Entidad}Query` cuando hay entrada más allá del criteria.

## Cuando un comando devuelve un objeto: `command/result/`

Un comando normalmente devuelve `UUID` (el id de lo creado) o `void`, y entonces no necesita tipo
propio. Cuando devuelve algo más rico, ese algo es un **`{Concepto}Result`** en
`application/{feature}/command/result/`, sin anotaciones y sin Lombok.

**Tiene dos formas, y la elección la decide cuántos desenlaces hay.** Un desenlace único es un
`record` plano (`AutenticacionResult`, `ReintentoNotificacionesResult(int reenviadas, int fallidas,
int agotadas)`). Varios desenlaces mutuamente excluyentes son una **`sealed interface` con un
`record` por variante** — `EnvioNotificacionResult` declara `Enviada`, `Duplicada` y `Fallida`, cada
una con lo que ese desenlace necesita (`Fallida` añade `motivo`). La sellada es lo que permite al
consumidor hacer un `switch` exhaustivo sin `default`, de modo que un desenlace nuevo rompa la
compilación en vez de caer en una rama muda: es el mismo argumento que `ResultadoEntrega` en el
puerto secundario, un nivel más arriba.

Existe en dos contextos y ninguno es el único patrón: `seguridad` (`AutenticacionResult`,
`RefrescoTokenResult`, `ValidacionTokenResult`) y **`notificaciones`** (`EnvioNotificacionResult`,
`ReintentoNotificacionesResult`) — este último es el precedente a copiar en un contexto de negocio.
No se inventa una variante nueva ni se devuelve el `Domain`, el `Entity` o el DTO desde la capa de
aplicación.

La cadena completa, con `seguridad/auth` como referencia:

| Paso | Quién | Qué hace |
|---|---|---|
| 1 | `{Concepto}ResultMapper` (`command/result/mapper/`) | `final`, constructor privado, **`static toResult(...)`**. Convierte en el `Result` lo que devolvió el puerto secundario (`CredencialesProveedor`, de `secondaryport/model/`) **o el propio domain** — `EnvioNotificacionResultMapper` parte de `NotificacionDomain` y lee de él `idEvento` y `destinatario().email()` |
| 2 | `{Accion}{Entidad}UseCaseImpl` | Es quien **llama** al `ResultMapper` y retorna el `Result` |
| 3 | `{Accion}{Entidad}Interactor` | Solo declara el tipo: `Interactor<{Accion}{Entidad}Command, {Concepto}Result>`, con su `@Transactional` |
| 4 | `{Accion}{Entidad}ResponseMapper` (`infrastructure/.../command/primaryadapter/web/mapper/`) | `static toResponse(result)` → `{Accion}{Entidad}ResponseDTO` |

El paso 2 es el que más se equivoca: como el mapper vive en `application`, tienta llamarlo desde el
`Interactor`. No — el `Interactor` solo declara el tipo y delega, igual que cuando el retorno es un
`UUID` pelado.

Cuando el resultado tiene más de una rama, el mapper expone **una fábrica por variante** en vez de
recibir un `Optional` o de decidir dentro. Con dos ramas (encontrado / no encontrado)
`ValidacionTokenResultMapper` tiene `toResult(identidad)` y `toResultInvalido()`, y el use case
encadena `.map(ValidacionTokenResultMapper::toResult).orElseGet(ValidacionTokenResultMapper::toResultInvalido)`.
Con una sellada de tres, `EnvioNotificacionResultMapper` expone `toResultEnviada`, `toResultDuplicada`
y `toResultFallida`, y el use case elige con un `switch` sobre el `ResultadoEntrega` del puerto. Así
el use case sigue sin un solo `if`.

**El `Result` nunca se serializa directo**, exactamente por la misma razón que el `ReadModel` (ver
abajo): el contrato JSON vive en el `ResponseDTO`, no en el tipo de retorno de la capa de
aplicación. Por eso el lado comando tiene su propio `{Accion}{Entidad}ResponseMapper`, simétrico al
`{Entidad}ResponseMapper` del lado lectura.

Para cobertura: `*Result` y `*ResultMapper` **no** están en las exclusiones de JaCoCo (a diferencia
de `*Command` y `*ReadModel`), así que el test del use case que asserta los campos del `Result` es lo
que cubre el mapper.

Simetría que conviene tener presente: en `command/`, `primaryport/model/` es la entrada y `result/`
la salida; en `query/`, `criteria/` es la entrada y `readmodel/` la salida.


## Una operación sin entrada: `SupplierInteractor` / `SupplierUseCase`

`shared:application` expone tres pares y son la matriz completa — no existe una cuarta combinación:

| | entrada | salida |
|---|---|---|
| `Interactor<I,O>` / `UseCase<I,O>` | sí | sí |
| `VoidInteractor<I>` / `VoidUseCase<I>` | sí | no |
| `SupplierInteractor<O>` / `SupplierUseCase<O>` | no | sí |

**`Void` como tipo de entrada está prohibido.** No es una elección neutra de tipado: el único valor
habitable de `java.lang.Void` es `null`, así que `Interactor<Void, O>` obliga a todo llamador a
escribir `ejecutar(null)` — el `null` del controller no es un descuido, es la única llamada que
compila. `SupplierInteractor<O>`/`SupplierUseCase<O>` declaran `ejecutar()` sin parámetros y
eliminan el null quitando el parámetro, que es el mismo movimiento que `VoidInteractor`/`VoidUseCase`
ya hacen del lado de la salida.

Aplica a la consulta que no lleva `Query` ni `Criteria` porque no hay nada que filtrar, paginar ni
ordenar: `ConsultarEstadosFicha` es la referencia — `estado_ficha` es un catálogo cerrado y el
endpoint lo devuelve entero.

Dos salidas falsas que no debes tomar. Un `record` vacío como objeto de consulta es la
indirección-por-nada que la propia convención rechaza en el objeto de acción: renombra el nada. Y el
centinela `VACIO` tampoco sirve: existe para representar un dato que **pudo estar y no está** (una
ficha que no se encontró), mientras que aquí no hay dato ausente sino dato inexistente.

Del lado del test, un `SupplierInteractor` mockeado se stubea `when(interactor.ejecutar())` — sin
`isNull()` ni ningún `ArgumentMatcher`, que es la señal en el `@WebMvcTest` de que la firma quedó
bien.


## Cuándo un grupo de campos se vuelve un value object

Un domain con muchos atributos no se parte por número, sino por cohesión. Extrae un `record` a
`domain/{feature}/model/` cuando varios campos **viajan siempre juntos y no cambian tras crearse**:
`Destinatario(nombre, email)` y `Contenido(asunto, cuerpo, pie)` en `notificaciones` redujeron
`NotificacionDomain` de 14 campos a 11 y de siete setters privados a dos.

**No extraigas el grupo de ciclo de vida.** `estado`/`detalleError`/`fechaEnvio`/`intentos` parecen
otro value object y no lo son: las transiciones (`marcarEnviada`, `marcarFallida`,
`prepararReintento`) los mutan. Un value object es inmutable, así que encerrarlos obligaría a
reconstruirlo en cada transición y a que el domain pidiera permiso para cambiar su propio estado.

Forma del value object, y el detalle que se rompe fácil:

- `crear(campos..., ValidationResult result)` — **recibe el `ValidationResult` del domain**, no
  lanza por su cuenta. Si cada VO lanzara, un payload con nombre y correo inválidos devolvería solo
  el primer error y el `fieldErrors[]` del Notification Pattern dejaría de acumular. Cada campo
  inválido devuelve `UtilTexto.VACIO` y sigue.
- `reconstruir(campos...)` — normaliza (`aplicarTrim`) pero **no valida**: lo que ya está en la base
  entra tal cual; rechazarlo al leer solo impediría corregirlo.
- `VACIO` como centinela + `esVacio()`, igual que un domain.
- La factoría `crear(...)` del domain **sigue recibiendo los campos sueltos**, no los VO ya
  construidos: si recibiera los VO, la validación se habría ejecutado antes, fuera del domain.
## El `ReadModel` nunca sale por HTTP

El `Controller` de lectura mapea `ReadModel` → `{Entidad}ResponseDTO` con
`{Entidad}ResponseMapper` (`final`, constructor privado, `static toResponse`). La política de
serialización (`@JsonInclude`, nombres) vive en el DTO, no en el `ReadModel` — así el contrato JSON
no puede filtrarse al tipo de retorno del puerto secundario. Paginado:
`PageResponseDTO.from(resultado.map({Entidad}ResponseMapper::toResponse))`. Ver
`ConsultarFichasPerfilCoordinadorController.java`.

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
el centinela (`VACIO` o el UUID por defecto), decidir qué significa la ausencia es de quien llama. Referencia:
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


### Un record repetido no siempre es duplicación: se comparte por significado, no por forma

`ContactoAsesor(nombre, email)` en `domain/asesorficha/model/` y `ContactoEstudiante(nombre, email)`
en `domain/estudiantefichaperfil/model/` son idénticos carácter por carácter, y aun así **no deben
fundirse en un `Contacto` de `shared:domain`**. La regla anterior aplica igual dentro de un módulo
que ya existe: hoy cada uno tiene un solo consumidor, su propio evento.

El motivo de fondo es más fuerte que el conteo. Coincidir en la forma —dos strings— no los hace el
mismo concepto, y DRY habla del conocimiento, no de la estructura: dos records que cambian por
razones distintas no son una duplicación. El precio del error es además asimétrico. Repetir cuesta
tres líneas; compartir mal crea **un punto de acoplamiento dentro del payload de un evento**, así que
añadirle un `telefono` o convertir `email` en un value object validado cambia a la vez el contrato de
todos los contextos que lo consumen — justo lo que la mensajería existe para evitar.

Reconsidéralo solo si tres features acaban con el mismo record **y cambian juntos por la misma
razón**. Eso ya es un concepto compartido, no una forma repetida.

Lo que sí es un error es que el mismo concepto viaje con **dos formas distintas** según el evento:
`FichaPerfilRegistradaEvent` llevaba al asesor en campos planos (`asesorNombre`, `asesorEmail`)
mientras `EstudiantesFichaPerfilAsignadosEvent` llevaba cada estudiante como record. Un destinatario
se modela igual en todos los eventos del contexto; si no, el siguiente evento copia el que quedaba
más a mano.

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
| `EntityManager` en el adaptador (`@PersistenceContext`, `createNativeQuery`, `Object[]` con `@SuppressWarnings("unchecked")`), o un repositorio cuyo `@Query` devuelve columnas de otra tabla | Cada tabla que el lado comando toca, **aunque solo la lea para una `Rule`**, tiene su propio `{Entidad}JpaEntity` de comando y su `{Entidad}CommandRepository` en su feature, y el adaptador delega en él. El aislamiento CQRS prohíbe *importar* el `JpaQueryEntity`, no tener un `JpaEntity` de comando sobre la misma tabla: son dos mapeos independientes. Con `EntityManager`, el SQL queda en un string que nadie valida al arrancar y el resultado se mapea por posición de columna, así que un cambio en el `SELECT` compila y rompe en silencio. El SQL propio (un `JOIN ... FOR UPDATE`, un `UPDATE` puntual) va como `@Query`/`@NativeQuery` en el `CommandRepository` con proyección tipada; un `JOIN` para filtrar es legítimo, lo que se devuelve es de la tabla del repositorio. Referencia: `EstudianteCommandRepository` (`findIdsVigentesByIdIn` y su `UPDATE` con `@Modifying`) |
| `Boolean existePorX(...)` | El repo es uniforme en `boolean` primitivo, puerto y adaptador (`existePorId`, `existePorTituloProyecto`, `existeTituloEnOtraFicha`). El envuelto introduce un `null` posible que nadie comprueba y un unboxing silencioso dentro de la `Rule`. Esto aplica al **puerto**, no al `Finder`: `Finder<T, Boolean>` lleva el envuelto por obligación del genérico y es correcto |
| Método de escritura sin log | Los de escritura registran `logger.debug({Feature}Key.LOG_GUARDADA, id)` con el `AppLogger` inyectado por constructor. Los de lectura **no** logean. Es un eslabón de la estructura de logs del flujo de escritura — la estructura completa (las tres líneas del use case, el interactor que no logea, el caso anidado) está en `arquisoft-estandares` |

**Matiz que no es excepción a lo anterior:** un adaptador sí puede lanzar una `InfrastructureException`
**propia**, desde `infrastructure/{feature}/exception/`, para fallos que solo él diagnostica —
proveedor externo caído (`ProveedorIdentidadNoDisponibleException`), objeto ausente en MinIO. Lo
prohibido es envolver Spring Data y, sobre todo, lanzar excepciones de dominio.

Forma canónica:

```java
@Override
public void registrar({Feature}Entity entity) {
    repository.save({Feature}JpaMapper.toJpaEntity(entity));
    logger.debug({Feature}Key.LOG_GUARDADA, entity.id());
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

Se crea `query/` cuando la feature tiene una **lectura real alcanzada por un `primaryport`** (un
`UseCase` con su `Controller`), o cuando **una consulta** necesita comprobar algo de ella para su
política de acceso. Lo que decide el lado es **quién pregunta**, no de quién es el dato: una
comprobación que alimenta un `Validator`/`Rule` de **comando** va en el `OutputPort` de `command/`,
consumida por un `Finder` de comando, aunque el dato pertenezca a **otra feature**. Una que alimenta
el `Validator` de una **consulta** va en `query/finder/` + `query/secondaryport/` de la feature
consultada (`evaluacionjurado/query/`, sin `primaryport` propio), porque una consulta no toca el lado
comando. Detalle en `arquisoft-estandares` → *Validación en una consulta*.

Ejemplo real: `RegistrarFichaPerfilUseCaseImpl` confirma que el asesor existe con
`AsesorFichaExisteFinder`, que delega en `AsesorFichaOutputPort.existePorId(...)` — el puerto de
**command** de `asesorficha`. No hay `AsesorFichaQueryOutputPort` y no debe haberlo.
`estudiante`, `representantecomite` y `evaluacionfichaperfil` no tienen paquete `query/` por lo
mismo.


## `shared:domain` vs `shared:application` — la frontera la sostiene el compilador

`shared:domain` solo tiene `DomainEvent` (`com.arquisoft.shared.events`) y `DomainRule`
(`com.arquisoft.shared.rules`). Todo lo demás vive en `shared:application`:
`UseCase`/`VoidUseCase`/`SupplierUseCase` (`com.arquisoft.shared.usecase`),
`Interactor`/`VoidInteractor`/`SupplierInteractor` (`com.arquisoft.shared.interactor`),
`Finder` (`com.arquisoft.shared.finder`) y el puerto `EventPublisher`
(`com.arquisoft.shared.publisher`).

Antes eran un solo módulo llamado `domain`, así que `{contexto}/domain` recibía `UseCase` e
`Interactor` en su classpath y un domain podía implementarlos sin que nada fallara. Hoy no
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
- **`baselineOnMigrate` está en `false`** en todo contexto con Flyway (`usuarios`, `fichas`, `notificaciones`, `proyectos`, `evaluaciones`, `solicitudes`). Flyway ya no
  acepta en silencio una base con objetos preexistentes ni una versión fuera de orden — falla el
  arranque, que es justo lo que se quiere para no corromper el historial.
- **La versión es un timestamp `VyyyyMMddHHmmss`** tomado al crear el archivo
  (`V20260504181427__crear_tablas_fichas_perfil.sql`), no una secuencia. Dos migraciones de la misma
  entrega se separan por un segundo. Nunca se retrocede un timestamp ni se renombra/edita una
  migración ya aplicada: se agrega otra.
- **Una FK hacia otro contexto no es posible** — son bases distintas. Un dato de otro contexto que A
  necesita se modela como tabla réplica local poblada por eventos AMQP (`asesor_ficha`, `estudiante`
  en `fichas`); un chequeo puntual de escritura que solo pregunta "¿esto es así ahora?" puede ser una
  *Consulta síncrona entre contextos* (ver `references/consultas-sincronas.md`) — nunca una tabla compartida, nunca un import.
- **Una réplica de usuario con `eliminado_en` se lee declarando la vigencia.** Las bajas no se
  borran: `eliminado_en` no nulo marca al usuario dado de baja (`estudiante` en `fichas`,
  `estudiante`/`asesor` en `proyectos`). Toda consulta a esa tabla elige a propósito, según la
  intención:
  - **Crear un vínculo nuevo** (asignar, agregar, designar) → solo vigentes, con un
    `{Entidad}sVigentesFinder` sobre un método de repositorio con `eliminadoEn IS NULL`
    (`EstudiantesVigentesFinder` → `EstudianteCommandRepository.findIdsVigentesByIdIn`).
  - **Operar sobre lo ya vinculado** (remover, modificar el vínculo) → sin filtro, para que un usuario
    dado de baja se pueda desvincular (`EstudiantesExistentesFinder` en
    `RemoverEstudianteFichaPerfilUseCaseImpl`).
  - **Lectura** (`JpaQueryEntity` cuyo `@Subselect` hace `JOIN` a la réplica) → solo vigentes por
    defecto. La excepción es la vista de **quien administra el vínculo** (el coordinador que puede
    desvincular): ahí se incluyen las bajas, marcadas con un `boolean vigente` en el `ReadModel` y el
    `ResponseDTO`, porque la baja no borra el vínculo y ocultarlo lo dejaría huérfano. Referencia:
    `EstudianteFichaPerfilJpaQueryEntity` expone `(e.eliminado_en IS NULL) AS vigente`; el coordinador
    lee con `consultarPorFicha` y el estudiante con `consultarVigentesPorFicha` /
    `findCompanerosByFichaPerfilIdAndEstudianteId`. El historial pedido por la HU también incluye
    bajas, y el plan lo declara.

  Un `existsById` o un `JOIN` sin filtro compila y pasa los tests sembrados con datos frescos, y en
  producción deja asignar o listar a alguien dado de baja sin que nada falle.
- `@Table` no lleva `schema` ni catálogo, y el SQL no prefija nombres de base: la conexión ya apunta
  a la base correcta.


## Referencias bajo demanda — `references/`

Lo que sigue no aplica a toda HU, así que vive fuera de este archivo. **Antes de planificar,
implementar, testear o validar la parte correspondiente, abre el archivo con `Read`**: su contenido
es tan vinculante como el de arriba, solo se carga cuando hace falta. Si dudas de si aplica, ábrelo.

| Abre | Cuando la HU… | Secciones |
|---|---|---|
| `references/eventos.md` | publica o consume un evento, crea o cambia un estado (⇒ notificación), o mantiene una tabla réplica de otro contexto | *Eventos de dominio — una sola forma* · *El evento se emite donde ocurre el hecho* · *Transición de estado ⇒ notificación* (las ocho piezas) · *La routing key se declara una vez* · *Consumidores: primero el productor* · *Una cola de evento se declara con `ColaEvento`* · *El `nack` distingue fallo transitorio* · *Un efecto externo se reintenta desde la base* · *Replicación entre contextos* (incluye el nombrado de beans por FQN) · *Saga* |
| `references/consultas-sincronas.md` | necesita verificar al escribir un dato cuyo dueño es otro contexto | *Consultas síncronas entre contextos* |

## Rutas y autorización viven en constantes, no en literales

- `@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")` — placeholder de propiedad
  con default; las rutas vienen del yml. **No existe una clase `{Contexto}Routes`.**
- `@PreAuthorize(FichasAuthorities.Expresiones.HAS_FICHA_PERFIL_CREATE)` — nunca la cadena literal
  `"hasAuthority('...')"`. `FichasAuthorities` (`infrastructure/security/`) declara el client role
  crudo (para los tests) y su expresión SpEL.
- **Un client role por endpoint, propio y distinto.** La granularidad de los client roles vive a
  nivel de salida a la web: cada `Controller`/endpoint tiene el suyo y **nunca se reutiliza el de
  otro**, para poder concederlo o revocarlo por separado en Keycloak. Dos endpoints sobre el mismo
  recurso (`/fichas-perfil/coordinador`, `/fichas-perfil/asesor` — mismo `@Tag`) llevan roles
  independientes, diferenciando el segmento de recurso con un calificador:
  `fichas:ficha-perfil-coordinador:view` para uno, `fichas:ficha-perfil-asesor:view` para el otro.
- Nunca el prefijo `/api` en la ruta: ya es el `context-path` global.
