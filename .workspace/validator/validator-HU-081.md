# Reporte de Validación — HU-081 (Ronda 8: RN-7 + Parte A + Parte B — enriquecimiento del evento + correo al coordinador)

## Metadata
- Bounded Context: `solicitudes` (+ `notificaciones`, `shared:message`)
- Fecha: 2026-09-05 · Rama: `feature/HU-081-enviar-solicitud-novedad-coordinador`
- Plan validado: `.workspace/h-plan/PLAN-HU-081.md` (§3.1 RN-7 + §10 revisado + trazabilidad)

## Score
| Nivel | Checks | Pasados | Fallados | Score |
|---|---|---|---|---|
| 1 — Completitud | 12 | 12 | 0 | 25/25 |
| 2 — Convenciones DDD + Arquisoft | 30 | 28 | 2 menores restantes | 37/40 |
| 3 — Compilación | — | gate del usuario `BUILD SUCCESSFUL` (el validador no ejecuta Gradle en su entorno) | — | OK |
| 4 — Tests | — | ✅ Completado + validado end-to-end con Mailpit | — | OK |
| Total | | | | **≈95/100** |

Bloqueantes: 0

## Estado Final
> ✅ APROBADO — sin bloqueantes.

## Menores corregidos en esta ronda (por el implementador, post-análisis)
- **[2.6]** `DatosUsuarioFinder` devolvía `Optional<UsuarioEntity>` (el `Entity` del puerto). **Corregido:** ahora devuelve `Optional<UsuarioDomain>`, mapeando `Entity → Domain` con `UsuarioMapper::toDomain` al volver (convención `arquisoft-arquitectura` → "the finder maps Entity → Domain after it returns"). De paso `UsuarioMapper.toDomain` deja de ser código muerto.
- **[2.12]** El `logger.info(LOG_ENVIADA)` de cierre quedaba **antes** del `eventPublisher.publish(...)` tras la Parte A. **Corregido:** movido a última sentencia de `ejecutar` (después de `publish`, antes del `return`).

## Menores remanentes (SIN acción requerida)
- **[2.13]** Falta `SolicitudNovedadCoordinadorEnviadaPayloadTest` (prueba de contrato serialización productor↔consumidor). Atenuante: el repo es inconsistente — `EstudiantesFichaPerfilAsignadosPayload` y `FichaPerfilRegistradaPayload` tampoco lo tienen. Recomendado, no bloqueante.
- **[2.12]** `LOG_ASIGNACION_NO_VERIFICADA` cuelga del enum `SolicitudKey` y del segmento `solicitud`, aunque el `AsignacionProyectoOutputAdapter` vive en la feature `asignacionproyecto`. Pragmático para un stub de una línea que se borra al activar RN-7 (la clave se retira con él).

## Observaciones (cerradas o sin acción)
- **O-1 — patrón "consulta síncrona entre contextos" vs. skill:** RESUELTA en esta ronda. `arquisoft-arquitectura/SKILL.md`, `1-planificador.md` y `4a-validator-analyze.md` fueron actualizados con la sección/checks del patrón (además de lo que ya estaba en `CLAUDE.md`).
- **O-2 — `AsignacionProyectoOutputAdapter` es un STUB deliberado:** devuelve `true` + `logger.warn`, documentado en `PLAN-HU-081.md §3.1` (con checklist de activación) y `CLAUDE.md` (§ *Consultas síncronas entre contextos* + *Desviaciones conocidas*). RN-7 no rechaza nada hasta que existan `shared:web-client` y la query de `proyectos`. No es precedente para código nuevo.

## Verificación — las 8 piezas del patrón "Transición de estado ⇒ notificación" (Parte B)
| # | Pieza | Estado |
|---|---|---|
| 1 | `EventTopics.Solicitudes.NOVEDAD_COORDINADOR_ENVIADA` | ✅ constante nueva, formato `{contexto}.{entidad}.{accion}` |
| 2 | `SolicitudNovedadCoordinadorEnviadaEvent` extiende `DomainEvent`, `EVENT_TOPIC` desde `EventTopics`, 5 campos | ✅ |
| 3 | `NotificacionesSolicitudesQueueConfig` `@Bean Declarables` vía `ColaEvento.declarar` (cola + `.dead` + 2 bindings; nombre de cola = expresión constante) | ✅ |
| 4 | `SolicitudNovedadCoordinadorEnviadaPayload` — `record` propio del adaptador con `idEvento` + `ocurridoEn` | ✅ |
| 5 | `SolicitudNovedadCoordinadorEnviadaConsumer` extends `AbstractNotificacionConsumer` (copia de `AsesorFichaCambiadoConsumer`; `plantilla(...)`, `registrar(...)`, `withCorrelation(...)`; log de entrada con `logger.info(ClaveMensaje, args)` forma #94) | ✅ |
| 6 | `TipoNotificacion.SOLICITUD_NOVEDAD_COORDINADOR` | ✅ |
| 7 | `TipoNotificacionEvento.SOLICITUD_NOVEDAD_COORDINADOR` — `TipoNotificacionEventoTest` cubre parity | ✅ |
| 8 | `PlantillaKey.ASUNTO_/CUERPO_*` (aridad 1/3) + `ConsumidorKey.LOG_*` (aridad 2) + textos en `catalogo/notificaciones.properties`; pie reutiliza `PIE_GENERICO` | ✅ |
- Routing key por constante compartida (`EventTopics.Solicitudes.*` en productor y binding) ✅
- Paquete del consumer `amqp/solicitudes/solicitud/` — 2 segmentos ✅
- Correo enmascarado en el log de recepción (`UtilTexto.enmascararCorreo`) ✅
- `EnviarNotificacionCommand.crear(...)` desde el payload (no `new`) ✅
- Sin migración (`TipoNotificacion` es VARCHAR), sin client role nuevo ✅

## Tests
- Gate verde reportado por el usuario (`test` + `checkstyleMain`/`checkstyleTest` + jacoco) + validado end-to-end con Mailpit (POST → 201 → cola → consumer → correo con asunto/cuerpo/pie renderizados).
- Nuevos: `DestinatarioAsignadoRuleImplTest`, `DestinatarioAsignadoFinderImplTest`, `AsignacionProyectoOutputAdapterTest`, `DatosUsuarioFinderImplTest`, `SolicitudNovedadCoordinadorEnviadaConsumerTest`.
- Actualizados: `EnviarSolicitudNovedadCoordinadorValidatorImplTest`, `...UseCaseImplTest`, `UsuarioCommandOutputAdapterTest`.
- `verify(eventPublisher).publish(captor)` sobre `getTemaEvento()`/`solicitudId`/nombre/email en el caso feliz; `never().publish()` en excepciones; `inOrder` existencia → asignación → get-or-create → unicidad → persistir → publicar.
- Falta: `SolicitudNovedadCoordinadorEnviadaPayloadTest` (menor).

## Próximos pasos
Entrega vía commit (lo hace el usuario/Claude, no @4c-commit). Publicar `PLAN-HU-081.md` + este reporte en `arquisoft-docs`.
