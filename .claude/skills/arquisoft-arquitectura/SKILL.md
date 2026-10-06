---
name: arquisoft-arquitectura
description: Arquitectura hexagonal + DDD + CQRS real de Arquisoft Backend — capas, paquetes, convención de sufijos, puertos, eventos y aislamiento CQRS. Cargar antes de planificar, implementar, testear o validar cualquier HU/HT. El contexto de referencia es siempre fichas/fichaperfil.
---

# Skill: arquisoft-arquitectura

**Fuente de verdad de arquitectura para agentes.** `CLAUDE.md` es un índice operativo y remite aquí;
si discrepan, gana esta skill. Nomenclatura, validación, mensajes, excepciones, Checkstyle y testing
están en `arquisoft-estandares`.

Ningún ejemplo se pega como bloque de código: cada fila apunta al archivo real de `fichas` —el único
contexto completo y el patrón a copiar—. Ábrelo con `Read` cuando necesites el detalle.

## Otros contextos de referencia, y su límite

| Contexto | Referencia de | Límite (no se copia) |
|---|---|---|
| `seguridad` | `command/result/` (+`mapper/`), excepciones por capa dentro del slice, controllers por acción, `RequestDTO` `record` + `RequestMapper`, `AppLogger` | Sin BD (Keycloak + Redis): no hay `JpaEntity`, Flyway ni `@Transactional`. Sus cuatro `*ResponseDTO` son clases Lombok, no `record` |
| `notificaciones` | `Consumer` AMQP; comando **sin `Validator`** (su única consulta es un corte de idempotencia con `Finder` + `if/return`, no una `Rule`) | — |
| `usuarios` | Lado **dueño** de una réplica y adaptador hacia proveedor externo: `RegistrarUsuarioUseCaseImpl` persiste, da de alta en Keycloak (`secondaryadapter/keycloak/KeycloakProveedorIdentidadOutputAdapter`) y encadena `Agregar{Estudiante,Coordinador,Asesor,AsesorFicha}` desde el orquestador; cada uno publica su `*Agregado`. `RemoverEstudianteUseCaseImpl` es la baja lógica con `*Removido` | Sin lado `query/` |
| `evaluaciones` | Molde de un **contexto que arranca** (`itemcualitativojurado`): `Controller` → `RequestMapper` → `Command.crear` → `Interactor` → `UseCase` → `Finder` → `Validator` → `Rule` → `OutputPort` (`Entity`) → `JpaMapper` → `CommandRepository`, migración timestamp y `EvaluacionesAuthorities`. También la **consulta con política de acceso**: `ConsultarEvaluacionesCualitativasJuradoUseCaseImpl` (`arquisoft-estandares` → *Validación en una consulta*) | Sin eventos ni consumidores; sus consultas no paginan ni filtran |
| `proyectos` | Lado **réplica**: consume `*Agregado`/`*Removido` de `usuarios` (`primaryadapter/amqp/usuarios/{entidad}/`) para sus espejos de `asesor`, `coordinador` y `estudiante`. Cada caso de uso devuelve una sellada (`AgregacionAsesorResult` → `Agregada`/`Duplicada`/`Descartada`; `Descartada` = evento más viejo que el `ocurridoEn` guardado) | Sin controllers, `query/` ni eventos propios |
| `solicitudes` | **Un flujo con variantes por tipo** y camino completo a `notificaciones`. Un `Interactor` por tipo (`EnviarSolicitud{Tipo}`, `EliminarSolicitud{Tipo}`, `ResponderSolicitud{Tipo}`, `ModificarEstadoRespuesta{Tipo}`) converge en un use case por acción; el mapper del interactor fija el tipo esperado en el objeto de acción y `SolicitudEsDelTipoRule` despacha a la regla del tipo, así los códigos de error por tipo no cambian. `EnviarSolicitudUseCase` orquesta `RegistrarRemitente`/`RegistrarDestinatario` (`void`, cada uno con su `Validator` de existencia) y un solo `EnviarSolicitudValidator.validar(...)`. Sus eventos (`SolicitudEnviadaEvent`, `SolicitudRespondidaEvent`, `SolicitudEstadoModificadoEvent`, con `responsableNombre` genérico) derivan tema y tipo de `TipoSolicitud`, y `notificaciones` los consume con un consumidor por tipo (`amqp/solicitudes/{solicitud,respuesta}/`). Réplica de `usuario` + lado `query/` cuyo mapper fuerza el filtro por JWT y tipo | `UsuarioSolicitudesCommandOutputAdapter`/`…CommandRepository` llevan el contexto en el nombre (convención retirada) |
| `biblioteca` | **Contexto nuevo que nace como réplica** (HU-240): andamiaje completo (módulos Gradle, base, `BibliotecaDataSourceConfig`, Flyway, colas, catálogo) + réplica de `bibliotecario`, calcada de `proyectos/coordinador` | Solo *Agregar*: sin `bibliotecario.removido`, `usuario.modificado`, lápida, controllers ni `query/` |

