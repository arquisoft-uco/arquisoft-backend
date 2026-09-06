# PLAN: Enviar solicitud de novedad para el coordinador (+ bootstrapping del contexto `solicitudes`)

## Metadata
- **ID Historia:** HU-081
- **Bounded Context:** `solicitudes` (NUEVO — no existe hoy en el backend)
- **Tipo de Use Case:** Escritura
- **Módulos Gradle afectados:** `solicitudes` (nuevo), `solicitudes:domain` (nuevo), `solicitudes:application` (nuevo), `solicitudes:infrastructure` (nuevo). Además tocan raíz: `settings.gradle`, `init-db.sql`, `docker-compose.yml`, `.env.example`, `src/main/resources/application.yml`, `src/main/resources/application-prod.yml`, `shared:message`, `catalogo/`.
- **Fecha de plan:** 2026-08-27
- **Rama sugerida:** `feature/HU-081-enviar-solicitud-novedad-coordinador`
- **Fuentes consultadas:**
  - `artefactos/estrategicos/propuestas-hu/historias_usuario_priorizadas.md` (HU081, item #245)
  - `artefactos/estrategicos/event-storming/Solicitudes - Event Storming.md` (acción "Enviar solicitud de novedad para el coordinador", evento, POL-01/03/06)
  - `artefactos/estrategicos/modelo-dominio/enriquecido/documentacion/15_solicitudes_modelo_enriquecido.md` (agregado Solicitud, tipos, longitudes, restricción única)
  - `artefactos/estrategicos/modelo-dominio/anemico/documentacion/15_delimitar_contextos_solicitudes.md` (dependencia de `usuarios`, vistas materializadas Destinatario/Remitente)
  - `mer/11_tablas_solicitudes.sql` (DDL exacto)
  - `mer/data/11_data_solicitudes.sql` (constantes de catálogo `tipo_solicitud` y `estado_respuesta`)
  - `artefactos/estrategicos/event-storming/Usuario - Event Storming.md` (eventos de `usuarios`)
  - Código real de `fichas` como patrón: `FichasDataSourceConfig`, `FichasUsuariosQueueConfig`, `UsuarioCreadoConsumer`/`UsuarioCreadoPayload`, `RegistrarFichaPerfil*` (flujo de comando completo), migraciones `db/migration/fichas/` (réplicas `asesor_ficha`/`estudiante`), `EstadoFicha` (enum de catálogo con tabla propia), `catalogo/fichas.properties`, `ClavesCatalogo`.
  - `src/main/java/com/arquisoft/config/outbox/ContextAwareEventPublicationRepository.java` (el outbox autodescubre cualquier `DataSource` con tabla `event_publication`).
  - `notificaciones`: `AsesorFichaCambiadoConsumer`, `NotificacionesFichasQueueConfig` (referencia — NO se cablea en HU081).
  - ADR-001 (monolito modular asíncrono, el evento es el contrato), ADR-008/009/010/011, ADR-012 v1.1.
- **Observaciones del usuario:** El plan es ÚNICO e incluye el bootstrapping completo del contexto `solicitudes` + el caso de uso de HU081. Preguntas abiertas resueltas por el usuario el 2026-08-27 (ver sección 0).

---

## 0. Decisiones sobre las preguntas abiertas (RESUELTAS — 2026-08-27)

| # | Pregunta | Decisión del usuario |
|---|---|---|
| **P1** | ¿Cómo se pueblan las réplicas `usuario` / `remitente` / `destinatario`? El evento actual `UsuarioCreadoEvent(usuarioId, email, rol)` no trae `identificador` ni `nombre`. | **NO se debilita la proyección.** `usuario.identificador` y `usuario.nombre` quedan **NOT NULL** (como el MER). El contrato correcto es que `usuarios` publique `UsuarioCreadoEvent` enriquecido `{usuarioId, identificador, nombre, email, estado, rol}` + `UsuarioActualizadoEvent` + `UsuarioDesactivadoEvent` — **eso NO es parte de HU081**, queda documentado como dependencia externa (sección 4.4). Para HU081: el `UsuarioCreadoConsumer` de `solicitudes` se construye contra la **forma enriquecida completa** pero queda **inerte** (no-op ante payload sin `identificador`/`nombre`) hasta que `usuarios` publique. Para poblar `usuario` en local se ejecuta a mano el bloque SQL de referencia de **§11.2** (data demo documentada en el plan, **sin archivo en el repo**). |
| **P2 / P3** | ¿HU081 emite evento y con qué alcance? ¿Incluye correo al coordinador (`notificaciones`)? | **RESUELTO FINAL (ajuste post-validación autorizado por el usuario 2026-08-27):** el evento sigue el **camino estándar, exactamente como `fichas`** — el `UseCase` inyecta el puerto `EventPublisher` de `shared:application` y llama `eventPublisher.publish(...)` tras persistir, dentro de la tx del interactor; el insert en el outbox (`event_publication`) engancha por esa transacción (`solicitudesTransactionManager`), y `ModulithAmqpExternalizationConfig` externaliza cualquier `DomainEvent` al exchange `arquisoft.events` con routing key = `getTemaEvento()`. **Ya NO es una desviación** — es el patrón único de `arquisoft-arquitectura`. **Alcance: SOLO lado publicación** — sin consumidor, sin `notificaciones`, sin `Queue`/`Binding` para este evento (se acepta el warning de publisher-return del broker hasta que exista un consumidor, igual que cualquier evento sin cola). Se crea la migración `event_publication` en `solicitudes` (autodetectada por `ContextAwareEventPublicationRepository`). El `@Transactional` del interactor ahora **sí** aporta atomicidad de outbox además de la unidad de trabajo. <br><br> _(Histórico — decisión original, revertida: evento in-process vía `ApplicationEventPublisher` envuelto en puerto local `SolicitudEventoOutputPort`, sin outbox ni AMQP.)_ |
| **P4** | ¿De dónde sale el `destinatario` (coordinador)? | **`destinatarioId` viaja en el body del request DTO** (id de usuario del coordinador). `solicitudes` **NO** resuelve el coordinador desde `proyectos`: el modelo enriquecido de Solicitud no tiene atributo de proyecto y la tabla `solicitud` del MER no tiene `proyecto_grado_id` — se ignora la columna del borrador `flujo-gestion-solicitudes.md`, **manda el MER**. El servicio solo valida que el destinatario exista en la proyección local `usuario`. Cero dependencia de `proyectos`. |
| **P5** | ¿`TipoSolicitud` es entrada del usuario o fija del endpoint? | **Aceptado el supuesto por defecto:** endpoint específico; el `tipo` **no se recibe**, lo fija el use case a `TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR`. POL-06 satisfecha por construcción → **no hay `Rule` de POL-06**. |
| **P6** | Ubicación del enum `TipoSolicitud`. | **Aceptado el supuesto por defecto:** como `tipo_solicitud` tiene tabla Postgres propia, vive en `domain/tiposolicitud/` con `exception/` hermano (patrón `EstadoFicha`). |
| **P7** | Alcance del scaffold. | **Aceptado el supuesto por defecto:** solo lo mínimo para HU081. `SolicitudesDataSourceConfig` completo (DataSource + EMF + TxManager + Flyway) como `fichas`. Las tablas `respuesta` / `estado_respuesta` se crean en la migración base (para no fragmentar migraciones) pero sin código Java. |
| **P8** | `remitente` / `destinatario`: get-or-create automático, o exigir que ya existan. | **Aceptado el supuesto por defecto.** Runtime: get-or-create perezoso dentro del use case, idempotente por `UNIQUE(usuario_id)` — es la vía normal y la única en el repo. Las filas `remitente`/`destinatario` **no** se siembran: las crea el get-or-create. Para tener usuarios de prueba en un entorno local, el plan documenta en **§11.2** un bloque `INSERT ... ON CONFLICT DO NOTHING` de referencia que se corre a mano con `psql`. **No hay archivo de seed en el repo** (decisión del usuario 2026-08-28): no existe convención de `db/seed/` — `fichas` no la tiene — y un `.sql` de datos demo empaquetado en el jar es ruido; una migración Flyway, además, corre en todos los entornos (prod incluido) y es inmutable tras aplicarse. |
| **P9** | Client role exacto. | **Aceptado el supuesto por defecto:** `solicitudes:solicitud:create`, poseído por el rol realm `estudiante`. |
| **P10** | ¿HU081 lleva `command/primaryport/mapper/`? | **SÍ (decisión explícita del usuario 2026-08-27).** No un mapper de paso, sino con **objeto de acción**: "Enviar solicitud" arrastra más que el agregado — además del `SolicitudDomain` puede materializar un `RemitenteDomain` y/o un `DestinatarioDomain` nuevos (get-or-create). Se introduce `EnvioSolicitudNovedadCoordinadorDomain` (`domain/solicitud/`, nominalización "enviar" → "Envío", análogo a `RegistroFichaPerfilDomain`) + `EnviarSolicitudNovedadCoordinadorMapper` (`application/solicitud/command/primaryport/mapper/`). Ver secciones 4.2, 6.3, 6.6, 7. |

Preguntas de FASE 3 restantes (2, 6, 7, 10, 11, 12) resueltas con los valores del plan: Escritura · enum en `domain/{catalogo}/` · persistencia nueva · sin RabbitMQ · retorna UUID envuelto en ResponseDTO · sin recurso con dueño (remitente = `sub` del JWT). Observación de cierre: ninguna.

---

## 1. Resumen Funcional

Un **Estudiante** autenticado envía una **Solicitud** de tipo *Novedad para el Coordinador*: un mensaje libre (1–100 caracteres) dirigido a un coordinador. El sistema valida el formato, confirma que remitente y destinatario existen como usuarios conocidos en la proyección local, garantiza que no exista una solicitud idéntica (misma combinación destinatario + remitente + fecha + mensaje), persiste la solicitud con su fecha de creación y **publica el evento de dominio** `SolicitudNovedadCoordinadorEnviadaEvent` por el camino estándar (`EventPublisher` + outbox de Spring Modulith + externalización AMQP), como en `fichas`. Sin consumidor todavía (solo lado publicación).

Como es la **primera HU del contexto `solicitudes`**, este plan también incluye el *bootstrapping* completo: módulos Gradle, base de datos `solicitudes`, `SolicitudesDataSourceConfig`, variables de entorno, migración Flyway base (todas las tablas del MER + datos de catálogo), migración `event_publication` (outbox), y el consumidor AMQP `usuarios.usuario.creado` (listo pero inerte hasta que `usuarios` enriquezca su evento — P1). Los datos demo para local se documentan como bloque SQL de referencia en §11.2 (sin archivo en el repo).

**NO cubre:** eliminar/consultar/responder solicitudes, los otros 4 tipos de "enviar solicitud", el envío de correo al coordinador, ninguna integración RabbitMQ del lado publicación, ni el enriquecimiento del evento de `usuarios` (dependencia externa — sección 4.4).

---

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|---|---|
| CA-1 | Estudiante autenticado con rol `estudiante` envía `{ destinatario, mensajeSolicitud }` válidos | `201 Created` + `{ "id": "<uuid>" }`; fila en `solicitud` con `tipo_solicitud_id = 'NOVEDAD_PARA_EL_COORDINADOR'` y `fecha_creacion` = instante del servidor |
| CA-2 | `mensajeSolicitud` en blanco / nulo | `422` con `fieldErrors[]` (`mensajeSolicitud`) |
| CA-3 | `mensajeSolicitud` de más de 100 caracteres | `422` con `fieldErrors[]` (`mensajeSolicitud`) |
| CA-4 | `destinatario` ausente o no es UUID | `422` con `fieldErrors[]` (`destinatario`) |
| CA-5 | `destinatario` es UUID válido pero no corresponde a ningún usuario de la proyección local | `422` `DESTINATARIO_NO_ENCONTRADO` |
| CA-6 | El `sub` del JWT no corresponde a ningún usuario de la proyección local | `422` `REMITENTE_NO_ENCONTRADO` |
| CA-7 | Ya existe una solicitud con la misma combinación (destinatario, remitente, fecha, mensaje) | `422` `SOLICITUD_DUPLICADA` |
| CA-8 | Sin token | `401` |
| CA-9 | Token válido sin el client role `solicitudes:solicitud:create` | `403` |
| CA-10 | Solicitud creada correctamente | El `UseCase` llama `eventPublisher.publish(new SolicitudNovedadCoordinadorEnviadaEvent(...))` tras persistir; el evento entra al outbox (`event_publication`) en la tx del interactor y se externaliza a `arquisoft.events` con routing key `solicitudes.solicitud.novedad_coordinador_enviada`. Sin consumidor hoy. Observable en test con `verify(eventPublisher).publish(captor)` sobre `getTemaEvento()`/`solicitudId` |
| CA-11 | Segunda solicitud del mismo estudiante a otro coordinador | Se reutiliza la fila `remitente` existente (get-or-create idempotente), se crea nueva fila `destinatario` si el coordinador no la tenía |

---

## 3. Reglas de Negocio

> Invariante LOCAL (formato, longitud, obligatoriedad de la propia instancia) → dentro del `SolicitudDomain`, acumulado en `ValidationResult` → 422 con `fieldErrors[]`. Restricción de CONJUNTO (unicidad, existencia) → `Rule` de dominio con su record de entrada, orquestada por el `EnviarSolicitudNovedadCoordinadorValidator` sobre lo que los `Finder`s ya trajeron → 422 con su propia `DomainException`. Nunca `if/throw` de negocio en el use case.

| # | Regla | Dónde se valida | Finder que trae el dato | Excepción → HTTP |
|---|---|---|---|---|
| RN-1 (POL-01) | `mensajeSolicitud`: obligatorio, 1–100, trim inicio+fin | `SolicitudDomain.crear` (invariante local) + `EnviarSolicitudNovedadCoordinadorCommand.crear` (formato de request) | — | `DomainValidationException` 422 + `fieldErrors[]` |
| RN-2 (POL-01) | `destinatario`: obligatorio, UUID válido | `EnviarSolicitudNovedadCoordinadorCommand.crear` (`ValidatorUUID`) | — | `DomainValidationException` 422 + `fieldErrors[]` |
| RN-3 | El **remitente** (usuario del `sub` del JWT) debe existir en la proyección local `usuario` | `UsuarioExisteFinder` → `UsuarioOutputPort.existePorId` | `UsuarioExisteFinder` | `RemitenteNoEncontradoException` (dominio, feature `solicitud`) → 422 |
| RN-4 (POL-01) | El **destinatario** debe existir en la proyección local `usuario` | `UsuarioExisteFinder` → `UsuarioOutputPort.existePorId` | `UsuarioExisteFinder` (reutilizado, 2 consumidores) | `DestinatarioNoEncontradoException` (dominio, feature `solicitud`) → 422 |
| RN-5 (POL-03 / `uk_solicitud_unica`) | No debe existir otra solicitud con la misma combinación `(destinatario_id, remitente_id, fecha_creacion, mensaje_solicitud)` | `SolicitudDuplicadaFinder` → `SolicitudOutputPort.existePorCombinacionUnica(...)` | `SolicitudDuplicadaFinder` | `SolicitudDuplicadaException` (dominio, feature `solicitud`) → 422 |
| RN-6 (POL-06) | El tipo de solicitud es *Novedad para el Coordinador* | **Por construcción** — el use case fija `TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR`, no se recibe. **No hay `Rule`** (P5). | — | — |
| RN-7 (POL-10) | El **destinatario** debe ser el **coordinador asignado** al proyecto de grado del remitente (consulta síncrona a `proyectos`) | `EnviarSolicitudNovedadCoordinadorValidator.validarAsignacionDestinatario` → `DestinatarioAsignadoRule` sobre `ExistenciaAsignacionResponsable` | `DestinatarioAsignadoFinder` → `AsignacionProyectoOutputPort.esCoordinadorAsignado` | `DestinatarioNoAsignadoException` (dominio, feature `solicitud`) → 422 |

**Orden de validación:** 1) integridad del dato (`Command.crear` + `SolicitudDomain.crear`) → 2) existencia y asignación contra sistemas externos (RN-3, RN-4 → RN-7 → RN-5) → 3) reglas del agregado (ninguna extra). Cada regla confía en que la anterior ya lanzó.