## Los planes y reportes de `.workspace/` NO son referencia de convención

`.workspace/h-plan/PLAN-*.md` y `.workspace/validator/validator-*.md` son registro de trabajo ya
entregado, local (`.gitignore`; la copia que perdura está en `arquisoft-docs`). Reflejan las
convenciones de su fecha. Un plan que dice algo de la columna izquierda está caducado; uno que no lo
dice tampoco es referencia:

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

Donde un archivo de `.workspace/` y esta skill discrepen, **gana la skill** — no es una desviación que
reportar. Para ver cómo se hace algo, abre el código real de `fichas`, nunca un plan viejo. Si te piden
retomar una HU cuyo plan muestra esos indicadores, di qué partes están desactualizadas antes de tocar
nada.

## Dirección de dependencias (no negociable)

`domain ← application ← infrastructure`. Los bounded contexts **nunca** se importan entre sí: se
comunican por eventos en RabbitMQ (`shared:amqp`), con una única excepción acotada —una consulta
síncrona de solo lectura por HTTP, ver `references/consultas-sincronas.md`—. Entre features de un
mismo contexto (`fichaperfil` consulta `asesorficha`) se pasa siempre por el **puerto de application**
de la otra feature, nunca por su `domain/` ni su adaptador.

### La dirección de dependencias la verifica el build

`{contexto}/infrastructure` declara `:{contexto}:domain` solo en `testImplementation` (los slices arman
domains en el *arrange*), así que un import del dominio desde producción de infrastructure **no
compila**. Esa barrera es la razón de que los puertos hablen `Entity`. La tarea
`verificarCapasHexagonales` (cuelga de `check`) la vuelve a comprobar sobre el `compileClasspath`
resuelto, fugas transitivas incluidas, en los once contextos:

| Capa | No puede alcanzar |
|---|---|
| `domain` | `application` e `infrastructure` de su contexto, y `shared:application` |
| `application` | `infrastructure` de su contexto |
| `infrastructure` | `domain` de su contexto (en `main`; en `test` sí) |

Si falla, **el arreglo nunca es añadir la dependencia al `build.gradle`**: el tipo está en la capa
equivocada. Un valor de catálogo que un adaptador necesita nombrar viaja como `String`, con un espejo
propio en infraestructura, y se resuelve en `Command.crear(...)` con `desde(...)`
(`AsesorFichaCambiadoConsumer` usa `TipoNotificacionEvento.getCodigo()`;
`EnviarNotificacionCommand.crear` llama `TipoNotificacion.desde`). Un domain que un adaptador quiere
construir indica que el puerto debería hablar `Entity`.

## Estructura de una feature — el árbol real de `fichaperfil`

Rutas abreviadas desde `fichas/{capa}/src/main/java/com/arquisoft/fichas/{capa}/fichaperfil/`.

### domain

| Paquete | Qué vive ahí | Ejemplo real |
|---|---|---|
| `{feature}/` (directo) | El domain (sufijo `Domain`), Notification Pattern (`VACIO`, `esVacio()`, setters privados) | `FichaPerfilDomain.java` |
| `{feature}/` — **objeto de acción** | Nominalización del verbo cuando la acción arrastra más que el domain; al lado del domain. Condicional (el `{Accion}{Entidad}Mapper` existe siempre) | `RegistroFichaPerfilDomain.java`, `CambioAsesorFichaDomain.java`, `ModificacionFichaPerfilDomain.java` |
| `{feature}/model/` | Value objects y el record de entrada de cada `Rule` | `ExistenciaAsesorFicha.java`, `DisponibilidadTituloFicha.java` |
| `{feature}/rules/` (+`impl/`) | Regla pura: sin Spring, sin Lombok, **sin dependencias de constructor** | `FichaPerfilTituloUnicoRule.java` + `impl/FichaPerfilTituloUnicoRuleImpl.java` |
| `{feature}/event/` | Eventos de dominio (extienden `DomainEvent`) | `AsesorFichaCambiadoEvent.java` |
| `{feature}/exception/` | Excepciones de dominio (→ 422) | `FichaTituloDuplicadoException.java` |

#### El objeto de acción lleva solo lo que la acción necesita

Cuando la acción **modifica o referencia** algo existente, el objeto de acción son **ids planos y
escalares**: `CambioAsesorFichaDomain` son dos `UUID`; `ModificacionFichaPerfilDomain` es `UUID` +
título + `UUID` del estudiante. No se carga el domain entero para cambiar un campo.

Cuando la acción **crea** un domain y arrastra algo que ese domain no posee, el objeto de acción
**contiene ese `Domain`**, construido por el mapper que invoca el `Interactor` — nunca copia sus campos
para que el `UseCase` haga `{Entidad}Domain.crear(...)`. Así la integridad se valida una vez y antes de
cualquier `Finder` (orden 1 → 2 → 3); con campos copiados se valida dos veces y lo que solo valida el
`crear` del `UseCase` se rechaza después de consultar la BD. La señal es un `{Entidad}Domain.crear(...)`
dentro de un `UseCase`: solo se justifica si `crear` necesita un dato que existe tras una consulta o
escritura previa, y el plan lo dice.

El compuesto se arma de abajo arriba — `RegistrarFichaPerfilMapper` es la referencia:

1. `FichaPerfilDomain.crear(...)` — genera su propio id.
2. `AsignarEstadoInicialFichaPerfilMapper.toDomain(ficha.getId())` y
   `AsignarEstudiantesFichaPerfilMapper.toDomain(ficha.getId(), ...)` — cada pieza la construye el
   mapper **de su propia feature**.
3. `RegistroFichaPerfilDomain.crear(ficha, estadoInicial, estudiantes)` — solo comprueba `noNulo` de
   cada componente; cada parte ya validó lo suyo.

### application — lado command

| Paquete | Qué vive ahí | Ejemplo real |
|---|---|---|
| `command/primaryport/interactor/` (+`impl/`) | Contrato primario, dueño de `@Transactional` | `RegistrarFichaPerfilInteractor.java` + `impl/…InteractorImpl.java` |
| `command/primaryport/model/` | `Command` — `record` con `crear(...)` que valida formato | `RegistrarFichaPerfilCommand.java` |
| `command/primaryport/mapper/` | `Command` → dominio (`final`, ctor privado, `static toDomain`): objeto de acción, o el domain directo si el `Command` mapea 1-a-1. **Obligatorio en toda escritura**; lo invoca el `Interactor` | `RegistrarFichaPerfilMapper.java` |
| `command/usecase/` (+`impl/`) | Colaborador interno — **no** bajo `primaryport/`, sin transacción | `RegistrarFichaPerfilUseCaseImpl.java` |
| `command/validator/` (+`impl/`) | Puro: construye sus `Rule`s con `new` en un ctor sin argumentos; sin `OutputPort`, sin `Finder`, **sin un solo `if`** | `RegistrarFichaPerfilValidatorImpl.java` |
| `command/finder/` (+`impl/`) | Uno por consulta; siempre devuelve valor, nunca `Optional`, `null` ni lanza por "no encontrado": `Boolean`/`Long`, el `Domain` (o `VACIO`) o el `UUID` (o `UtilUUID.obtenerUUIDPorDefecto()`) | `TituloFichaPerfilExisteFinderImpl.java` |
| `command/finder/model/` | Entrada del `Finder` cuando no es un tipo de dominio ni un escalar. Condicional | `notificaciones/.../finder/model/CriterioReintento.java` |
| `command/secondaryport/` (+`entity/`, `mapper/`) | Puerto de salida — habla `Entity`, nunca `Domain` | `FichaPerfilOutputPort.java`, `entity/FichaPerfilEntity.java`, `mapper/FichaPerfilMapper.java` |
| `command/secondaryport/model/` | Tipos de la firma del puerto que no son la `Entity`: lo que devuelve un sistema externo y las selladas de desenlace | `notificaciones/.../secondaryport/model/{MensajeNotificacion,ResultadoEntrega}.java`, `seguridad/.../CredencialesProveedor.java` |
| `command/result/` (+`mapper/`) | Salida del comando cuando **no** es `UUID` ni `void` — ver abajo | `notificaciones/.../EnvioNotificacionResult.java` + `EnvioNotificacionResultMapper.java`; `seguridad/auth/.../AutenticacionResult.java` |

### application — lado query

| Paquete | Qué vive ahí | Ejemplo real |
|---|---|---|
| `query/primaryport/interactor/` (+`impl/`) | `@Transactional(readOnly = true, transactionManager = "fichasTransactionManager")`. Recibe **siempre** un `Query`, lo convierte con `primaryport/mapper.toCriteria(...)` y delega | `ConsultarFichasPerfilCoordinadorInteractorImpl.java` |
| *(entrada del interactor)* | Sin dato validado extra: el genérico `ConsultaCriteriaQuery` (`shared:query`), sin tipo propio | `ConsultaCriteriaQuery` |
| `query/primaryport/model/` | `{Consulta}{Entidad}Query` **solo** con entrada validada más allá del criteria (path variable, subject del JWT, filtro forzado): `record` con `crear(...)` que valida ese dato y **compone** `ConsultaCriteriaQuery`, nunca re-declara `pagina`/`tamanio`/`ordenamiento`/`raiz` | `ConsultarFichasPerfilAsesoradasQuery.java` |
| `query/primaryport/mapper/` | `Consultar{Entidad}[{Rol}]Mapper` — `final`, ctor privado, `static toCriteria(query)`. Aquí corre la validación `camposFiltrables()`/`camposOrdenables()` | `ConsultarFichasPerfilCoordinadorMapper.java` |
| *(sin entrada)* | Catálogo cerrado devuelto entero, sin `Query` ni `Criteria` → `SupplierInteractor<O>`/`SupplierUseCase<O>`. **Nunca `Interactor<Void, O>`** | `ConsultarEstadosFichaInteractor.java` |
| `query/usecase/` (+`impl/`) | Recibe el `{Entidad}Criteria`, no el `Query` | `ConsultarFichasPerfilCoordinadorUseCaseImpl.java` |
| `query/criteria/` | Filtros/orden/paginación, extiende `QueryCriteria` | `FichaPerfilCriteria.java` |
| `query/readmodel/` | Proyección plana. **Nunca se serializa directo** — sin Jackson, sin Lombok | `FichaPerfilReadModel.java` |
| `query/secondaryport/` | Puerto de lectura, retorna `PaginatedResult<ReadModel>` | `FichaPerfilQueryOutputPort.java` |
| `query/validator/` (+`impl/`), `query/finder/` (+`impl/`) | **Solo si la HU pone una política de acceso sobre la instancia consultada.** `Consultar{…}Validator` propio, `{X}QueryFinder` y su `{X}AccesoQueryOutputPort` en la feature consultada; las `Rule`s son del `domain/` y se comparten con los comandos (`arquisoft-estandares` → *Validación en una consulta*) | `evaluaciones/.../evaluacioncualitativajurado/query/validator/`, `evaluacionjurado/query/finder/` |