### 3.1 RN-7 — activación diferida (STUB hoy)

La regla, el puerto (`AsignacionProyectoOutputPort`), el finder y la llamada desde el `UseCase`/`Validator` **están cableados y activos**. Lo que falta es la fuente del dato:

- El coordinador↔estudiante vive en `proyectos_grado` (`proyecto_grado.coordinador_id`), contexto que hoy es **scaffolding** (solo `ProyectosDataSourceConfig`).
- El transporte es una **consulta síncrona vía `shared:web-client`** (módulo que aún no existe — dueño: HT de web-client). Ver CLAUDE.md § *Synchronous cross-context queries*.

Mientras tanto, `AsignacionProyectoOutputAdapter` (`infrastructure/asignacionproyecto/command/secondaryadapter/webclient/`) es un **stub**: devuelve `true` y loguea `warn`. Consecuencia: **RN-7 no rechaza nada todavía**. Registrado en CLAUDE.md § *Known deviations*.

**Checklist de activación (cuando existan `shared:web-client` y la query de `proyectos`):**
1. Crear `AsignacionProyectoWebClientOutputAdapter` que consuma `shared:web-client` → endpoint de `proyectos` (reutilizar la consulta que ya devuelve el coordinador/asesor del proyecto), reenviando el bearer del llamante; fallo de transporte → `InfrastructureException` (503).
2. Config `clientes.proyectos.base-url` en `application.yml`.
3. Borrar el stub (`AsignacionProyectoOutputAdapter`), la clave `SolicitudKey.LOG_ASIGNACION_NO_VERIFICADA` y su línea en `catalogo/solicitudes.properties`.
4. Quitar la fila del stub en CLAUDE.md § *Known deviations*.
5. HU-082 (novedad asesor) y HU-083 (cambio de asesor) replican el mismo patrón — HU-082 sobre `asesor_proyecto_grado` (es uno de los asesores activos), HU-083 sobre `coordinador_id` como esta. **Supuesto para HU-082, pendiente de confirmar con negocio:** el asesor destinatario es el del proyecto de grado, no el de la ficha de perfil.

**Nota sobre RN-5 y la fecha:** `fecha_creacion` se genera en `SolicitudDomain.crear(...)` con la hora del servidor. En la práctica dos peticiones idénticas casi nunca colisionan (difieren en microsegundos); la `Rule` + el `UNIQUE` de Flyway son el backstop de integridad exigido por el MER y el orden de validación.

**POL-01 sobre remitente/destinatario "válidos a nivel de tipo de dato…":** la validación de existencia (RN-3/RN-4) contra la proyección local es la interpretación aplicable; `solicitudes` no valida atributos internos del usuario (nombre, email) porque son datos que posee `usuarios`, no `solicitudes`.

---

## 4. Modelo DDD del Contexto

### 4.1 Entidad raíz
- **Clase:** `SolicitudDomain` (`domain/solicitud/SolicitudDomain.java`)
  - Campos privados no-`final`: `id: UUID` (autogen), `destinatario: UUID` (id de fila `destinatario`, no modificable), `remitente: UUID` (id de fila `remitente`, no modificable), `fechaCreacion: LocalDateTime` (autogen), `mensajeSolicitud: String` (1–100, trim), `tipoSolicitud: TipoSolicitud`.
  - Notification Pattern: constructor privado, setters privados que cortan con `return`, solo getters, `crear(destinatario, remitente, mensajeSolicitud, tipoSolicitud)` + `reconstruir(id, destinatario, remitente, fechaCreacion, mensajeSolicitud, tipoSolicitud)`. Centinela `VACIO` + `esVacio()`. Sin Lombok.
  - `crear(...)` genera `id` (`UtilUUID.generarNuevoUUID`) y `fechaCreacion` (hora del servidor), valida `mensajeSolicitud` (`ValidatorTexto.noEnBlanco` → `ValidatorLongitud.longitudEntre(1, SolicitudesLimits.Solicitud.MENSAJE_MAX)` → `UtilTexto.aplicarTrim`), valida `destinatario`/`remitente`/`tipoSolicitud` (`ValidatorObjeto.noNulo`), cierra con `result.lanzarSiTieneErrores()`.

### 4.2 Objeto de acción — `EnvioSolicitudNovedadCoordinadorDomain`

`domain/solicitud/EnvioSolicitudNovedadCoordinadorDomain.java` — al lado del agregado, **sin subpaquete** (igual que `RegistroFichaPerfilDomain` vive en `domain/fichaperfil/`). Nombre = nominalización del verbo "enviar" → "Envío" (regla de CLAUDE.md: `Registro`/`Cambio`/`Envío`…; se prefiere `Envio…` sobre `RegistroSolicitud…` porque el nombre debe seguir al comando de la HU, "Enviar solicitud", no a un "Registrar" que la HU no usa).

**Por qué existe** (y no un mapper de paso): "Enviar solicitud" arrastra más que el `SolicitudDomain`. Además del agregado, la acción **puede materializar** un `RemitenteDomain` y/o un `DestinatarioDomain` nuevos — el get-or-create de las filas de rol por `usuario_id`. Ese *bundle* (solicitud + remitente + destinatario) es lo que justifica el objeto de acción, igual que `RegistroFichaPerfilDomain` agrupa `FichaPerfilDomain` + `EstadoFichaPerfilDomain` + `AgregacionEstudiantesFichaPerfilDomain`.

**Qué agrupa** (3 objetos de dominio, todos construidos por el mapper — sección 6.6/7):
| Getter | Tipo | Contenido |
|---|---|---|
| `getSolicitud()` | `SolicitudDomain` | Agregado construido por el mapper con los **ids candidatos** de remitente/destinatario (`remitente.getId()` / `destinatario.getId()`), el `mensajeSolicitud` del comando y `TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR`. Su `crear(...)` es donde se validan los invariantes locales (RN-1) en tiempo de mapeo |
| `getRemitente()` | `RemitenteDomain` | Candidato `RemitenteDomain.crear(command.remitenteUsuario())` — id nuevo, estado *deseado* si el remitente no existiera aún |
| `getDestinatario()` | `DestinatarioDomain` | Candidato `DestinatarioDomain.crear(command.destinatarioUsuario())` — análogo |

`EnvioSolicitudNovedadCoordinadorDomain.crear(SolicitudDomain, RemitenteDomain, DestinatarioDomain)` valida no-nulo de los tres con `ValidatorObjeto.noNulo` acumulando en `ValidationResult` (Notification Pattern), exactamente como `RegistroFichaPerfilDomain.crear`. Expone además `getRemitenteUsuario()` / `getDestinatarioUsuario()` como atajos (`remitente.getUsuario()` / `destinatario.getUsuario()`) para el orden de validación del use case.

**Quién hace qué (sin ambigüedad):**
- El **mapper** (`primaryport/mapper/`, `static`, sin I/O) construye el *bundle* con estado deseado: candidatos `RemitenteDomain`/`DestinatarioDomain` (ids nuevos) y un `SolicitudDomain` que referencia esos ids candidatos. **No consulta la BD.**
- El **use case** (dueño de todo el I/O) resuelve la realidad: con los `Finder`s decide, por cada parte, si **reutiliza** la fila existente (`UNIQUE(usuario_id)`) o **persiste** el candidato; obtiene los ids reales; y **reconstruye** el `SolicitudDomain` con esos ids reales antes de validar unicidad y persistir. El `SolicitudDomain` candidato del *bundle* aportó la validación de formato (RN-1) en el mapeo; el `SolicitudDomain` final aporta las FKs correctas.

### 4.3 Réplicas locales (vistas materializadas de `usuarios`)
- **`UsuarioDomain`** (`domain/usuario/UsuarioDomain.java`) — feature `usuario`. Campos: `id: UUID`, `identificador: String` (**obligatorio, NOT NULL**), `nombre: String` (**obligatorio, NOT NULL**), `email: String` (obligatorio). `crear(...)` valida obligatoriedad de los 4 campos (`ValidatorTexto.noEnBlanco` / `ValidatorObjeto.noNulo`), `reconstruir(...)`. Es la réplica que puebla el `UsuarioCreadoConsumer` (o el seed).
- **`RemitenteDomain`** (`domain/remitente/RemitenteDomain.java`) — feature `remitente`. Campos: `id: UUID` (autogen en `crear`), `usuario: UUID`. `crear(usuario)` + `reconstruir(id, usuario)`.
- **`DestinatarioDomain`** (`domain/destinatario/DestinatarioDomain.java`) — feature `destinatario`. Simétrico.

Atributos por objeto de dominio (solo lo documentado en el MER):

**Solicitud**
| Atributo | Tipo | Longitud | Obligatorio | Modificable | Autogenerado | Notas |
|---|---|---|---|---|---|---|
| id | UUID | — | Sí | No | Sí | PK |
| destinatario | UUID (FK→`destinatario.id`) | — | Sí | No | No | Se resuelve en el use case |
| remitente | UUID (FK→`remitente.id`) | — | Sí | No | No | Se resuelve del `sub` del JWT |
| fechaCreacion | TIMESTAMP | — | Sí | No | Sí (calculado) | `LocalDateTime` del servidor |
| mensajeSolicitud | VARCHAR | 1..100 | Sí | No | No | Trim inicio+fin |
| tipoSolicitud | VARCHAR(60) (FK→`tipo_solicitud.id`) | — | Sí | No | No | Fijo = `NOVEDAD_PARA_EL_COORDINADOR` |

**Combinación única:** `(destinatario, remitente, fechaCreacion, mensajeSolicitud)` → `uk_solicitud_unica` en Flyway + `SolicitudUnicaRule` (RN-5).

**Usuario (réplica)** — MER exacto: `id UUID PK`, `identificador VARCHAR(30) NOT NULL`, `nombre VARCHAR(50) NOT NULL`, `email VARCHAR(50) NOT NULL`.
**Remitente / Destinatario (réplica)** — `id UUID PK`, `usuario_id UUID NOT NULL` con `UNIQUE(usuario_id)`.

### 4.4 Dependencia externa / contrato pendiente en `usuarios` (NO es parte de HU081)

`usuarios` es dueño del agregado `Usuario` (ADR-001: el evento es el contrato entre contextos). Para que `solicitudes` (y `fichas`) mantengan su proyección local sin FK cruzada, `usuarios` debe evolucionar su contrato de eventos. **Esto requiere una HT en el backlog del lado `usuarios`, fuera del alcance de HU081.**

| Evento | Payload objetivo | Routing key | Estado hoy |
|---|---|---|---|
| `UsuarioCreadoEvent` (enriquecer) | `{ usuarioId, identificador, nombre, email, estado, rol }` | `usuarios.usuario.creado` (ya existe) | Publica solo `{ usuarioId, email, rol }` |
| `UsuarioActualizadoEvent` (nuevo) | `{ usuarioId, identificador, nombre, email, estado, rol }` | `usuarios.usuario.actualizado` | No existe |
| `UsuarioDesactivadoEvent` (nuevo) | `{ usuarioId, estado }` | `usuarios.usuario.desactivado` | No existe |

`solicitudes` **solo persiste** de ese payload los 4 campos que el MER modela (`id`, `identificador`, `nombre`, `email`); `estado` y `rol` se ignoran en este contexto por ahora. Mientras el contrato no llegue: el consumer de `solicitudes` queda inerte (sección 6.10) y la proyección se siembra a mano (sección 11.2). También beneficia a `fichas`, cuyas réplicas `estudiante`/`asesor_ficha` hoy no se pueblan.

### 4.5 Enum de catálogo `TipoSolicitud` (`domain/tiposolicitud/TipoSolicitud.java`)

Constantes copiadas **fila por fila** de `mer/data/11_data_solicitudes.sql`:

| id (`name()`) | nombre (`getNombre()`) | descripcion (solo doc MER) |
|---|---|---|
| `NOVEDAD_PARA_EL_COORDINADOR` | Novedad para el Coordinador | Solicitud para temas que surgen de improvisto y que no están tipados |
| `NOVEDAD_PARA_EL_ASESOR` | Novedad para el Asesor | Solicitud para temas que surgen de improvisto y que no están tipados |
| `CAMBIO_DE_ASESOR` | Cambio de Asesor | Solicitud para modificar el asesor |
| `AMPLIACION_DE_PLAZO` | Ampliación de Plazo | Solicitud para extender la fecha de entrega del proyecto |
| `REGISTRO_Y_MODIFICACION_DE_USUARIOS` | Registro y modificación de Usuarios | Solicitud para el registro o modificación de usuarios |
| `VACIO` | `""` | Centinela del Notification Pattern — nunca se persiste |

Métodos: `getId()` → `name()`; `getNombre()`; `desde(String)` → `TipoSolicitudNoEncontradoException` (`domain/tiposolicitud/exception/`, → 422); `esValido(String)`; ambos delegan en `UtilEnum.desde(...)` filtrando `VACIO`. Patrón idéntico a `EstadoFicha`.