### infrastructure

| Paquete | Qué vive ahí | Ejemplo real |
|---|---|---|
| `command/primaryadapter/web/` (+`dto/`, `mapper/`) | Un `Controller` por acción | `RegistrarFichaPerfilController.java`, `dto/RegistrarFichaPerfil{Request,Response}DTO.java`, `mapper/RegistrarFichaPerfilRequestMapper.java` |
| `command/primaryadapter/amqp/{productor}/{entidad}/` | `Consumer` (extiende `AbstractEventConsumer`, o `AbstractNotificacionConsumer` en `notificaciones`) + su payload `record` **local** | `notificaciones/.../amqp/fichas/asesorficha/AsesorFichaCambiadoConsumer.java` |
| `command/secondaryadapter/entity/` (+`mapper/`, `repository/`) | JPA + `OutputAdapter` + repo Spring Data | `FichaPerfilJpaEntity.java`, `FichaPerfilJpaMapper.java`, `FichaPerfilCommandOutputAdapter.java`, `FichaPerfilCommandRepository.java` |
| `query/primaryadapter/web/` (+`dto/`, `mapper/`) | `Controller` de lectura + `{Entidad}ResponseDTO` + `{Entidad}ResponseMapper` + `Consultar{Entidad}[{Rol}]RequestMapper.toQuery(dto[, datoDelJwt])`, que arma el **`Query`**, nunca el `Criteria` | `ConsultarFichasPerfilCoordinadorController.java`, `FichaPerfilResponseMapper.java`, `ConsultarFichasPerfilCoordinadorRequestMapper.java` |
| `query/secondaryadapter/repository/` (+`mapper/`) | `@Subselect`/`@Immutable`/`@Synchronize`, plana; specification, sort, adapter, repo | `FichaPerfilJpaQueryEntity.java`, `FichaPerfilJpaSpecification.java`, `FichaPerfilSortMapper.java`, `FichaPerfilQueryOutputAdapter.java`, `FichaPerfilQueryRepository.java` |
| `{feature}/exception/` | Excepciones de infraestructura del feature (→ 503), dentro del slice | `seguridad/infrastructure/auth/exception/ProveedorIdentidadNoDisponibleException.java` |
| `security/`, `config/`, `filter/` | Transversales del contexto | `FichasAuthorities.java`, `FichasDataSourceConfig.java` |
| `handler/` | El `@RestControllerAdvice`, si lo hay — **nunca en `exception/`** (solo `seguridad` tiene uno) | `SeguridadGlobalExceptionHandler.java` |
| `src/main/resources/db/migration/{contexto}/` | Migraciones Flyway, subcarpeta propia obligatoria | `V20260504181427__crear_tablas_fichas_perfil.sql` |

## Quién orquesta a quién: el que llama compone, el llamado es un solo paso

Un caso de uso de escritura puede invocar a otro (`RegistrarFichaPerfil` encadena
`AsignarEstadoInicialFichaPerfil` y `AsignarEstudiantesFichaPerfil`), pero todos los pasos cuelgan del
**mismo orquestador**, nunca de un hermano:

```java
// RegistrarFichaPerfilUseCaseImpl
fichaPerfilOutputPort.registrarFicha(FichaPerfilMapper.toEntity(ficha));
asignarEstadoInicialFichaPerfilUseCase.ejecutar(registro.getEstadoInicial());  // recibe solo su parte
asignarEstudiantesFichaPerfilUseCase.ejecutar(registro.getEstudiantes());
eventPublisher.publish(new FichaPerfilRegistradaEvent(...));
```

**La señal es la firma:** un caso de uso encadenado recibe el objeto de dominio más estrecho que lee.
Si pide el objeto de acción completo y usa una parte, lo pide para alimentar un paso siguiente, que
pertenece al que llama. Enterrarlo en un hermano oculta el orden real de la transacción y obliga al
test del paso intermedio a verificar un encadenamiento que no es suyo.

## El `UseCase` de escritura nunca recibe un `Command`

`UseCase<{Algo}Domain, R>`, siempre: el `Command` pertenece al interactor y muere ahí, convertido por
el `{Accion}{Entidad}Mapper`. Vale también cuando el comando no crea ningún domain:
`ReintentarNotificacionesFallidas` recibe dos números y aun así los nominaliza en
`ReintentoNotificacionesDomain`.

La validación repetida entre `Command` y dominio no es redundancia: petición malformada → 400
(`ApplicationValidationException`), estado de dominio imposible → 422 (`DomainValidationException`).
En lectura, el use case recibe el `Criteria`.

## Cuando un comando devuelve un objeto: `command/result/`

Si el comando devuelve algo más que `UUID` o `void`, es un `{Concepto}Result` en
`application/{feature}/command/result/`, sin anotaciones ni Lombok. Un desenlace único es un `record`
(`AutenticacionResult`, `ReintentoNotificacionesResult`); varios desenlaces excluyentes son una
**`sealed interface` con un `record` por variante** (`EnvioNotificacionResult` → `Enviada`, `Duplicada`,
`Fallida(motivo)`), para que el consumidor haga un `switch` exhaustivo sin `default` y un desenlace
nuevo rompa la compilación. Precedente en contexto de negocio: `notificaciones`. Nunca se devuelve el
`Domain`, la `Entity` ni un DTO desde application.

| Paso | Quién | Qué hace |
|---|---|---|
| 1 | `{Concepto}ResultMapper` (`command/result/mapper/`) | `final`, ctor privado, `static toResult(...)`, desde lo que devolvió el puerto o desde el propio domain |
| 2 | `{Accion}{Entidad}UseCaseImpl` | **Llama** al `ResultMapper` y retorna el `Result` — no el `Interactor`, que solo declara el tipo y delega |
| 3 | `{Accion}{Entidad}Interactor` | `Interactor<{Accion}{Entidad}Command, {Concepto}Result>` con su `@Transactional` |
| 4 | `{Accion}{Entidad}ResponseMapper` (`web/mapper/`) | `static toResponse(result)` → `ResponseDTO`. El `Result` nunca se serializa directo, igual que el `ReadModel` |

Con varias ramas, el mapper expone **una fábrica por variante** en vez de recibir un `Optional` o
decidir dentro: `ValidacionTokenResultMapper.toResult(identidad)`/`toResultInvalido()`;
`EnvioNotificacionResultMapper.toResultEnviada/Duplicada/Fallida`, elegidas con un `switch` sobre el
`ResultadoEntrega` del puerto. `*Result` y `*ResultMapper` no están excluidos de JaCoCo: los cubre el
test del use case que asserta los campos.

## Una operación sin entrada: `SupplierInteractor` / `SupplierUseCase`

| | entrada | salida |
|---|---|---|
| `Interactor<I,O>` / `UseCase<I,O>` | sí | sí |
| `VoidInteractor<I>` / `VoidUseCase<I>` | sí | no |
| `SupplierInteractor<O>` / `SupplierUseCase<O>` | no | sí |

**`Void` como tipo de entrada está prohibido:** su único valor es `null`, así que obliga a todo
llamador a escribir `ejecutar(null)`. Referencia: `ConsultarEstadosFicha` (catálogo cerrado devuelto
entero). Igual para un `Finder` cuyo puerto no recibe argumentos (`AdministradoresVigentesCountFinder`
→ `contarVigentes()`): `SupplierFinder<R>` con `obtener()`, nunca `Finder<Void, R>` ni un parámetro
que la consulta ignora. Tampoco un `record` vacío como entrada ni el centinela `VACIO`, que representa
un dato ausente, no uno inexistente.

## Cuándo un grupo de campos se vuelve un value object

Por cohesión, no por número: extrae un `record` a `domain/{feature}/model/` cuando varios campos
**viajan siempre juntos y no cambian tras crearse** (`Destinatario(nombre, email)`,
`Contenido(asunto, cuerpo, pie)` en `NotificacionDomain`). No extraigas el grupo de ciclo de vida
(`estado`/`detalleError`/`fechaEnvio`/`intentos`): las transiciones lo mutan y un VO es inmutable.

- `crear(campos..., ValidationResult result)` — **recibe el `ValidationResult` del domain** y no lanza
  por su cuenta; si lanzara, `fieldErrors[]` dejaría de acumular. Campo inválido → `UtilTexto.VACIO`.
- `reconstruir(campos...)` — normaliza (`aplicarTrim`) pero no valida.
- `VACIO` + `esVacio()`, igual que un domain.
- El `crear(...)` del domain sigue recibiendo los **campos sueltos**, no los VO ya construidos, para
  que la validación ocurra dentro del domain.

## El `ReadModel` nunca sale por HTTP