> Para HU081 el `tipo` es fijo y su fila de catálogo la siembra la migración base → **no se necesita `Finder` ni consulta de `tipo_solicitud`**; la FK se satisface con `TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId()`.

### 4.6 Evento de Dominio (camino estándar, como `fichas`)

| Evento | Clase | `EVENT_TOPIC` / routing key | Cómo se emite | Consumidor |
|---|---|---|---|---|
| Solicitud de novedad para el coordinador enviada | `SolicitudNovedadCoordinadorEnviadaEvent` (`domain/solicitud/event/`, extiende `DomainEvent`) | `solicitudes.solicitud.novedad_coordinador_enviada` | El `UseCase` inyecta el puerto `EventPublisher` (`com.arquisoft.shared.publisher`, `shared:application`) y llama `eventPublisher.publish(evento)` tras persistir, dentro de la tx del interactor. `SpringModulithEventPublisher` (`@Primary`) inserta en `event_publication` en esa misma transacción; `ModulithAmqpExternalizationConfig` (`shared:amqp`) lo externaliza al exchange `arquisoft.events` con routing key `getTemaEvento()`. | **Ninguno hoy** (solo lado publicación). Sin `Queue`/`Binding` — el broker devolvería el mensaje (publisher-return, warning) hasta que exista un consumidor. |

**Payload:** `solicitudId`, `remitenteUsuarioId`, `destinatarioUsuarioId`, `mensajeSolicitud`, `fechaCreacion`, `tipoSolicitud` (`getId()`).

**Igual que `CambiarAsesorFichaUseCaseImpl` → `AsesorFichaCambiadoEvent`.** No se toca nada de `shared:amqp`.

---

## 5. Integraciones Externas

Ninguna más allá de PostgreSQL (base `solicitudes`) y RabbitMQ **solo del lado consumo** (`usuarios.usuario.creado`, sección 6.10, hoy inerte). Sin Keycloak Admin API, sin SMTP, sin MinIO, sin HTTP externo, sin publicación AMQP.

---

## 6. Árbol de Archivos a Crear / Modificar

`{feature}` en minúsculas sin separadores. Rutas desde la raíz del repo.

### 6.1 Bootstrapping del contexto (infra de proyecto)

| Ámbito | Ruta | Tipo | Responsabilidad |
|---|---|---|---|
| Gradle | `settings.gradle` | MODIFICAR | Añadir `include 'solicitudes'`, `'solicitudes:domain'`, `'solicitudes:application'`, `'solicitudes:infrastructure'` (bloque "Contexto 10" al final de la sección de contextos) |
| Gradle | `solicitudes/build.gradle` | NUEVO | Solo comentario `// Configuración automática desde build.gradle raíz` |
| Gradle | `solicitudes/domain/build.gradle` | NUEVO | Copia de `fichas/domain/build.gradle` (`implementation project(':shared:domain')` + test AssertJ/Mockito/JUnit launcher) |
| Gradle | `solicitudes/application/build.gradle` | NUEVO | Copia de `fichas/application/build.gradle` (`:solicitudes:domain`, `:shared:application`, `:shared:query`, `:shared:logger`, `spring-tx`, junit launcher) |
| Gradle | `solicitudes/infrastructure/build.gradle` | NUEVO | Copia de `fichas/infrastructure/build.gradle` (domain+application+shared:domain/application/query/amqp/logger/tracing/web/minio/jpa, security, oauth2-resource-server, validation, amqp, data-jpa, flyway, postgresql, springdoc, test stack) |
| BD | `init-db.sql` | MODIFICAR | Añadir `CREATE DATABASE solicitudes OWNER arquisoft_user;` y el bloque `\c solicitudes` + `GRANT ALL ON SCHEMA public TO arquisoft_user;` + `ALTER SCHEMA public OWNER TO arquisoft_user;` |
| Infra | `docker-compose.yml` | MODIFICAR | Servicio `backend` → `environment`: `DB_SOLICITUDES_URL: jdbc:postgresql://postgres:5432/solicitudes`, `DB_SOLICITUDES_USERNAME: arquisoft_user`, `DB_SOLICITUDES_PASSWORD: arquisoft123` |
| Infra | `.env.example` | MODIFICAR | `DB_SOLICITUDES_URL` (bloque Linux comentado + bloque Windows activo), `DB_SOLICITUDES_USERNAME`, `DB_SOLICITUDES_PASSWORD` |
| Config | `src/main/resources/application.yml` | MODIFICAR | Bloque `datasource.solicitudes` (copia del de `fichas`: `url: ${DB_SOLICITUDES_URL:jdbc:postgresql://localhost:5432/solicitudes}`, username/password, hikari, pool-name `HikariPool-Solicitudes`) |
| Config | `src/main/resources/application-prod.yml` | MODIFICAR | Bloque `datasource.solicitudes` (prod: `${DB_SOLICITUDES_URL}` sin default, hikari 20/5/30000, pool-name `HikariPool-Solicitudes-Prod`) |
| Config | `solicitudes/infrastructure/src/main/java/com/arquisoft/solicitudes/infrastructure/config/SolicitudesDataSourceConfig.java` | NUEVO | **Copia completa** de `FichasDataSourceConfig`: `@EnableJpaRepositories(basePackages="com.arquisoft.solicitudes.infrastructure", entityManagerFactoryRef="solicitudesEntityManagerFactory", transactionManagerRef="solicitudesTransactionManager")`; beans `solicitudesDataSource`, `solicitudesEntityManagerFactory` (`setPackagesToScan("com.arquisoft.solicitudes.infrastructure")`, **un** paquete), `solicitudesTransactionManager`, `solicitudesFlyway` (`.locations("classpath:db/migration/solicitudes")`, `.baselineOnMigrate(false)`) |
| Config raíz | `src/main/java/com/arquisoft/ArquisoftApplication.java` | MODIFICAR (fix post-pruebas) | `@SpringBootApplication(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)`. El scan raíz nombra los `@Component` por nombre simple y `solicitudes` introduce 3 clases con nombre simple ya usado por otros contextos (`UsuarioCreadoConsumer` ↔ `fichas`, `RegistrarUsuarioUseCaseImpl` ↔ `fichas`, `UsuarioCommandOutputAdapter` ↔ `usuarios`) → `ConflictingBeanDefinitionException` al arrancar. FQN da nombre único a cada bean escaneado; **no** toca los beans de método `@Bean` (DataSources/EMFs/Flyways/exchanges/ObjectMappers/`handlerExceptionResolver`), que son los únicos referenciados por `@Qualifier` en el repo (`grep getBean(` en producción → 0). Arregla la colisión para todos los contextos presentes y futuros (HU-082/083/085 repetirían el patrón de réplica `usuario`). |

### 6.2 Migraciones Flyway — `solicitudes/infrastructure/src/main/resources/db/migration/solicitudes/`

> Versión = timestamp `V2026MMDDHHMMSS` **generado al crear el archivo**. Nombres abajo = placeholders con el orden correcto.

**2 migraciones** (contiguo NO requerido por Flyway — los timestamps quedan como están, no se renumeran):

| Ruta | Tipo | Responsabilidad |
|---|---|---|
| `V20260827144741__crear_esquema_base_solicitudes.sql` | NUEVO | Todas las tablas del MER (`mer/11_tablas_solicitudes.sql`) **verbatim**: `estado_respuesta`, `tipo_solicitud`, `usuario` (con `identificador`/`nombre` **NOT NULL**, como el MER), `remitente`, `destinatario`, `solicitud`, `respuesta` + 4 índices. Sin prefijo de base/schema. **+ INSERTs de catálogo** de `mer/data/11_data_solicitudes.sql`: `tipo_solicitud` (5 filas) y `estado_respuesta` (3 filas) |
| `V20260827175416__crear_event_publication.sql` | NUEVO | Tabla `event_publication` + 2 índices, **verbatim** de `fichas/.../V20260724005915__crear_event_publication.sql` (cabecera nombra `solicitudesTransactionManager`). La autodetecta `ContextAwareEventPublicationRepository` (raíz). Timestamp posterior al esquema base. |

**Datos demo:** sin archivo en el repo. Bloque `INSERT` de referencia documentado en **§11.2**, para correr a mano con `psql` en un entorno local si se quieren usuarios de prueba.

### 6.3 Feature `solicitud` — dominio

| Ruta | Tipo |
|---|---|
| `solicitudes/domain/src/main/java/com/arquisoft/solicitudes/domain/solicitud/SolicitudDomain.java` | NUEVO — aggregate root |
| `.../domain/solicitud/EnvioSolicitudNovedadCoordinadorDomain.java` | NUEVO — **objeto de acción** (bundle solicitud + remitente + destinatario), al lado del agregado sin subpaquete (sección 4.2) |
| `.../domain/solicitud/model/ExistenciaRemitente.java` | NUEVO — `record (UUID usuario, boolean existe)` |
| `.../domain/solicitud/model/ExistenciaDestinatario.java` | NUEVO — `record (UUID usuario, boolean existe)` |
| `.../domain/solicitud/model/ClaveSolicitud.java` | NUEVO — `record (UUID destinatario, UUID remitente, LocalDateTime fechaCreacion, String mensajeSolicitud)` — entrada del `SolicitudDuplicadaFinder` |
| `.../domain/solicitud/model/DisponibilidadSolicitud.java` | NUEVO — `record (ClaveSolicitud clave, boolean yaExiste)` — entrada de `SolicitudUnicaRule` |
| `.../domain/solicitud/rules/RemitenteExisteRule.java` + `rules/impl/RemitenteExisteRuleImpl.java` | NUEVO |
| `.../domain/solicitud/rules/DestinatarioExisteRule.java` + `rules/impl/DestinatarioExisteRuleImpl.java` | NUEVO |
| `.../domain/solicitud/rules/SolicitudUnicaRule.java` + `rules/impl/SolicitudUnicaRuleImpl.java` | NUEVO |
| `.../domain/solicitud/exception/RemitenteNoEncontradoException.java` | NUEVO — extiende `DomainException` (422) |
| `.../domain/solicitud/exception/DestinatarioNoEncontradoException.java` | NUEVO — extiende `DomainException` (422) |
| `.../domain/solicitud/exception/SolicitudDuplicadaException.java` | NUEVO — extiende `DomainException` (422) |
| `.../domain/solicitud/event/SolicitudNovedadCoordinadorEnviadaEvent.java` | NUEVO — extiende `DomainEvent`, `EVENT_TOPIC = "solicitudes.solicitud.novedad_coordinador_enviada"` |

### 6.4 Feature `tiposolicitud` — dominio (catálogo)

| Ruta | Tipo |
|---|---|
| `.../domain/tiposolicitud/TipoSolicitud.java` | NUEVO — enum de catálogo (sección 4.5) |
| `.../domain/tiposolicitud/exception/TipoSolicitudNoEncontradoException.java` | NUEVO — extiende `DomainException` (422) |

### 6.5 Features `usuario`, `remitente`, `destinatario` — dominio (réplicas)

| Ruta | Tipo |
|---|---|
| `.../domain/usuario/UsuarioDomain.java` | NUEVO — réplica, `crear` (4 campos obligatorios) / `reconstruir` |
| `.../domain/remitente/RemitenteDomain.java` | NUEVO |
| `.../domain/destinatario/DestinatarioDomain.java` | NUEVO |

### 6.6 Feature `solicitud` — application (command)

| Ruta | Tipo |
|---|---|
| `.../application/solicitud/command/primaryport/model/EnviarSolicitudNovedadCoordinadorCommand.java` | NUEVO — `record (UUID remitenteUsuario, UUID destinatarioUsuario, String mensajeSolicitud)` + `crear(String remitenteUsuario, String destinatarioUsuario, String mensajeSolicitud)` (valida formato con `ValidatorUUID`/`ValidatorTexto`/`ValidatorLongitud`, convierte con `UtilUUID.generarUUIDDesdeTexto`). **Sin cambios respecto al plan anterior.** |
| `.../application/solicitud/command/primaryport/mapper/EnviarSolicitudNovedadCoordinadorMapper.java` | NUEVO — `final`, constructor privado, `static toDomain(EnviarSolicitudNovedadCoordinadorCommand command)` → `EnvioSolicitudNovedadCoordinadorDomain`. Construye los candidatos `RemitenteDomain.crear(command.remitenteUsuario())` / `DestinatarioDomain.crear(command.destinatarioUsuario())` y el `SolicitudDomain.crear(destinatario.getId(), remitente.getId(), command.mensajeSolicitud(), TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR)`, luego `EnvioSolicitudNovedadCoordinadorDomain.crear(solicitud, remitente, destinatario)`. **Sin I/O.** Análogo a `RegistrarFichaPerfilMapper.toDomain` |
| `.../application/solicitud/command/primaryport/interactor/EnviarSolicitudNovedadCoordinadorInteractor.java` + `interactor/impl/…InteractorImpl.java` | NUEVO — `@Transactional(transactionManager = "solicitudesTransactionManager")`, retorna `UUID`. Llama `EnviarSolicitudNovedadCoordinadorMapper.toDomain(command)` y pasa el `EnvioSolicitudNovedadCoordinadorDomain` al use case (igual que `RegistrarFichaPerfilInteractorImpl` con `RegistrarFichaPerfilMapper`) |
| `.../application/solicitud/command/usecase/EnviarSolicitudNovedadCoordinadorUseCase.java` + `usecase/impl/…UseCaseImpl.java` | NUEVO — orquestación (sección 7) |
| `.../application/solicitud/command/validator/EnviarSolicitudNovedadCoordinadorValidator.java` + `validator/impl/…ValidatorImpl.java` | NUEVO — constructor sin argumentos, `new …RuleImpl()` ×3; `validar(SolicitudDomain, boolean remitenteExiste, boolean destinatarioExiste, boolean solicitudYaExiste)` — sin `if` |
| `.../application/solicitud/command/finder/UsuarioExisteFinder.java` + `finder/impl/…FinderImpl.java` | NUEVO — `Finder<UUID, Boolean>` → `UsuarioOutputPort.existePorId`. Un finder, 2 consumidores |
| `.../application/solicitud/command/finder/SolicitudDuplicadaFinder.java` + `finder/impl/…FinderImpl.java` | NUEVO — `Finder<ClaveSolicitud, Boolean>` → `SolicitudOutputPort.existePorCombinacionUnica(clave.destinatario(), clave.remitente(), clave.fechaCreacion(), clave.mensajeSolicitud())` |
| `.../application/solicitud/command/secondaryport/SolicitudOutputPort.java` | NUEVO — `void registrar(SolicitudEntity)`, `boolean existePorCombinacionUnica(UUID destinatario, UUID remitente, LocalDateTime fechaCreacion, String mensajeSolicitud)` |
| `.../application/solicitud/command/secondaryport/entity/SolicitudEntity.java` | NUEVO — `record (UUID id, UUID destinatario, UUID remitente, LocalDateTime fechaCreacion, String mensajeSolicitud, String tipoSolicitud)` (plano; `tipoSolicitud` = `getId()`) |
| `.../application/solicitud/command/secondaryport/mapper/SolicitudMapper.java` | NUEVO — `final`, `static toEntity(SolicitudDomain)` / `toDomain(SolicitudEntity)` (usa `TipoSolicitud.desde(...)`) |
| _(sin puerto local de eventos)_ | El `UseCase` inyecta directamente el puerto `EventPublisher` de `shared:application` — como `fichas`. Ver §4.6/§10. |