El `Controller` de lectura mapea `ReadModel` → `{Entidad}ResponseDTO` con `{Entidad}ResponseMapper`
(`final`, ctor privado, `static toResponse`); la política de serialización vive en el DTO. Paginado:
`PageResponseDTO.from(resultado.map({Entidad}ResponseMapper::toResponse))`
(`ConsultarFichasPerfilCoordinadorController.java`). Un `ReadModel` anidado pertenece a la feature que
describe: `asesorficha` posee `AsesorFichaReadModel` y su `ResponseDTO` sin `UseCase` propio.

## Convención de sufijos

`Domain · Interactor/InteractorImpl · UseCase/UseCaseImpl · Validator/ValidatorImpl ·
Rule/RuleImpl · Finder/FinderImpl · OutputPort · QueryOutputPort · Entity · JpaEntity ·
JpaQueryEntity · Command · Query · Criteria · ReadModel · Result/ResultMapper ·
RequestDTO/ResponseDTO · RequestMapper/ResponseMapper · Controller (web) · Consumer (AMQP) ·
CommandOutputAdapter · QueryOutputAdapter · SortMapper · JpaSpecification`.

Español para el concepto, inglés para el sufijo (`Controller`, no `Controlador`). Paquete de feature en
minúsculas sin separadores (`fichaperfil`). **Cada paquete se llama como el sufijo de sus clases**
(`mapper/` → `*Mapper`, `exception/` → `*Exception`), por eso un `@RestControllerAdvice` va en
`handler/`, no en `exception/` ni en `advice/` (`shared/web/handler/GlobalAppExceptionHandler`).

## Dónde vive cada excepción: en el slice, y en la capa de su clase base

No hay `exception/` a nivel de contexto:

| Excepción | Capa y paquete | HTTP |
|---|---|---|
| La que lanza un `Rule` (no encontrado, duplicado, propiedad) | `domain/{feature}/exception/` | 422 |
| La de la orquestación de application | `application/{feature}/exception/` | 400 |
| Fallo real de infraestructura, desde un `OutputAdapter` | `infrastructure/{feature}/exception/` | 503 |

**Toda la jerarquía de un concepto va junta:** `AutenticacionException` y sus subclases
`CredencialesInvalidasException`/`TokenInvalidoException` viven en `seguridad/application/auth/exception/`;
`ProveedorIdentidadNoDisponibleException` (Keycloak caído) baja a `infrastructure/auth/exception/`. Una
subclase en distinta capa que su padre parte la jerarquía en dos módulos.

### Un fallo que el negocio registra no es una excepción: es un valor

Si quien llama captura la excepción para seguir adelante —el fallo es un estado a persistir—, no debía
ser excepción. El puerto devuelve una sellada y desaparece el `try/catch` de application:

```java
public sealed interface ResultadoEntrega {
    record Entregada() implements ResultadoEntrega {}
    record Rechazada(String motivo) implements ResultadoEntrega {}
}
```

El adaptador traduce lo que sabe diagnosticar y registra ahí la traza técnica; una avería inesperada
sí se propaga (DLQ o 503). Referencia: `EnvioNotificacionOutputPort` +
`SmtpEnvioNotificacionOutputAdapter`. Si application tiene que **nombrar** una excepción del adaptador,
vive junto al puerto (`command/secondaryport/exception/`): es parte de su contrato.

## Un módulo `shared:` con un solo consumidor no es compartido

Un `shared:*` de un solo cliente es un contexto mal ubicado, y puede meter infraestructura en el
classpath de application sin que `verificarCapasHexagonales` lo vea (razona por nombre de módulo):
`shared:notification` traía `JavaMailSender` a `notificaciones/application` y se disolvió dentro del
contexto. **Exige dos consumidores reales antes de crear uno.**

### Un record repetido no siempre es duplicación: se comparte por significado, no por forma

`ContactoAsesor(nombre, email)` y `ContactoEstudiante(nombre, email)` son idénticos y **no** se funden
en un `Contacto` compartido: DRY habla de conocimiento, no de estructura, y compartir mal acopla el
payload de varios eventos (añadir un `telefono` cambiaría el contrato de todos sus consumidores).
Reconsidéralo solo si tres features tienen el mismo record **y cambian por la misma razón**. Lo que sí
es error: el mismo concepto con dos formas según el evento (un destinatario en campos planos en uno y
como record en otro). Un destinatario se modela igual en todos los eventos del contexto.

## Puertos: hablan `Entity`, nunca `Domain`

`domain/` no declara puertos ni hace I/O. El `OutputPort` habla el `record` plano `{Entidad}Entity`
(`command/secondaryport/entity/`), sin JPA ni Lombok; una relación viaja como id desnudo.

- **UseCase**: `Domain → Entity` antes del puerto (`{Entidad}Mapper.toEntity`).
- **Finder**: `Entity → Domain` al volver.
- **OutputAdapter**: `Entity ↔ JpaEntity` (`{Entidad}JpaMapper`).

## El `CommandOutputAdapter` es pura delegación