### 6.7 Features `usuario`, `remitente`, `destinatario` — application

| Ruta | Tipo |
|---|---|
| `.../application/usuario/command/primaryport/model/RegistrarUsuarioCommand.java` | NUEVO — `record (UUID usuarioId, String identificador, String nombre, String email)` (llega de un evento; sin `crear(...)` de formato, igual que el stub de `fichas`) |
| `.../application/usuario/command/primaryport/interactor/RegistrarUsuarioInteractor.java` + `impl/` | NUEVO — `@Transactional(transactionManager = "solicitudesTransactionManager")`, `void`. **SÍ persiste** (a diferencia del stub de `fichas`), por eso va por Interactor |
| `.../application/usuario/command/usecase/RegistrarUsuarioUseCase.java` + `impl/` | NUEVO — *upsert* de la réplica: `if (usuarioOutputPort.existePorId(cmd.usuarioId())) actualizar(...) else registrar(...)` |
| `.../application/usuario/command/secondaryport/UsuarioOutputPort.java` | NUEVO — `boolean existePorId(UUID)`, `void registrar(UsuarioEntity)`, `void actualizar(UsuarioEntity)` |
| `.../application/usuario/command/secondaryport/entity/UsuarioEntity.java` + `mapper/UsuarioMapper.java` | NUEVO — `record (UUID id, String identificador, String nombre, String email)` |
| `.../application/remitente/command/secondaryport/RemitenteOutputPort.java` | NUEVO — `Optional<UUID> buscarIdPorUsuario(UUID usuarioId)`, `void registrar(RemitenteEntity)` |
| `.../application/remitente/command/secondaryport/entity/RemitenteEntity.java` + `mapper/RemitenteMapper.java` | NUEVO — `record (UUID id, UUID usuario)` |
| `.../application/remitente/command/finder/RemitenteDeUsuarioFinder.java` + `impl/` | NUEVO — `Finder<UUID, Optional<UUID>>` |
| `.../application/destinatario/command/secondaryport/…` + `finder/DestinatarioDeUsuarioFinder.java` + `impl/` | NUEVO — simétrico a `remitente` |

> **P8:** el get-or-create de `remitente`/`destinatario` lo hace el `EnviarSolicitudNovedadCoordinadorUseCaseImpl` (consulta el `Finder`; si `Optional.empty()` construye `RemitenteDomain.crear(usuarioId)` y llama a `RemitenteOutputPort.registrar`). Es una escritura → vive en `command/`, **no** hay paquete `query/`. Idempotente por `UNIQUE(usuario_id)`.

### 6.8 Feature `solicitud` — infrastructure

| Ruta | Tipo |
|---|---|
| `.../infrastructure/solicitud/command/primaryadapter/web/EnviarSolicitudNovedadCoordinadorController.java` | NUEVO — `@RestController`, `@RequestMapping("${rutas.solicitudes.solicitud.base:/solicitudes}")` + `@PostMapping("${rutas.solicitudes.solicitud.novedad-coordinador:/novedad-coordinador}")` (patrón base+sub como `RegistrarEvaluacionFichaPerfilController`), `@PreAuthorize(SolicitudesAuthorities.Expresiones.HAS_SOLICITUD_CREATE)`, remitente de `@AuthenticationPrincipal Jwt jwt` (`jwt.getSubject()`), `@Tag`/`@Operation`/`@ApiResponses`/`@SecurityRequirement`. Retorna `201` + `EnviarSolicitudNovedadCoordinadorResponseDTO(UUID id)` |
| `solicitudes/infrastructure/src/main/resources/rutas-solicitudes.yml` | NUEVO — un placeholder por segmento (`rutas.solicitudes.solicitud.base: /solicitudes`, `.novedad-coordinador: /novedad-coordinador`), mismo encabezado que `rutas-fichas.yml`. Vive en el módulo del contexto. |
| `src/main/resources/application.yml` | MODIFICAR — añadir `- "classpath:rutas-solicitudes.yml"` a `spring.config.import` tras `rutas-fichas.yml` |
| `src/main/java/com/arquisoft/config/OpenApiConfig.java` | MODIFICAR — `@Value("${rutas.solicitudes.solicitud.base:/solicitudes}") solicitudesBasePath` + `@Bean GroupedOpenApi solicitudesApi()` (`.group("09-solicitudes")`, `.displayName("Solicitudes")` inline, `.pathsToMatch(solicitudesBasePath + "/**")`), tras `evaluacionesApi()` |
| `.../infrastructure/solicitud/command/primaryadapter/web/dto/EnviarSolicitudNovedadCoordinadorRequestDTO.java` | NUEVO — `record (String destinatario, String mensajeSolicitud)` desnudo, sin anotaciones |
| `.../infrastructure/solicitud/command/primaryadapter/web/dto/EnviarSolicitudNovedadCoordinadorResponseDTO.java` | NUEVO — `record (UUID id)` |
| `.../infrastructure/solicitud/command/primaryadapter/web/mapper/EnviarSolicitudNovedadCoordinadorRequestMapper.java` | NUEVO — `static toCommand(dto, String remitenteUsuarioId)` → `EnviarSolicitudNovedadCoordinadorCommand.crear(remitenteUsuarioId, dto.destinatario(), dto.mensajeSolicitud())` |
| `.../infrastructure/solicitud/command/secondaryadapter/entity/SolicitudJpaEntity.java` | NUEVO — `@Entity @Table(name="solicitud")`, Lombok `@Getter/@Builder/@NoArgsConstructor/@AllArgsConstructor`. `@ManyToOne(LAZY)` a `DestinatarioJpaEntity` / `RemitenteJpaEntity` (`@JoinColumn destinatario_id` / `remitente_id`), `@ManyToOne` a `TipoSolicitudJpaEntity` (`@JoinColumn tipo_solicitud_id`), `fecha_creacion`, `mensaje_solicitud` |
| `.../infrastructure/solicitud/command/secondaryadapter/mapper/SolicitudJpaMapper.java` | NUEVO — `Entity ↔ JpaEntity`, referencias id-only vía `RemitenteJpaMapper.toReferencia` / `DestinatarioJpaMapper.toReferencia` / `TipoSolicitudJpaMapper.toReferencia` |
| `.../infrastructure/solicitud/command/secondaryadapter/repository/SolicitudCommandOutputAdapter.java` | NUEVO — implementa `SolicitudOutputPort`, sin `try/catch`; `existePorCombinacionUnica` → `repository.existsBy…` |
| `.../infrastructure/solicitud/command/secondaryadapter/repository/SolicitudCommandRepository.java` | NUEVO — extiende `JpaRepository<SolicitudJpaEntity, UUID>` |
| _(sin adaptador de eventos propio)_ | La implementación del puerto `EventPublisher` es `SpringModulithEventPublisher` de `shared:amqp` (`@Primary`), compartida. Nada que crear en `solicitudes`. |

### 6.9 Feature `tiposolicitud` — infrastructure (para la FK del `@ManyToOne`)

| Ruta | Tipo |
|---|---|
| `.../infrastructure/tiposolicitud/command/secondaryadapter/entity/TipoSolicitudJpaEntity.java` | NUEVO — `@Entity @Table(name="tipo_solicitud")`, `@Id String id`, `nombre`, `descripcion` |
| `.../infrastructure/tiposolicitud/command/secondaryadapter/mapper/TipoSolicitudJpaMapper.java` | NUEVO — `static toReferencia(String id)` (instancia detached id-only para el `@ManyToOne` sin cascade) |

> Sin `OutputPort`/`OutputAdapter`/`Repository` para `tipo_solicitud`: nadie lo consulta en HU081. Solo `JpaEntity` + `JpaMapper.toReferencia` para que Hibernate escriba la FK. Evitar un repo vacío (sería *dead code* como `EstadoEvaluacionCommandRepository` en `fichas`).

### 6.10 Features `usuario`, `remitente`, `destinatario` — infrastructure

| Ruta | Tipo |
|---|---|
| `.../infrastructure/usuario/command/primaryadapter/amqp/UsuarioCreadoConsumer.java` | NUEVO — copia de `fichas`: `@RabbitListener(queues = SolicitudesUsuariosQueueConfig.USUARIO_CREADO_QUEUE)`, `withCorrelation`, deserializa `UsuarioCreadoPayload`. **Guarda de inercia (P1):** si `payload.identificador()` o `payload.nombre()` son `null`/blancos → `logger.info(...)` y `return` (no-op idempotente), pendiente del contrato enriquecido de `usuarios` (sección 4.4). Si están presentes → `registrarUsuarioInteractor.ejecutar(new RegistrarUsuarioCommand(...))` |
| `.../infrastructure/usuario/command/primaryadapter/amqp/UsuarioCreadoPayload.java` | NUEVO — `record (String idEvento, String usuarioId, String identificador, String nombre, String email, String estado, String rol)` — **forma enriquecida completa**; lectura tolerante (el `ObjectMapper` de RabbitMQ ignora props desconocidas y deja `null` las ausentes) |
| `.../infrastructure/usuario/command/secondaryadapter/entity/UsuarioJpaEntity.java` + `mapper/UsuarioJpaMapper.java` (`toReferencia` no aplica) + `repository/UsuarioCommandOutputAdapter.java` + `repository/UsuarioCommandRepository.java` | NUEVO — `@Table(name="usuario")`, persistencia real (upsert), sin `try/catch` |
| `.../infrastructure/remitente/command/secondaryadapter/entity/RemitenteJpaEntity.java` + `mapper/RemitenteJpaMapper.java` (`toReferencia`) + `repository/RemitenteCommandOutputAdapter.java` + `repository/RemitenteCommandRepository.java` | NUEVO — `@Table(name="remitente")`, `findByUsuarioId`, `save` |
| `.../infrastructure/destinatario/command/secondaryadapter/…` (simétrico) | NUEVO — `@Table(name="destinatario")` |
| `.../infrastructure/config/SolicitudesUsuariosQueueConfig.java` | NUEVO — copia de `FichasUsuariosQueueConfig`: `USUARIO_CREADO_QUEUE = "solicitudes.usuarios.usuario.creado"`, `USUARIO_CREADO_ROUTING_KEY = "usuarios.usuario.creado"`, `Queue` durable con DLX + `Binding` al exchange `arquisoftEventsExchange` |

### 6.11 Transversales del contexto — infrastructure

| Ruta | Tipo |
|---|---|
| `.../infrastructure/security/SolicitudesAuthorities.java` | NUEVO — patrón `FichasAuthorities`: `SOLICITUD_CREATE = "solicitudes:solicitud:create"` + `Expresiones.HAS_SOLICITUD_CREATE = "hasAuthority('...')"` |
| `solicitudes/infrastructure/src/test/java/com/arquisoft/solicitudes/infrastructure/SolicitudesInfrastructureTestApplication.java` | NUEVO — ancla `@SpringBootApplication` para los slices `@WebMvcTest`/`@DataJpaTest` |

### 6.12 `shared:message` + catálogo

| Ruta | Tipo | Contenido |
|---|---|---|
| `shared/message/src/main/java/com/arquisoft/shared/message/constant/SolicitudesCodes.java` | NUEVO | `Solicitud`: `MENSAJE_REQUERIDO`, `MENSAJE_DEMASIADO_LARGO`, `DESTINATARIO_REQUERIDO`, `DESTINATARIO_NO_ENCONTRADO`, `REMITENTE_REQUERIDO`, `REMITENTE_NO_ENCONTRADO`, `SOLICITUD_DUPLICADA`; `TipoSolicitud`: `TIPO_NO_ENCONTRADO` |
| `.../constant/SolicitudesFields.java` | NUEVO | `Solicitud`: `DESTINATARIO = "destinatario"`, `MENSAJE = "mensajeSolicitud"`, `REMITENTE = "remitente"` |
| `.../constant/SolicitudesLimits.java` | NUEVO | `Solicitud`: `MENSAJE_MIN = 1`, `MENSAJE_MAX = 100` |
| `.../annotation/SolicitudesApiMessages.java` | NUEVO | Textos Swagger `@Tag`/`@Operation`/`@ApiResponse` (embebidos, no van a Redis) |
| `.../key/solicitudes/SolicitudKey.java` | NUEVO | `ERROR_REMITENTE_NO_ENCONTRADO` (1), `ERROR_DESTINATARIO_NO_ENCONTRADO` (1), `ERROR_SOLICITUD_DUPLICADA` (0), `LOG_ENVIADA` (1), `LOG_GUARDADA` (1) |
| `.../key/solicitudes/TipoSolicitudKey.java` | NUEVO | `ERROR_TIPO_NO_ENCONTRADO` (1) |
| `.../key/solicitudes/UsuarioReplicaKey.java` | NUEVO | `LOG_USUARIO_CREADO_RECIBIDO` (4), `LOG_USUARIO_CREADO_IGNORADO_SIN_DATOS` (1), `LOG_REPLICA_GUARDADA` (1) |
| `shared/message/src/main/java/com/arquisoft/shared/message/ClavesCatalogo.java` | MODIFICAR | Añadir `SolicitudKey.class`, `TipoSolicitudKey.class`, `UsuarioReplicaKey.class` al `Set.of(...)` |
| `catalogo/solicitudes.properties` | NUEVO | Textos de las claves. Ej.: `solicitudes.dominio.solicitud.error.remitente-no-encontrado=No se encontró el remitente (usuario %s)`; `solicitudes.aplicacion.solicitud.log.enviada=Solicitud de novedad para el coordinador enviada — id={}`; `solicitudes.infraestructura.solicitud.log.guardada=Solicitud guardada: id={}`; `solicitudes.infraestructura.usuarioreplica.log.usuario-creado-ignorado-sin-datos=[SOLICITUDES] UsuarioCreado sin identificador/nombre (contrato viejo), se ignora: usuarioId={}` |
| `catalogo/cargar.sh` (o script de carga) | VERIFICAR | Confirmar que descubre `solicitudes.properties` automáticamente; si enumera archivos, añadirlo |

---

## 7. Detalle por Archivo (clave)

### `SolicitudDomain` (`domain/solicitud/`)
- Campos privados no-`final`: `id, destinatario, remitente, fechaCreacion, mensajeSolicitud, tipoSolicitud`. Sin Lombok.
- `public static final SolicitudDomain VACIO` con ceros (`UtilUUID.obtenerUUIDPorDefecto()`, `UtilFecha.VACIO`, `UtilTexto.VACIO`, `TipoSolicitud.VACIO`).
- `crear(UUID destinatario, UUID remitente, String mensajeSolicitud, TipoSolicitud tipoSolicitud)`: `new` privado + `ValidationResult`; `setId()`, `setFechaCreacion()` (hora del servidor), `setDestinatario/Remitente/TipoSolicitud` (`ValidatorObjeto.noNulo`, corte con `return`), `setMensajeSolicitud` (`ValidatorTexto.noEnBlanco` → `ValidatorLongitud.longitudEntre(SolicitudesLimits.Solicitud.MENSAJE_MIN, MENSAJE_MAX)` → `UtilTexto.aplicarTrim`); `result.lanzarSiTieneErrores()`.
- `reconstruir(UUID id, UUID destinatario, UUID remitente, LocalDateTime fechaCreacion, String mensajeSolicitud, TipoSolicitud tipoSolicitud)` — sin validación, sin eventos.
- Getters + `esVacio()`. Checkstyle: `reconstruir` con 6 parámetros → OK (≤7).

### `EnviarSolicitudNovedadCoordinadorCommand`
- `record (UUID remitenteUsuario, UUID destinatarioUsuario, String mensajeSolicitud)`; compact constructor aplica `UtilTexto.aplicarTrim` al mensaje.
- `static crear(String remitenteUsuario, String destinatarioUsuario, String mensajeSolicitud)`:
  - `destinatarioUsuario`: `ValidatorTexto.noEnBlanco(..., SolicitudesFields.Solicitud.DESTINATARIO, SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result)` → si ok `ValidatorUUID.uuidValido(...)`.
  - `remitenteUsuario` (del JWT): `ValidatorTexto.noEnBlanco` + `ValidatorUUID.uuidValido` con `REMITENTE_REQUERIDO` (robustez).
  - `mensajeSolicitud`: `ValidatorTexto.noEnBlanco` → `ValidatorLongitud.longitudEntre(1, MENSAJE_MAX, ...)`.
  - `result.lanzarSiTieneErroresDeEntrada()`; retorna con `UtilUUID.generarUUIDDesdeTexto`.

### `EnvioSolicitudNovedadCoordinadorDomain` (`domain/solicitud/`)
- Objeto de acción. Campos privados no-`final`: `solicitud: SolicitudDomain`, `remitente: RemitenteDomain`, `destinatario: DestinatarioDomain`. Sin Lombok.
- `crear(SolicitudDomain solicitud, RemitenteDomain remitente, DestinatarioDomain destinatario)`: `new` privado + `ValidationResult`; `setSolicitud/setRemitente/setDestinatario` validan `ValidatorObjeto.noNulo` (cortan con `return`); `result.lanzarSiTieneErrores()`. Idéntico en forma a `RegistroFichaPerfilDomain.crear`.
- Getters: `getSolicitud()`, `getRemitente()`, `getDestinatario()`, `getRemitenteUsuario()` (= `remitente.getUsuario()`), `getDestinatarioUsuario()` (= `destinatario.getUsuario()`).

### `EnviarSolicitudNovedadCoordinadorMapper` (`application/solicitud/command/primaryport/mapper/`)
- `final`, constructor privado, `static toDomain(EnviarSolicitudNovedadCoordinadorCommand command)`. **Sin I/O.**
```
var remitente    = RemitenteDomain.crear(command.remitenteUsuario());
var destinatario = DestinatarioDomain.crear(command.destinatarioUsuario());
var solicitud    = SolicitudDomain.crear(
        destinatario.getId(), remitente.getId(),
        command.mensajeSolicitud(), TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);
return EnvioSolicitudNovedadCoordinadorDomain.crear(solicitud, remitente, destinatario);
```
- El `SolicitudDomain.crear(...)` de aquí es donde se validan los invariantes locales del agregado (RN-1: mensaje 1–100, no nulos). Los ids de remitente/destinatario que referencia son los **candidatos** (ids recién generados de los `RemitenteDomain`/`DestinatarioDomain` candidatos).

### `EnviarSolicitudNovedadCoordinadorInteractorImpl`
- `@Component @RequiredArgsConstructor implements EnviarSolicitudNovedadCoordinadorInteractor`.
- `@Transactional(transactionManager = "solicitudesTransactionManager")` sobre `ejecutar(EnviarSolicitudNovedadCoordinadorCommand command) → UUID`:
  `return useCase.ejecutar(EnviarSolicitudNovedadCoordinadorMapper.toDomain(command));`
- **No** implementa el `UseCase`. El qualifier es obligatorio: hay varios `PlatformTransactionManager` y `usuariosTransactionManager` es `@Primary`. Fija `solicitudesTransactionManager` y con él engancha el insert en el outbox (`event_publication`) en la misma transacción que el `save` — atomicidad exigida por `ContextAwareEventPublicationRepository`.

### `EnviarSolicitudNovedadCoordinadorUseCaseImpl`
Inyecta: `SolicitudOutputPort`, `RemitenteOutputPort`, `DestinatarioOutputPort`, `RemitenteDeUsuarioFinder`, `DestinatarioDeUsuarioFinder`, `UsuarioExisteFinder`, `SolicitudDuplicadaFinder`, `EnviarSolicitudNovedadCoordinadorValidator`, `EventPublisher` (`com.arquisoft.shared.publisher`, la **interfaz**), `AppLogger`.

Flujo `ejecutar(EnvioSolicitudNovedadCoordinadorDomain envio) → UUID` — recibe el **objeto de acción** del interactor (igual que `RegistrarFichaPerfilUseCaseImpl` recibe `RegistroFichaPerfilDomain`):

1. `boolean remitenteUsuarioExiste = usuarioExisteFinder.obtener(envio.getRemitenteUsuario())`
2. `boolean destinatarioUsuarioExiste = usuarioExisteFinder.obtener(envio.getDestinatarioUsuario())`
3. `validator.validarExistenciaUsuarios(envio, remitenteUsuarioExiste, destinatarioUsuarioExiste)` — construye `ExistenciaRemitente`/`ExistenciaDestinatario` e invoca `RemitenteExisteRule` / `DestinatarioExisteRule`; lanza `RemitenteNoEncontradoException` / `DestinatarioNoEncontradoException` **antes** de tocar la BD de escritura. (Orden de validación paso 2: existencia de usuario primero.)
4. Get-or-create fila `remitente` (idempotente por `UNIQUE(usuario_id)`):
   `UUID remitenteId = remitenteDeUsuarioFinder.obtener(envio.getRemitenteUsuario()).orElseGet(() -> { remitenteOutputPort.registrar(RemitenteMapper.toEntity(envio.getRemitente())); return envio.getRemitente().getId(); })`
   — si la fila ya existía se **reutiliza** su id; si no, se **persiste el candidato** `envio.getRemitente()` y su id es el definitivo.
5. Get-or-create fila `destinatario` (simétrico, con `envio.getDestinatario()`).
6. **Reconstrucción del agregado con las FKs reales:**
   `SolicitudDomain solicitud = SolicitudDomain.crear(destinatarioId, remitenteId, envio.getSolicitud().getMensajeSolicitud(), envio.getSolicitud().getTipoSolicitud())`
   — el `SolicitudDomain` candidato del *bundle* (`envio.getSolicitud()`) ya hizo su trabajo de validación de formato en el mapeo; aquí se materializa el agregado que se persiste, con los ids de fila resueltos. Camino único, sin `if` (no se intenta "reusar" el candidato cuando coincida — siempre se reconstruye).
7. `ClaveSolicitud clave = new ClaveSolicitud(destinatarioId, remitenteId, solicitud.getFechaCreacion(), solicitud.getMensajeSolicitud())`
8. `boolean yaExiste = solicitudDuplicadaFinder.obtener(clave)`
9. `validator.validarUnicidad(new DisponibilidadSolicitud(clave, yaExiste))` — invoca `SolicitudUnicaRule`; lanza `SolicitudDuplicadaException`. (Orden de validación: unicidad después de existencia.)
10. `solicitudOutputPort.registrar(SolicitudMapper.toEntity(solicitud))`
11. `logger.info(Mensajes.obtener(SolicitudKey.LOG_ENVIADA), solicitud.getId())`
12. `eventPublisher.publish(new SolicitudNovedadCoordinadorEnviadaEvent(solicitud.getId(), envio.getRemitenteUsuario().toString(), envio.getDestinatarioUsuario().toString(), solicitud.getMensajeSolicitud(), solicitud.getFechaCreacion(), TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR.getId()))` — igual que `CambiarAsesorFichaUseCaseImpl`
13. `return solicitud.getId()`

> **Nota de coherencia con el objeto de acción:** el use case opera sobre `envio.getRemitente()` / `envio.getDestinatario()` (candidatos a persistir en el get-or-create) y sobre `envio.getSolicitud()` (portador del mensaje y el tipo ya validados). La única pieza que el mapper **no** puede fijar es la FK real de las filas de rol — de eso se encarga el use case, dueño del I/O, en los pasos 4–6.

### `EnviarSolicitudNovedadCoordinadorValidatorImpl`
- Constructor sin argumentos: `this.remitenteExisteRule = new RemitenteExisteRuleImpl(); this.destinatarioExisteRule = new DestinatarioExisteRuleImpl(); this.solicitudUnicaRule = new SolicitudUnicaRuleImpl();`
- `validarExistenciaUsuarios(EnvioSolicitudNovedadCoordinadorDomain envio, boolean remitenteExiste, boolean destinatarioExiste)`: construye `ExistenciaRemitente(envio.getRemitenteUsuario(), remitenteExiste)` / `ExistenciaDestinatario(...)` e invoca las dos Rules en orden.
- `validarUnicidad(DisponibilidadSolicitud disponibilidad)`: invoca `SolicitudUnicaRule`.
- Sin `if`. (Dos métodos porque el orden de validación exige consultar existencia de usuario **antes** del get-or-create, y la clave de unicidad solo se conoce **después**; ambos siguen siendo pura orquestación de Rules.)

### `UsuarioCreadoConsumer` (`infrastructure/usuario/.../amqp/`)
- Copia de `fichas` con: cola `SolicitudesUsuariosQueueConfig.USUARIO_CREADO_QUEUE`, `RegistrarUsuarioInteractor` (Interactor, no `UseCase` directo — aquí SÍ persiste), clave `UsuarioReplicaKey.LOG_USUARIO_CREADO_RECIBIDO`.
- **Guarda de inercia:** `if (UtilTexto.estaEnBlanco(payload.identificador()) || UtilTexto.estaEnBlanco(payload.nombre())) { logger.info(Mensajes.obtener(UsuarioReplicaKey.LOG_USUARIO_CREADO_IGNORADO_SIN_DATOS), payload.usuarioId()); return; }` — ACK del mensaje, sin persistir, sin ir a DLQ. Se retira cuando `usuarios` publique el contrato enriquecido (sección 4.4).
- `RegistrarUsuarioUseCaseImpl` hace upsert: control de flujo de infraestructura de réplica, no una regla de negocio, sin `Rule`.

### `EnviarSolicitudNovedadCoordinadorController`
- `@RequestMapping("${rutas.solicitudes.solicitud.base:/solicitudes}")` (clase) + `@PostMapping("${rutas.solicitudes.solicitud.novedad-coordinador:/novedad-coordinador}")` (método) — patrón base+sub de `RegistrarEvaluacionFichaPerfilController`. Los placeholders resuelven desde `rutas-solicitudes.yml`; los defaults del `@Value` cubren los slices de test.
- `@Tag(name = SolicitudesApiMessages.Solicitud.TAG_NAME, ...)`, `@Operation(summary = ..., security = @SecurityRequirement(name = ApiSecurity.BEARER_AUTH))`, `@ApiResponses` `201`/`400`·`422`(`ErrorResponseDTO`)/`401`/`403`.
- Grupo Swagger `09-solicitudes` (`OpenApiConfig.solicitudesApi()`, `.pathsToMatch(solicitudesBasePath + "/**")`).
- Firma: `public ResponseEntity<EnviarSolicitudNovedadCoordinadorResponseDTO> enviar(@RequestBody EnviarSolicitudNovedadCoordinadorRequestDTO request, @AuthenticationPrincipal Jwt jwt)`.
- `UUID id = interactor.ejecutar(EnviarSolicitudNovedadCoordinadorRequestMapper.toCommand(request, jwt.getSubject()));`
- `return ResponseEntity.status(HttpStatus.CREATED).body(new EnviarSolicitudNovedadCoordinadorResponseDTO(id));`

### `SolicitudNovedadCoordinadorEnviadaEvent`
- Extiende `DomainEvent`. `EVENT_TOPIC = "solicitudes.solicitud.novedad_coordinador_enviada"`, `EVENT_TYPE = "SolicitudNovedadCoordinadorEnviadaEvent"`. Constructor `(UUID solicitudId, String remitenteUsuarioId, String destinatarioUsuarioId, String mensajeSolicitud, LocalDateTime fechaCreacion, String tipoSolicitud)` → `super(EVENT_TOPIC, EVENT_TYPE)`. Solo getters.
- Extiende `DomainEvent`: `getTemaEvento()` es a la vez el identificador lógico y la routing key AMQP con que `ModulithAmqpExternalizationConfig` lo externaliza.

### Publicación del evento
- El `UseCase` inyecta la **interfaz** `EventPublisher` (`com.arquisoft.shared.publisher`) y llama `eventPublisher.publish(evento)` — sin puerto ni adaptador propios de `solicitudes`. La implementación es `SpringModulithEventPublisher` (`@Primary`, `shared:amqp`): outbox + externalización a `arquisoft.events`. Migración `event_publication` en `solicitudes` (§11.3). Idéntico a `fichas`.

---

## 8. Endpoints REST