`FichaPerfilCommandOutputAdapter` es la referencia: cero `try/catch`. Cuando la ejecución llega al
adaptador, el orden de validación ya garantizó formato, existencia, unicidad e invariantes; no queda
error de negocio que traducir.

| Anti-patrón | Por qué está prohibido |
|---|---|
| `catch (DataIntegrityViolationException)` → `throw {X}DuplicadoException` | Infrastructure no ve el dominio, y la unicidad ya la declara `{X}UnicoRule` vía `Finder` en el paso 2. La garantía real es el `UNIQUE` de la migración |
| `catch (DataAccessException)` → `InfrastructureException` | Sobra: `GlobalAppExceptionHandler` lo deja caer al catch-all → 500 con log, que es lo correcto para "BD caída". El `catch` esconde la causa raíz |
| `saveAndFlush(...)` | Ante una violación deja la transacción *rollback-only* y el `UnexpectedRollbackException` aparece en el commit, lejos del origen. Usa `save` (en el *arrange* de un `@DataJpaTest` sí es legítimo) |
| `EntityManager` en el adaptador (`createNativeQuery`, `Object[]`), o un `@Query` que devuelve columnas de otra tabla | Cada tabla que el comando toca, aunque solo la lea para una `Rule`, tiene su `{Entidad}JpaEntity` de comando y su `{Entidad}CommandRepository` en su feature (CQRS prohíbe importar el `JpaQueryEntity`, no mapear la tabla dos veces). Con `EntityManager` el SQL no se valida al arrancar y se mapea por posición: un cambio rompe en silencio. SQL propio → `@Query`/`@NativeQuery` con proyección tipada en el `CommandRepository`; un `JOIN` para filtrar es legítimo. Referencia: `EstudianteCommandRepository` (`findIdsVigentesByIdIn`, `UPDATE` con `@Modifying`) |
| `Boolean existePorX(...)` en puerto/adaptador | `boolean` primitivo: el envuelto introduce un `null` posible y un unboxing silencioso en la `Rule`. El `Finder<T, Boolean>` sí lleva el envuelto por el genérico |
| Método de escritura sin log | Los de escritura registran `logger.debug({Feature}Key.LOG_GUARDADA, id)`; los de lectura no logean. Estructura completa en `arquisoft-estandares` |

Un adaptador sí puede lanzar una `InfrastructureException` **propia** (`infrastructure/{feature}/exception/`)
para fallos que solo él diagnostica (proveedor externo caído, objeto ausente en MinIO). Lo prohibido es
envolver Spring Data y lanzar excepciones de dominio.

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

`query/secondaryadapter` **nunca** importa nada de `command/secondaryadapter`, ni el `JpaEntity`. El
lado lectura declara su `{Entidad}JpaQueryEntity` con `@Subselect` (join resuelto en SQL → entidad
plana, sin `@ManyToOne`), `@Immutable` y `@Synchronize({...})`. `{Entidad}QueryRepository` extiende
`QueryRepository`/`SpecificationQueryRepository` (`shared:jpa`), **nunca `JpaRepository`**, para no
heredar `save`/`delete` (`FichaPerfilQueryRepository.java`). El `QueryOutputAdapter` es pura
delegación: `PageableMapper.toPageable(criteria, {Entidad}SortMapper::traducir)` a la entrada y
`PaginationMapper.toResult(page)` a la salida; no arma `PageRequest`/`Sort` a mano ni remapea
excepciones.

## Cuándo NO existe un paquete `query/`

`query/` existe si la feature tiene una **lectura real alcanzada por un `primaryport`**, o si una
consulta necesita comprobar algo de ella para su política de acceso. **El lado lo decide quién
pregunta**, no de quién es el dato: un chequeo que alimenta una `Rule` de comando va en el `OutputPort`
de `command/` vía `Finder`, aunque el dato sea de otra feature; uno que alimenta el `Validator` de una
consulta va en `query/finder/` + `query/secondaryport/` de la feature consultada (`evaluacionjurado/query/`,
sin `primaryport` propio). Ejemplo: `RegistrarFichaPerfilUseCaseImpl` usa `AsesorFichaExisteFinder` →
`AsesorFichaOutputPort.existePorId(...)` (puerto de **command**); no hay `AsesorFichaQueryOutputPort`.

## `shared:domain` vs `shared:application` — la frontera la sostiene el compilador

`shared:domain` solo tiene `DomainEvent` (`com.arquisoft.shared.events`) y `DomainRule`
(`com.arquisoft.shared.rules`). `shared:application` tiene `UseCase`/`VoidUseCase`/`SupplierUseCase`
(`…shared.usecase`), `Interactor`/`VoidInteractor`/`SupplierInteractor` (`…shared.interactor`),
`Finder`/`SupplierFinder` (`…shared.finder`) y `EventPublisher` (`…shared.publisher`). Así "el dominio
no orquesta" es un hecho de compilación.