| Método | Ruta (sin `/api`) | Request | Response | HTTP | Client role | Swagger |
|---|---|---|---|---|---|---|
| POST | `/solicitudes/novedad-coordinador` — `@RequestMapping("${rutas.solicitudes.solicitud.base:/solicitudes}")` + `@PostMapping("${rutas.solicitudes.solicitud.novedad-coordinador:/novedad-coordinador}")`; los placeholders resuelven desde `rutas-solicitudes.yml` (importado en `application.yml`) | `{ "destinatario": "<uuid usuario coordinador>", "mensajeSolicitud": "<1..100>" }` | `{ "id": "<uuid>" }` | 201 / 400·422 / 401 / 403 | `solicitudes:solicitud:create` | `@Tag` "Solicitudes", `@Operation` "Enviar solicitud de novedad para el coordinador". Grupo Swagger `09-solicitudes` (`OpenApiConfig.solicitudesApi()`) |

- **Endpoint NUEVO.** Un controller por acción.
- El `remitente` **no** viaja en el body: sale de `jwt.getSubject()`.
- Retorno = **UUID envuelto en ResponseDTO** (pregunta 11 = A). No hay `command/result/`.

---

## 9. Seguridad y Autorización (Keycloak)

| Client role | Roles realm que lo poseen | Endpoint(s) | Descripción |
|---|---|---|---|
| `solicitudes:solicitud:create` | `estudiante` | `POST /solicitudes/novedad-coordinador` | Permite a un estudiante enviar una solicitud de novedad para el coordinador |

- `@PreAuthorize(SolicitudesAuthorities.Expresiones.HAS_SOLICITUD_CREATE)` — constante, no literal.
- Alta del client role en el realm `arquisoft` de Keycloak y asignación al rol de realm `estudiante` (config del realm / HT-011). **Acción de configuración, fuera del código.**
- No hay caso 403 por "no eres el dueño": el remitente se toma del JWT, nunca del body. Q12 = N/A.

---

## 10. Eventos de Dominio — camino estándar (como `fichas`)

### 10.1 Publicación

| Dirección | Mecanismo | Routing key | Payload | Consumidor |
|---|---|---|---|---|
| Emite | `eventPublisher.publish(...)` (puerto `EventPublisher` de `shared:application`) → `SpringModulithEventPublisher` (`@Primary`) → outbox `event_publication` en la tx del interactor → `ModulithAmqpExternalizationConfig` externaliza al exchange `arquisoft.events` | `solicitudes.solicitud.novedad_coordinador_enviada` (= `getTemaEvento()`) | `SolicitudNovedadCoordinadorEnviadaEvent` (solicitudId, remitenteUsuarioId, destinatarioUsuarioId, mensajeSolicitud, fechaCreacion, tipoSolicitud) | **Ninguno hoy** — solo lado publicación |

**Ya NO es una desviación.** Ajuste post-validación autorizado por el usuario (2026-08-27): HU081 usa el patrón único de `arquisoft-arquitectura`, idéntico a `CambiarAsesorFichaUseCaseImpl` → `AsesorFichaCambiadoEvent`.
- **Alcance: SOLO lado publicación.** Sin consumidor, sin `notificaciones`, **sin `Queue`/`Binding`** para este evento. El broker devolverá el mensaje (publisher-return → warning en log) hasta que exista un consumidor — comportamiento aceptado, igual que cualquier evento sin cola.
- **`shared:amqp` no se toca:** `ModulithAmqpExternalizationConfig` ya enruta cualquier `DomainEvent` a `arquisoft.events` con `getTemaEvento()`.
- **Migración `event_publication` en `solicitudes`** (§11.3), autodetectada por `ContextAwareEventPublicationRepository` (raíz).
- **`@Transactional` del interactor:** aporta ahora atomicidad de outbox (insert en `event_publication` en la misma tx que el `save`) además de la unidad de trabajo y la fijación de `solicitudesTransactionManager`.
- **Sin puerto ni adaptador locales de eventos** en `solicitudes` — el `UseCase` inyecta la interfaz `EventPublisher` directamente.

### 10.2 Consumo (RabbitMQ) — listo pero inerte

| Dirección | Exchange | Routing Key | Payload | Estado |
|---|---|---|---|---|
| Consume | `arquisoftEventsExchange` | `usuarios.usuario.creado` | `UsuarioCreadoPayload` (forma enriquecida: `idEvento, usuarioId, identificador, nombre, email, estado, rol`) | Cola `solicitudes.usuarios.usuario.creado` (durable, DLX). El consumer **ignora (no-op)** los mensajes que llegan sin `identificador`/`nombre` (contrato actual de `usuarios`). Se activa de verdad cuando `usuarios` enriquezca el evento (sección 4.4) |

**No hay sección de `notificaciones`:** el usuario decidió NO cablear correo al coordinador en HU081 (aunque el contexto `notificaciones` existe). Si se agrega más adelante, seguir el patrón `AsesorFichaCambiadoEvent` → `AsesorFichaCambiadoConsumer` (requiere primero el exchange de publicación de 10.1 y el contrato enriquecido de 4.4).

---

## 11. Migración de Base de Datos

- **Base de datos:** `solicitudes` (nueva — `init-db.sql`).
- **Ubicación:** `solicitudes/infrastructure/src/main/resources/db/migration/solicitudes/` (`solicitudesFlyway` → `classpath:db/migration/solicitudes`).
- **Versión:** timestamp `V2026MMDDHHMMSS` tomado al crear cada archivo. **2 migraciones** (`V20260827144741` esquema, `V20260827175416` `event_publication`) — Flyway NO exige numeración contigua, los timestamps se conservan tal cual.
- `baselineOnMigrate = false`. Sin prefijo de base/schema. Sin FK cruzada a otra base (réplicas `usuario`/`remitente`/`destinatario` viven **en** `solicitudes`).

### 11.1 `V…__crear_esquema_base_solicitudes.sql`
Contenido = `mer/11_tablas_solicitudes.sql` **verbatim** (`estado_respuesta`, `tipo_solicitud`, `usuario` con `identificador VARCHAR(30) NOT NULL` y `nombre VARCHAR(50) NOT NULL` **tal cual el MER**, `remitente`, `destinatario`, `solicitud`, `respuesta` + 4 índices).

Anchos de catálogo (ADR-012 v1.1, tablas **nuevas** → 60/60/300): `tipo_solicitud` y `estado_respuesta` ya vienen 60/60/300 en el MER — copiar tal cual, **sin `ALTER`**.

**+ datos de catálogo** (de `mer/data/11_data_solicitudes.sql`, en el mismo archivo):
- `tipo_solicitud`: 5 filas (`NOVEDAD_PARA_EL_COORDINADOR`, `NOVEDAD_PARA_EL_ASESOR`, `CAMBIO_DE_ASESOR`, `AMPLIACION_DE_PLAZO`, `REGISTRO_Y_MODIFICACION_DE_USUARIOS`).
- `estado_respuesta`: 3 filas (`APROBADA`, `NO_APROBADA`, `EN_REVISION`).

### 11.2 Datos demo para local — bloque SQL de referencia (NO hay archivo en el repo)

**No existe archivo de seed en el repositorio** (decisión del usuario 2026-08-28): sin convención de `db/seed/` en el repo (`fichas` no la tiene), un `.sql` de datos demo empaquetado en el jar es ruido, y una migración Flyway corre en todos los entornos (prod incluido) y es inmutable tras aplicarse.

Si en un entorno **local** se quieren usuarios de prueba, ejecutar a mano contra la base `solicitudes`:

```sql
-- Data demo OPCIONAL para local. NO forma parte del repo ni del despliegue.
-- Reemplazar los id por los 'sub' reales de los usuarios del realm 'arquisoft' (HT-011).
INSERT INTO usuario (id, identificador, nombre, email) VALUES
    ('<sub-estudiante>',   'EST-XXXX',  'Estudiante Demo',  'estudiante.demo@arquisoft.co'),
    ('<sub-coordinador>',  'COORD-XXX', 'Coordinador Demo', 'coordinador.demo@arquisoft.co')
ON CONFLICT DO NOTHING;
```

Las filas `remitente` / `destinatario` **no** se siembran: las materializa el get-or-create del use case (sección 7, pasos 4–5), idempotente por `UNIQUE(usuario_id)`. Solo hace falta que exista la fila en `usuario`.

### 11.3 `event_publication`
**SE CREA** — `V20260827175416__crear_event_publication.sql`, copia **verbatim** de `fichas/.../V20260724005915__crear_event_publication.sql` (tabla + 2 índices; cabecera nombra `solicitudesTransactionManager`). `ContextAwareEventPublicationRepository` (raíz) autodetecta la tabla y enruta hacia ella los eventos publicados en una tx de `solicitudesTransactionManager`. Ajuste post-validación autorizado por el usuario (ver §10.1).

---

## 12. Casos de Prueba Sugeridos

**Tamaño estimado:** Mediana (1 endpoint + scaffold + 1 consumer). Objetivo **30–45 tests**. Cobertura ≥ 75 % con `check` (`*Domain` cuenta).

### Domain (`solicitudes:domain`) — Rules/Validator sin Mockito
- `SolicitudDomainTest`: `crear` válido; `crear` acumula `fieldErrors[]` con mensaje en blanco + mensaje 101 chars; `crear` con `destinatario`/`remitente`/`tipo` null; `reconstruir` no valida; `esVacio()`.
- `TipoSolicitudTest`: `desde("NOVEDAD_PARA_EL_COORDINADOR")`; `desde("x")` → `TipoSolicitudNoEncontradoException`; `desde(null)`/`desde(" ")`; `esValido`; `getId()` = `name()`; `getNombre()` comparado con la fila del `data/`.
- `RemitenteExisteRuleTest` / `DestinatarioExisteRuleTest` / `SolicitudUnicaRuleTest`: no lanza vs lanza su excepción.
- `UsuarioDomainTest` / `RemitenteDomainTest` / `DestinatarioDomainTest`: `crear` (obligatoriedad de campos en `UsuarioDomain`) + `reconstruir` + getters.
- `EnvioSolicitudNovedadCoordinadorDomainTest`: `crear` válido con los 3 objetos; `crear` con `solicitud`/`remitente`/`destinatario` null → `fieldErrors[]` acumulados; getters (`getSolicitud`, `getRemitente`, `getDestinatario`, `getRemitenteUsuario`, `getDestinatarioUsuario`).

### Application (`solicitudes:application`) — Mockito
- `EnviarSolicitudNovedadCoordinadorMapperTest`: `toDomain(command)` → `EnvioSolicitudNovedadCoordinadorDomain` con `getRemitenteUsuario()`/`getDestinatarioUsuario()` = los del command, `getSolicitud().getMensajeSolicitud()` con trim, `getSolicitud().getTipoSolicitud() == NOVEDAD_PARA_EL_COORDINADOR`, y `getSolicitud()` referenciando los ids de los candidatos (`getRemitente().getId()` / `getDestinatario().getId()`); sin tocar mocks de puertos (mapper puro, sin I/O).
- `EnviarSolicitudNovedadCoordinadorUseCaseImplTest` (recibe un `EnvioSolicitudNovedadCoordinadorDomain` construido con `EnviarSolicitudNovedadCoordinadorMapper.toDomain(...)` en el `// Arrange`):
  - flujo feliz: `verify(solicitudOutputPort).registrar(...)` + `verify(solicitudEventoOutputPort).publicar(...)` con `ArgumentCaptor` sobre el `EVENT_TOPIC` y el `solicitudId`; retorna el id.
  - remitente no existe → `RemitenteNoEncontradoException`, no persiste, no publica, **no llama al get-or-create** (`verifyNoInteractions(remitenteOutputPort)`).
  - destinatario no existe → `DestinatarioNoEncontradoException`.
  - solicitud duplicada → `SolicitudDuplicadaException`.
  - get-or-create remitente: `RemitenteDeUsuarioFinder` → `empty` ⇒ `verify(remitenteOutputPort).registrar(...)` con el candidato `envio.getRemitente()`; → id presente ⇒ `verify(remitenteOutputPort, never()).registrar(...)` y la solicitud persistida referencia el id existente.
  - reconstrucción: la `SolicitudEntity` persistida lleva los ids de fila **resueltos** (no los candidatos) cuando el finder devuelve un id existente.
  - orden: existencia de usuario antes que get-or-create antes que unicidad (dos fallos simultáneos → gana la de existencia).
- `EnviarSolicitudNovedadCoordinadorValidatorImplTest`: con las Rules reales, cada excepción y su orden.
- `EnviarSolicitudNovedadCoordinadorCommandTest`: `crear` válido; `destinatario` no-UUID; mensaje en blanco / largo; `remitente` no-UUID.
- `RegistrarUsuarioUseCaseImplTest`: upsert — `existePorId=false` → `registrar`; `true` → `actualizar`.

### Infrastructure (`solicitudes:infrastructure`)
- `SolicitudCommandOutputAdapterTest` (`@DataJpaTest` + H2, `TestEntityManager`): siembra `tipo_solicitud`, `remitente`, `destinatario`; `registrar` inserta; `existePorCombinacionUnica` true/false.
- `UsuarioCommandOutputAdapterTest` / `RemitenteCommandOutputAdapterTest` (`@DataJpaTest`): `registrar`, `actualizar`/`buscarIdPorUsuario`, `existePorId`.
- `EnviarSolicitudNovedadCoordinadorControllerTest` (`@WebMvcTest` + `@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class, TestSecurityConfig.class})`, `@MockitoBean` del `Interactor`, `jwt().authorities(new SimpleGrantedAuthority(SolicitudesAuthorities.SOLICITUD_CREATE))`):
  - 201 + body `{id}` + captor: el `sub` del jwt llega como remitente al `Command`.
  - 422 mensaje en blanco / largo / destinatario no-UUID (`fieldErrors[]`).
  - 401 sin token; 403 con jwt sin el authority.
- _(sin test de adaptador de eventos — no hay adaptador propio)_. La publicación se verifica en `EnviarSolicitudNovedadCoordinadorUseCaseImplTest` con `verify(eventPublisher).publish(captor)` sobre `getTemaEvento()`/`solicitudId`/usuarios, y `verify(eventPublisher, never())` en los casos de excepción.
- `UsuarioCreadoConsumerTest`:
  - payload con `identificador`+`nombre` → `verify(registrarUsuarioInteractor).ejecutar(commandEsperado)`.
  - **payload sin `identificador`/`nombre` (contrato viejo) → no-op: `verifyNoInteractions(registrarUsuarioInteractor)`** (guarda de inercia P1).