| Módulo | Declara | Por qué |
|---|---|---|
| `{contexto}/domain` | `shared:domain` | Trae por `api` `message`/`exception`/`validation`/`util` |
| `{contexto}/application` | `shared:application` | Trae `shared:domain` por `api` |
| `{contexto}/infrastructure` | ambos | El controller inyecta el `Interactor`; el consumer toca `DomainEvent` |

**Un `{contexto}/domain/build.gradle` nunca declara `shared:application`.** Si al compilar el dominio
falta `UseCase`, `Interactor`, `Finder` o `EventPublisher`, el tipo está en la capa equivocada.

## Aislamiento de persistencia: una base de datos por contexto

`init-db.sql` crea una **base** por contexto (no schemas); cada `{Contexto}DataSourceConfig` levanta su
`DataSource`, `EntityManagerFactory`, `TransactionManager` y `Flyway`. `seguridad` no tiene.

- **Cada base tiene su `flyway_schema_history`:** el bean apunta a
  `classpath:db/migration/{contexto}`; una migración suelta en `db/migration/` la aplicarían todos.
- **`setPackagesToScan` recibe un solo paquete,** `"com.arquisoft.{contexto}.infrastructure"`, nunca
  también `application`: ahí solo está el `record` plano del puerto, sin JPA.
- **`baselineOnMigrate = false`**, heredado al copiar el config (`BibliotecaDataSourceConfig` es el más
  reciente): una base con objetos preexistentes o una versión fuera de orden falla el arranque.
- **Versión timestamp `VyyyyMMddHHmmss`** tomada al crear el archivo; dos de la misma entrega se
  separan por un segundo. Nunca se retrocede, renombra ni edita una migración aplicada: se agrega otra.
- **No hay FK hacia otro contexto.** El dato se modela como réplica local poblada por eventos
  (`asesor_ficha`, `estudiante` en `fichas`), o, para un chequeo puntual de escritura, como *Consulta
  síncrona entre contextos* (`references/consultas-sincronas.md`). Nunca tabla compartida ni import.
- **Una réplica con `eliminado_en` se lee declarando la vigencia** (las bajas no se borran):
  - **Crear un vínculo nuevo** → solo vigentes, con un `{Entidad}sVigentesFinder` sobre
    `eliminadoEn IS NULL` (`EstudiantesVigentesFinder` → `findIdsVigentesByIdIn`).
  - **Operar sobre lo ya vinculado** (remover, modificar) → sin filtro, para poder desvincular a un dado
    de baja (`EstudiantesExistentesFinder` en `RemoverEstudianteFichaPerfilUseCaseImpl`).
  - **Lectura** → solo vigentes, salvo la vista de **quien administra el vínculo**, que incluye las bajas
    marcadas con `boolean vigente` en `ReadModel` y `ResponseDTO` (`EstudianteFichaPerfilJpaQueryEntity`:
    `(e.eliminado_en IS NULL) AS vigente`; coordinador → `consultarPorFicha`, estudiante →
    `consultarVigentesPorFicha`). Un historial pedido por la HU también incluye bajas.

  Un `existsById` o un `JOIN` sin filtro pasa los tests con datos frescos y en producción deja asignar o
  listar a alguien dado de baja.
- `@Table` sin `schema` ni catálogo; el SQL no prefija nombres de base.

## Referencias bajo demanda — `references/`

No aplican a toda HU. **Antes de planificar, implementar, testear o validar la parte correspondiente,
abre el archivo con `Read`**: es igual de vinculante. Ante la duda, ábrelo.

| Abre | Cuando la HU… | Secciones |
|---|---|---|
| `references/eventos.md` | publica o consume un evento, crea o cambia un estado (⇒ notificación), o mantiene una réplica de otro contexto | *Eventos de dominio — una sola forma* · *El evento se emite donde ocurre el hecho* · *Transición de estado ⇒ notificación* (las ocho piezas) · *La routing key se declara una vez* · *Consumidores: primero el productor* · *Una cola de evento se declara con `ColaEvento`* · *El `nack` distingue fallo transitorio* · *Un efecto externo se reintenta desde la base* · *Replicación entre contextos* (incluye el nombrado de beans por FQN) · *Saga* |
| `references/consultas-sincronas.md` | necesita verificar al escribir un dato cuyo dueño es otro contexto | *Consultas síncronas entre contextos* |

## Rutas y autorización viven en constantes, no en literales

- `@RequestMapping("${rutas.fichas.fichas-perfil.base:/fichas-perfil}")` — placeholder con default; **no
  existe `{Contexto}Routes`**. Nunca el prefijo `/api`: ya es el `context-path`.
- `@PreAuthorize(FichasAuthorities.Expresiones.HAS_FICHA_PERFIL_CREATE)`, nunca el literal
  `"hasAuthority('...')"`. `FichasAuthorities` declara el client role crudo (para tests) y su SpEL.
- **Un client role por endpoint, propio y distinto**, para concederlo o revocarlo por separado en
  Keycloak. Dos endpoints sobre el mismo recurso se diferencian con un calificador:
  `fichas:ficha-perfil-coordinador:view` vs `fichas:ficha-perfil-asesor:view`.