**No testear:** getters/setters, métodos `private`, `*DTO`/`*Command`/`*ReadModel`/`*Entity` (excluidos de jacoco), `config/**`.

---

## 13. Checklist de Implementación

- [ ] `settings.gradle` con los 4 `include` de `solicitudes*`; 4 `build.gradle` nuevos copiados de `fichas`
- [ ] `init-db.sql`: `CREATE DATABASE solicitudes` + bloque `\c solicitudes` con GRANT/ALTER SCHEMA
- [ ] `docker-compose.yml` (servicio `backend`), `.env.example`, `application.yml`, `application-prod.yml`: variables/bloque `datasource.solicitudes` / `DB_SOLICITUDES_*`
- [ ] `SolicitudesDataSourceConfig` completo (DataSource + EMF de **un** paquete + `solicitudesTransactionManager` + `solicitudesFlyway` → `classpath:db/migration/solicitudes`, `baselineOnMigrate=false`)
- [ ] Migración base = `mer/11_tablas_solicitudes.sql` **verbatim** (`usuario.identificador`/`nombre` **NOT NULL**), + INSERTs de catálogo de `mer/data/11_data_solicitudes.sql`; timestamp al crear; sin prefijo de base/schema; sin FK cruzada
- [ ] **Datos demo:** sin archivo en el repo — bloque `INSERT ... ON CONFLICT DO NOTHING` de referencia documentado en §11.2, ejecución manual opcional en local. **NO** migración Flyway, **NO** datos de catálogo, **NO** archivo empaquetado
- [ ] **SE crea** tabla `event_publication` en `solicitudes` (`V20260827175416__crear_event_publication.sql`, verbatim de `fichas`) — outbox estándar
- [ ] `SolicitudDomain`: constructor privado, campos no-`final` con setters privados que cortan con `return`, solo getters, `crear`/`reconstruir` (no `build`/`rebuild`), sin Lombok, sin subcarpeta `aggregate/`, centinela `VACIO` + `esVacio()`
- [ ] **Objeto de acción `EnvioSolicitudNovedadCoordinadorDomain`** (`domain/solicitud/`, sin subpaquete) agrupa `SolicitudDomain` + `RemitenteDomain` + `DestinatarioDomain`, `crear(...)` con Notification Pattern (patrón `RegistroFichaPerfilDomain`); construido por **`EnviarSolicitudNovedadCoordinadorMapper`** (`application/solicitud/command/primaryport/mapper/`, `static toDomain(command)`, **sin I/O**); el `Interactor` llama al mapper y pasa el objeto de acción al use case; el use case opera sobre `envio.getSolicitud()`/`getRemitente()`/`getDestinatario()` y reconstruye el `SolicitudDomain` con las FKs reales tras el get-or-create (P10)
- [ ] Invariantes locales (mensaje 1–100, obligatorios) acumuladas en `ValidationResult` → 422 `fieldErrors[]`, sin excepción propia
- [ ] Restricciones de conjunto (RN-3/4/5) como `Rule` + record, orquestadas por el `Validator` → 422 con su `DomainException`; ningún `if/throw` de negocio en el use case
- [ ] `Validator` puro: constructor sin argumentos con `new …RuleImpl()`, sin `Finder`, sin `OutputPort`, sin `if`
- [ ] POL-06 por construcción (tipo fijo) — **no** hay `Rule` de tipo (P5)
- [ ] `Finder`s: `UsuarioExisteFinder` (1 puerto, 2 consumidores), `SolicitudDuplicadaFinder`, `RemitenteDeUsuarioFinder`, `DestinatarioDeUsuarioFinder`; devuelven valor, nunca lanzan por "no encontrado"
- [ ] Sin `Optional` en firmas del `Validator` ni en records de `Rule`
- [ ] **Evento por camino estándar (como `fichas`):** `event/SolicitudNovedadCoordinadorEnviadaEvent` (extiende `DomainEvent`); el `UseCase` inyecta la interfaz `EventPublisher` de `shared:application` y llama `eventPublisher.publish(evento)` tras persistir. Sin puerto/adaptador locales. Outbox `event_publication` + externalización a `arquisoft.events` vía `ModulithAmqpExternalizationConfig` (`shared:amqp` sin cambios). Solo lado publicación — sin `Queue`/`Binding`, sin consumidor
- [ ] IDs siempre `UUID`; `fecha_creacion` `LocalDateTime` generada en `SolicitudDomain.crear`
- [ ] `Interactor` dueño de `@Transactional(transactionManager = "solicitudesTransactionManager")` (unidad de trabajo + tx manager correcto); `UseCase` sin transacción; el `UseCase` no implementa `Interactor`
- [ ] `OutputPort` habla `Entity` (record plano), nunca `Domain`; `tipoSolicitud` viaja como `String` (`getId()`) en `SolicitudEntity`
- [ ] `CommandOutputAdapter`s sin `try/catch`, `save` (no `saveAndFlush`), `existePor…` primitivo `boolean`, log `debug` en escritura con `AppLogger`
- [ ] Excepciones nuevas extienden `DomainException` (422) en `domain/{feature}/exception/` del slice (no a nivel de contexto)
- [ ] Identificadores del body como `String`, validados en `Command.crear` con `ValidatorUUID`; `@AuthenticationPrincipal Jwt` para el remitente, nunca del body
- [ ] `RequestDTO`/`ResponseDTO` = `record` desnudos; `RequestMapper` externo `static toCommand`; sin Jakarta, sin Lombok, sin `toCommand()` en el DTO
- [ ] Controller con `@Tag`/`@Operation`/`@ApiResponses`/`@SecurityRequirement` (ADR-011), un controller por acción, ruta como placeholder de propiedad con default
- [ ] `@PreAuthorize(SolicitudesAuthorities.Expresiones.HAS_SOLICITUD_CREATE)` — constante; un solo client role
- [ ] `TipoSolicitud` en `domain/tiposolicitud/` (P6), constantes copiadas fila por fila de `mer/data/11_data_solicitudes.sql`, `desde`/`esValido`/`getId`/`getNombre`, nunca `valueOf` fuera del enum; ancho de tabla 60/60/300 (nueva), sin `ALTER`
- [ ] Réplica `usuario` **NOT NULL** en las 4 columnas; poblada por `UsuarioCreadoConsumer` (con **guarda de inercia**: no-op si falta `identificador`/`nombre`) o por el seed; `RegistrarUsuarioInteractor` con `@Transactional` porque **sí** persiste
- [ ] `remitente`/`destinatario`: get-or-create perezoso en el use case, idempotente por `UNIQUE(usuario_id)` — no se siembran (los crea el use case)
- [ ] **Dependencia externa documentada (sección 4.4):** contrato de eventos enriquecido de `usuarios` (`UsuarioCreadoEvent` + `UsuarioActualizadoEvent` + `UsuarioDesactivadoEvent`) — HT aparte, NO parte de HU081
- [ ] Catálogo: `SolicitudKey`/`TipoSolicitudKey`/`UsuarioReplicaKey` + registro en `ClavesCatalogo` + `catalogo/solicitudes.properties`, aridad correcta (`%s` cliente, `{}` log); `SolicitudesCodes`/`Fields`/`Limits`/`ApiMessages` en `shared:message`
- [ ] Tests AAA, `debeHacerAlgo_cuandoCondicion()`, `@MockitoBean` (no `@MockBean`), `jwt().authorities(...)` (no `@WithMockUser`), cobertura ≥ 75 % con `check`
- [ ] `SolicitudesInfrastructureTestApplication` ancla creada en `src/test/`
- [ ] Sin `@Bean TaskExecutor` manual; sin `@Slf4j` (usar `AppLogger`)
- [ ] `./gradlew :solicitudes:domain:test :solicitudes:application:test :solicitudes:infrastructure:test checkstyleMain checkstyleTest` verde; `./gradlew build` levanta el contexto (Flyway migra la base `solicitudes`)
- [ ] Commit sugerido: `feat(solicitudes): enviar solicitud de novedad para el coordinador y bootstrapping del contexto`

---

## 14. Trazabilidad del Flujo

| Etapa | Agente | Estado | Fecha | Notas |
|---|---|---|---|---|
| Planificación | @planificador | ✅ Revisado con decisiones del usuario 2026-08-27 | 2026-08-27 | P1–P10 resueltas (sección 0). Evento in-process sin RabbitMQ (desviación aprobada); réplica `usuario` NOT NULL; contrato de `usuarios` enriquecido = dependencia externa (HT aparte); `command/primaryport/mapper/` con objeto de acción `EnvioSolicitudNovedadCoordinadorDomain` (P10) |
| Desarrollo | @implementador | ✅ Completado | 2026-08-27 | Bootstrapping completo del contexto `solicitudes` (4 módulos Gradle, base `solicitudes`, `SolicitudesDataSourceConfig`, migración base + seed demo, `SolicitudesUsuariosQueueConfig`) + caso de uso HU-081 en las 3 capas. Evento in-process vía `SolicitudEventoOutputPort`/`SolicitudEventoSpringOutputAdapter` (P2/P3). `UsuarioCreadoConsumer` inerte (P1). `command/primaryport/mapper/` con objeto de acción `EnvioSolicitudNovedadCoordinadorDomain` (P10). Fill-ins fuera de §6.12: nested `Usuario`/`Remitente`/`Destinatario` en `SolicitudesCodes`/`SolicitudesFields` (requeridos por §4.3). Nota: `:shared:message:test` tiene 1 fallo PREEXISTENTE y ambiental (`debeSustituirParametros`, encoding UTF-8 en Windows) — no es regresión de esta HU (falla igual en `git stash -u`). |
| Corrección post-@validator-analyze | @implementador | ✅ Completado | 2026-08-27 | Atendidos 1 bloqueante + 2 menores. **BLOQUEANTE** `LocalDateTime.now()` directo en `SolicitudDomain` → **Opción A**: nuevo accesor `UtilFecha.generarFechaHoraActual()` (`LocalDateTime`) + centinela `UtilFecha.FECHA_HORA_VACIA` en `shared:util` (con `UtilFechaTest`). `SolicitudDomain.setFechaCreacion()` usa el accesor; `VACIO` usa `UtilFecha.FECHA_HORA_VACIA` (patrón `EstadoFichaPerfilDomain.VACIO`). Opción A porque el MER modela `fecha_creacion` como `TIMESTAMP` y el plan (§4.1/§7/payload) fija `LocalDateTime` end-to-end. **MENOR 1**: bloque duplicado `// Contexto 10` en `settings.gradle` deduplicado. **MENOR 2**: Javadoc → comentario `//` de 1–3 líneas en `SolicitudEventoSpringOutputAdapter`, `SolicitudesDataSourceConfig`, `UsuarioCreadoConsumer`. Gate completo verde: `:solicitudes:{domain,application,infrastructure}:test` + los 3 `jacocoTestCoverageVerification` (90.0% / 98.6% / 100%) + `:shared:util:test` + `checkstyleMain`/`checkstyleTest` (`BUILD SUCCESSFUL`). Ningún test de @tester requirió cambios (el tipo de fecha sigue siendo `LocalDateTime`). |
| Tests | @tester | ✅ Completado | 2026-08-27 | 79 tests (domain 25, application 29, infrastructure 25), 0 fallos. Gate verde: `:solicitudes:{domain,application,infrastructure}:test` + los 3 `jacocoTestCoverageVerification` + `checkstyleMain`/`checkstyleTest`. **Cobertura por módulo (instrucción, JaCoCo):** `domain` 90.0% — CUMPLE; `application` 98.6% — CUMPLE; `infrastructure` 100% — CUMPLE (umbral 75%). Domain: `SolicitudDomain` (incl. `VACIO`/`esVacio`, `fieldErrors[]` acumulados), `EnvioSolicitudNovedadCoordinadorDomain`, `TipoSolicitud` (`desde`/`esValido`/`getId`/`getNombre`), 3 Rules puras, `UsuarioDomain`/`RemitenteDomain`/`DestinatarioDomain`. Application: `Command.crear`, `EnviarSolicitud...Mapper` (bundle), `Validator` (orden + regla dependiente), `UseCaseImpl` (captor evento in-process `EVENT_TOPIC`+`solicitudId`, get-or-create idempotente reuso/persistir-candidato, reconstrucción con FKs reales, `inOrder`), `RegistrarUsuarioUseCaseImpl` (upsert), 4 Finders, mappers de secondaryport, `InteractorImpl`. Infrastructure: `EnviarSolicitud...ControllerTest` (`@WebMvcTest`, 201+captor sub→remitente, 400×2, 422×2, 401, 403), `SolicitudCommandOutputAdapterTest` + `Remitente`/`Destinatario`/`UsuarioCommandOutputAdapterTest` (`@DataJpaTest`+H2, seed vía `TestEntityManager`), `JpaMappersConversionTest`, `SolicitudEventoSpringOutputAdapterTest`, `UsuarioCreadoConsumerTest` (incl. no-op de inercia P1 sin `identificador`/`nombre`). Se quitó el `failOnNoDiscoveredTests = false` de `solicitudes/infrastructure/build.gradle` (ya hay slices reales con cobertura ≥75%). **Para @validator-analyze:** (1) CA-2/CA-3/CA-4 del plan dicen 422 pero `EnviarSolicitudNovedadCoordinadorCommand.crear` cierra con `lanzarSiTieneErroresDeEntrada()` → `ApplicationValidationException` (**HTTP 400**); los tests del controller asertan `isBadRequest()` siguiendo `RegistrarFichaPerfilControllerTest` (mismo patrón, gate del implementador verde) — anotado, NO "arreglado". (2) `RemitenteMapper.toDomain`/`DestinatarioMapper.toDomain`/`UsuarioMapper.toDomain` y `SolicitudJpaMapper.toEntity`/`Remitente`·`DestinatarioJpaMapper.toEntity` no se ejercen en el flujo de producción hoy (solo se cubren por tests) — candidatos a revisión de código muerto. (3) `:shared:message:test` mantiene 1 fallo ambiental preexistente (`CatalogoCargaTest.debeSustituirParametros`, encoding UTF-8 en Windows) — no tocado. |
| Validación | @validator-analyze | ✅ APROBADO — Score ≈97/100, 0 bloqueantes | 2026-08-27 | Re-validación ronda 2. Bloqueante de ronda 1 (`LocalDateTime.now()` directo en `SolicitudDomain`) cerrado vía `UtilFecha.generarFechaHoraActual()` + `UtilFecha.FECHA_HORA_VACIA` en `shared:util`. 2 menores de ronda 1 cerrados (`settings.gradle` deduplicado, Javadoc→`//`). 4 menores remanentes sin acción requerida (CA-2/3/4 del plan dicen 422 pero la convención `Command.crear` devuelve 400 — corregir texto del plan; fallo ambiental preexistente `:shared:message:test`). Gate verde: tests + 3 `jacocoTestCoverageVerification` (90.0/98.6/100%) + checkstyle. |
| Reporte | @validator-report | ✅ Persistido (ronda 3) | 2026-08-27 | `.workspace/validator/validator-HU-081.md` — sobrescrito con el análisis de ronda 3 (cambio de diseño del evento al camino estándar de `fichas`). Estado APROBADO · Score ≈98/100 · 0 bloqueantes |
| Ajuste post-validación | @implementador | ✅ Completado | 2026-08-27 | **Cambio de diseño autorizado por el usuario tras la aprobación:** el evento pasa a manejarse **exactamente como `fichas`** — puerto `EventPublisher` de `shared:application` + outbox de Spring Modulith + externalización AMQP. Revertida la desviación P2/P3/§10. **Borrados:** `SolicitudEventoOutputPort` (application), `SolicitudEventoSpringOutputAdapter` + carpeta `evento/` + `SolicitudEventoSpringOutputAdapterTest` (infrastructure). `EnviarSolicitudNovedadCoordinadorUseCaseImpl` inyecta `com.arquisoft.shared.publisher.EventPublisher` y llama `eventPublisher.publish(evento)` en el mismo punto (tras persistir, en la tx del interactor). Nueva migración `V20260827175416__crear_event_publication.sql` (verbatim de `fichas`, cabecera nombra `solicitudesTransactionManager`; la autodetecta `ContextAwareEventPublicationRepository`). `SolicitudNovedadCoordinadorEnviadaEvent` y los `build.gradle` sin cambios. Test del use case actualizado (mock `EventPublisher`, `verify(...).publish(captor)` sobre `getTemaEvento()`/`solicitudId`/usuarios, `never()` en excepciones, `inOrder` intacto). **Alcance: SOLO lado publicación** — sin consumidor, sin `notificaciones`, sin `Queue`/`Binding` (se acepta el publisher-return warning). Plan actualizado: §0, §1, §2, §4.6, §6.2/6.6/6.8, §7, §10, §11.3, §12, §13. Gate completo verde + `./gradlew build -x test` OK (Flyway migra `event_publication` en `solicitudes`). |
| Re-validación | @validator-analyze | ✅ APROBADO — Score ≈98/100, 0 bloqueantes | 2026-08-27 | Ronda 3, tras el cambio de diseño del evento al **camino estándar de `fichas`** (`EventPublisher` de `shared:application` + outbox Spring Modulith + externalización AMQP). Verificados los 5 puntos: (1) evento como `CambiarAsesorFichaUseCaseImpl` — sin puerto/adaptador local; (2) migración `V20260827175416__crear_event_publication.sql` en subcarpeta del contexto, byte-idéntica a `fichas`, timestamp posterior a las base; (3) outbox atómico en la tx del interactor (`solicitudesTransactionManager`); (4) `application/build.gradle` y `shared:amqp` sin cambios; (5) §10 ya no describe una desviación. Elimina la única desviación arquitectónica que quedaba. 4 menores de ronda 2 remanentes sin acción requerida (CA-2/3/4 del plan dicen 422 vs. 400 de la convención; fallo ambiental preexistente `:shared:message:test`). Gate verde: tests + 3 `jacocoTestCoverageVerification` (90.0/98.6/100%) + checkstyle + `assemble`. |
| Fix post-pruebas | @implementador | ✅ Completado | 2026-08-28 | **`FullyQualifiedAnnotationBeanNameGenerator` en `ArquisoftApplication` (colisión de nombres de bean entre contextos).** Con `solicitudes` en el classpath la app no arrancaba: `ConflictingBeanDefinitionException` para `usuarioCreadoConsumer` (`solicitudes` ↔ `fichas`), y detrás `RegistrarUsuarioUseCaseImpl` (↔ `fichas`) y `UsuarioCommandOutputAdapter` (↔ `usuarios`). Causa: el scan raíz de `@SpringBootApplication` nombra los `@Component` por nombre simple. Fix: `@SpringBootApplication(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)` (forma soportada de Spring Boot; evita el doble-scan de un `@ComponentScan` explícito). Seguro: 0 `getBean(` en producción, 0 `@Component("nombre")` explícito, y los `@Qualifier` del repo apuntan solo a beans de método `@Bean` (cuyo nombre NO cambia el generador). **App verificada: `Started ArquisoftApplication in 48.363 s`, `Tomcat started on port 8080 '/api'`** — arranca de verdad, Flyway migra las de `solicitudes` (esquema base + `event_publication`). Ningún test afectado (no hay `@SpringBootTest`; los slices usan sus propios anclas). Plan: §6.1 (fila `ArquisoftApplication`). Gate verde + `assemble` + `bootRun`. |
| Ajuste post-validación | @implementador | ✅ Completado | 2026-08-28 | **Patrón de rutas + grupo OpenAPI, igual que `fichas`/`usuarios`/`seguridad`.** Nuevo `solicitudes/infrastructure/src/main/resources/rutas-solicitudes.yml` (`rutas.solicitudes.solicitud.base: /solicitudes`, `.novedad-coordinador: /novedad-coordinador`; encabezado idéntico a `rutas-fichas.yml`; vive en el módulo del contexto). Controller pasa de la clave única al patrón base+sub (`RegistrarEvaluacionFichaPerfilController`): `@RequestMapping("${rutas.solicitudes.solicitud.base:/solicitudes}")` + `@PostMapping("${rutas.solicitudes.solicitud.novedad-coordinador:/novedad-coordinador}")` — ruta efectiva sin cambio (`/api/solicitudes/novedad-coordinador`). `application.yml`: `- "classpath:rutas-solicitudes.yml"` tras `rutas-fichas.yml`. `OpenApiConfig`: `@Value solicitudesBasePath` + `@Bean solicitudesApi()` grupo `09-solicitudes`, displayName `"Solicitudes"` inline, `pathsToMatch(solicitudesBasePath + "/**")`, tras `evaluacionesApi()`. `SolicitudesApiMessages` sin cambios (displayName inline, como `fichas`). Tests del controller sin cambios (el slice `@WebMvcTest` resuelve los defaults del `@Value`). Plan actualizado: §6.8, §7, §8. Gate completo verde + `./gradlew assemble` OK. |
| Validación | @validator-analyze | ✅ APROBADO — Score ≈98/100, 0 bloqueantes | 2026-08-28 | Ronda 4, tras el ajuste del patrón de rutas + grupo OpenAPI (`rutas-solicitudes.yml` en el módulo infrastructure, `config.import` en `application.yml`, controller con placeholders base+sub, grupo `09-solicitudes`). Verificados los 5 puntos: (1) `rutas-solicitudes.yml` verbatim del encabezado de `rutas-fichas.yml`, un placeholder por segmento; (2) import bien colocado tras `rutas-fichas.yml`, cubre dev+prod; (3) controller base+sub como `RegistrarEvaluacionFichaPerfilController`, ruta efectiva sin cambio; (4) grupo `09-solicitudes` consistente (`@Value` base, `pathsToMatch(base + "/**")`, displayName inline, tras `evaluacionesApi()`); (5) nada de las rondas 1–3 roto. 4 menores remanentes sin acción requerida (CA-2/3/4 del plan dicen 422 vs. 400 de la convención; fallo ambiental preexistente `:shared:message:test`). Gate verde: tests + 3 `jacocoTestCoverageVerification` (90.0/98.6/100%) + checkstyle + `assemble`. |
| Reporte | @validator-report | ✅ Persistido (ronda 4) | 2026-08-28 | `.workspace/validator/validator-HU-081.md` — sobrescrito con el análisis de ronda 4 (patrón de rutas + grupo OpenAPI). Estado APROBADO · Score ≈98/100 · 0 bloqueantes |
| Validación | @validator-analyze | ✅ APROBADO — Score ≈98/100, 0 bloqueantes | 2026-08-28 | Ronda 5, tras el fix de arranque por colisión de nombres de bean (`@SpringBootApplication(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)` en `ArquisoftApplication`). Verificados los 5 puntos: (1) fix correcto y mínimo — 1 archivo, causa raíz confirmada (`ConflictingBeanDefinitionException` en `UsuarioCreadoConsumer`/`RegistrarUsuarioUseCaseImpl`/`UsuarioCommandOutputAdapter`); (2) 0 beans referenciados por nombre simple — `@Qualifier` (28) apuntan solo a beans de método `@Bean`, que el generador no altera; (3) no rompe rondas 1–4, slices de test intactos; (4) atributo `nameGenerator` es la vía recomendada frente a `@ComponentScan` explícito; (5) gap de proceso legítimo — el gate por módulo nunca arranca el contexto agregado. **App verificada: arranca de verdad, Flyway migra las 3 de `solicitudes`.** 4 menores remanentes sin acción + 1 observación de proceso para el owner. Gate verde: tests + 3 `jacocoTestCoverageVerification` (90.0/98.6/100%) + checkstyle + `assemble`. |
| Reporte | @validator-report | ✅ Persistido (ronda 5) | 2026-08-28 | `.workspace/validator/validator-HU-081.md` — sobrescrito con el análisis de ronda 5 (fix de arranque por colisión de nombres de bean). Estado APROBADO · Score ≈98/100 · 0 bloqueantes. Incluye "Observación de proceso" con la recomendación del punto 5 para el owner del proceso de validación (arranque real del contexto agregado en FASE 4 cuando el plan introduce un bounded context nuevo). |
| Ajuste post-pruebas (ronda 6) | @implementador | ✅ Completado — **superado por ronda 7** | 2026-08-28 | Seed demo sacado de migración Flyway (`V20260827144742` borrada) a script suelto `db/seed/seed-demo-solicitudes.sql`. **Superado:** en ronda 7 el script se eliminó por completo del repo. |
| Ajuste post-pruebas (ronda 7) | @implementador | ✅ Completado | 2026-08-28 | Script `db/seed/seed-demo-solicitudes.sql` + carpeta `db/seed/` **eliminados del repo** (observación O-1 de @validator-analyze ronda 6: sin convención `db/seed/`, ruido inerte en el jar). Conocimiento preservado en el plan §11.2 como bloque SQL de referencia (placeholders `<sub-estudiante>`/`<sub-coordinador>`, `ON CONFLICT DO NOTHING`; `remitente`/`destinatario` NO se siembran — get-or-create). Módulo `solicitudes` bajo `db/`: solo las 2 migraciones. Plan: §0 P1/P8, §1, §6.2, §11.2, §13, §14. Ningún Java tocado. Gate completo verde + `assemble`. |
| Validación | @validator-analyze | ✅ **APROBADO** — Score ≈98/100, 0 bloqueantes | 2026-08-28 | **Ronda 7 (final).** Verificado: (1) cero rastro de `db/seed/` en módulo y jar (jar solo con las 2 migraciones); (2) knowledge de seeding preservado en §11.2 (bloque SQL + nota get-or-create para roles); (3) rondas 1–6 intactas (2 migraciones, evento camino `fichas`, `FullyQualifiedAnnotationBeanNameGenerator`, `UtilFecha`, rutas, `event_publication`); (4) plan sin refs a archivo de seed inexistente; (5) O-1 resuelta, O-2 vigente para @commit (limpiar `flyway_schema_history` de la migración `V20260827144742` en entornos que ya la aplicaron). 4 menores heredados sin acción (CA 422↔400 = convención; fallo ambiental `:shared:message:test`; mappers bidireccionales; validator de 2 métodos). Historia de rondas: 1) RECHAZADO por `LocalDateTime.now()` directo en dominio → 2) corregido con `UtilFecha` + dedup `settings.gradle` + Javadoc infra → APROBADO → 3) evento migrado de costura in-process al camino estándar de `fichas` → 4) patrón de rutas + grupo OpenAPI → 5) `FullyQualifiedAnnotationBeanNameGenerator` (fix de arranque) → 6) seed fuera de Flyway → 7) seed eliminado del repo. Gate final verde: tests + 3× `jacocoTestCoverageVerification` (90.0/98.6/100 %) + `checkstyleMain`/`checkstyleTest` + `assemble`. |
| Reporte | @validator-report | ✅ Persistido | 2026-08-28 | Ronda 7 (final). `.workspace/validator/validator-HU-081.md`. |
| Añadido RN-7 (validación de asignación destinatario↔estudiante) | usuario + Claude | ✅ Completado — `BUILD SUCCESSFUL` | 2026-09-05 | Decidido con Brayan (transcripción de reunión): la validación "el destinatario es el coordinador/asesor asignado" se hace con **consulta síncrona a `proyectos` vía `shared:web-client`**, no con réplica por eventos. Alcance de este cambio: **solo lado `solicitudes`, solo HU-081**. `shared:web-client` lo crea Brayan+Juan aparte. Nuevos: `ExistenciaAsignacionResponsable`/`ConsultaAsignacionResponsable` (domain model), `DestinatarioAsignadoRule`(+impl), `DestinatarioNoAsignadoException` (422), `AsignacionProyectoOutputPort` (application, feature `asignacionproyecto`), `DestinatarioAsignadoFinder`(+impl), `AsignacionProyectoOutputAdapter` (**STUB** — devuelve `true`, `warn`). Modificados: `EnviarSolicitudNovedadCoordinadorValidator`(+impl) `+validarAsignacionDestinatario`, `EnviarSolicitudNovedadCoordinadorUseCaseImpl` (inyecta el finder, llama tras `validarExistenciaUsuarios`), `SolicitudKey` (+2 claves), `SolicitudesCodes.Solicitud` (+`DESTINATARIO_NO_ASIGNADO`), `catalogo/solicitudes.properties` (+2). Tests: `DestinatarioAsignadoRuleImplTest`, `DestinatarioAsignadoFinderImplTest`, `AsignacionProyectoOutputAdapterTest` (nuevos); `EnviarSolicitudNovedadCoordinadorValidatorImplTest` + `...UseCaseImplTest` (actualizados). CLAUDE.md: nueva § *Synchronous cross-context queries*, excepción a "never import each other", carve-out de "single consumer", fila en *Known deviations*. RN-7 §3.1 con checklist de activación. Catálogo cargado en Redis remoto (`172.16.1.12`, 273 claves, `solicitudes` 9→11). Build verde: tests + checkstyle + jacoco. |
| Commit | @commit | ⏳ Pendiente | | |
| PR | @commit | ⏳ Pendiente | | |
