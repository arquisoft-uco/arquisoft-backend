# Reporte de Validación — HU-081 (Re-validación · Ronda 7 · Eliminación total del script de seed)

## Metadata
- **Bounded Context:** `solicitudes` (nuevo — bootstrapping completo)
- **Fecha:** 2026-08-28 · **Rama propuesta:** `feature/HU-081-enviar-solicitud-novedad-coordinador`
- **Cambio de esta ronda:** eliminado del repo `solicitudes/infrastructure/src/main/resources/db/seed/seed-demo-solicitudes.sql` + carpeta `db/seed/`. Cierra la observación O-1 de ronda 6. El conocimiento de cómo sembrar datos demo queda documentado en el plan §11.2 (bloque SQL de referencia, sin archivo).

## Score
| Nivel | Checks | Pasados | Fallados | Score |
|---|---|---|---|---|
| 1 — Completitud del plan | 16 | 16 | 0 | 16/16 |
| 2 — Convenciones DDD + Arquisoft | 46 | 46 | 0 | 46/46 |
| 3 — Compilación | 1 | 1 | 0 | OK |
| 4 — Tests + arranque | — | tests + cobertura 90.0/98.6/100 % · app arranca (`Started ArquisoftApplication`) | anti-patrones menores no bloqueantes | OK |
| **Total** | | | | **≈98/100** |

**Bloqueantes:** 0 · **Menores:** 4 (heredados, sin acción) · **Observaciones abiertas:** 1 (O-2, nota de despliegue para @commit)

## Estado Final
> ✅ **APROBADO** — sin bloqueantes.

Es el estado más limpio de la HU: el contexto `solicitudes` no arrastra ningún artefacto de datos demo en el repo ni en el jar; la vía para sembrar en local queda como documentación pura.

---

## Verificación de los 5 puntos solicitados

### (1) Cero rastro de `db/seed/` en el módulo y el jar — CONFIRMADO
- `find solicitudes -path "*resources/db*"` → solo `db/migration/solicitudes/` con **2 archivos** (`V20260827144741__crear_esquema_base_solicitudes.sql`, `V20260827175416__crear_event_publication.sql`). La carpeta `db/seed/` no existe.
- **Inspección del jar** (`solicitudes-infrastructure-1.0.0.jar`, reconstruido en este análisis): bajo `db/` solo `db/migration/solicitudes/` con esas 2 migraciones. Sin `db/seed/`, sin ningún `.sql` de seed.
- `grep -rn "db/seed|seed-demo|seed_demo|seed_demo_solicitudes"` sobre `*.java`/`*.gradle`/`*.yml`/`*.sql`/`*.properties` (excl. `build/`) → **0 resultados**.

### (2) El conocimiento de cómo sembrar demo quedó bien preservado en §11.2 — CONFIRMADO
- §11.2 retitulada "Datos demo para local — bloque SQL de referencia (NO hay archivo en el repo)".
- Contiene: el bloque `INSERT INTO usuario (...) VALUES (...) ON CONFLICT DO NOTHING` con placeholders `<sub-estudiante>` / `<sub-coordinador>`; instrucción de reemplazar por los `sub` reales del realm `arquisoft` (HT-011); nota explícita de que es opcional / manual / local / no repo / no despliegue.
- **Correcto sobre los roles:** aclara que `remitente` / `destinatario` **no** se siembran — los materializa el get-or-create del use case (§7 pasos 4–5), idempotente por `UNIQUE(usuario_id)`; solo hace falta la fila en `usuario`. Coherente con el flujo real del código.
- Rationale de la decisión documentado (sin convención `db/seed/`; ruido inerte en el jar; inmutabilidad de las migraciones Flyway).

### (3) Nada de rondas 1–6 se rompió — CONFIRMADO
- `git status` de esta ronda: solo se borró el archivo de seed y la carpeta (+ plan). **Ningún archivo Java tocado.**
- Gate verde: `:solicitudes:{domain,application,infrastructure}:test` + 3× `jacocoTestCoverageVerification` (90.0/98.6/100 %) + `checkstyleMain`/`checkstyleTest` → **EXIT 0**.
- **2 migraciones intactas** (timestamps `V20260827144741`, `V20260827175416` sin renumerar), evento por camino `fichas` (`EventPublisher` + outbox + externalización AMQP), `FullyQualifiedAnnotationBeanNameGenerator`, `UtilFecha.generarFechaHoraActual()`, `rutas-solicitudes.yml` + grupo `09-solicitudes`, `V20260827175416__crear_event_publication.sql` byte-idéntica a `fichas`: todos intactos.
- Ningún test referenciaba el seed; `@DataJpaTest` no carga Flyway.

### (4) El plan sin referencias a un archivo de seed inexistente — CONFIRMADO
- Barridas las menciones de `db/seed/` en §0 (P1, P8), §1, §6.2, §11.2, §13. Las únicas menciones restantes del concepto "seed" son: la **justificación de por qué no existe** (§0 P8, §11.2) y el **registro histórico** en §14 + `validator-HU-081.md` (artefactos de rondas previas que este reporte refresca).
- §6.2 y §11 describen consistentemente **2 migraciones**; ningún punto del plan describe un archivo de seed versionado ni bajo `db/`.

### (5) Cierre de O-1 y O-2 de la ronda 6
- **O-1 (script demo empaquetado inerte en el jar):** ✅ **RESUELTA** — al borrar el archivo, ya no viaja en el artefacto.
- **O-2 (entornos que ya aplicaron `V20260827144742` requieren limpiar `flyway_schema_history`):** sigue **abierta y vigente para @commit**. Con `validateOnMigrate` por defecto, una base que tenga la fila `20260827144742` fallará al arrancar esta rama con "applied migration not resolved". El orquestador ya limpió el único servidor afectado (`172.16.1.10`); cualquier otro entorno necesita el mismo borrado manual (fila de `flyway_schema_history` + filas demo) antes de desplegar. **A incluir en las notas de despliegue del PR.**

---

## Menores remanentes (heredados de rondas anteriores — SIN acción requerida)

1. **CA-2 / CA-3 / CA-4 dicen 422; la implementación devuelve 400** (`Command.crear` → `ApplicationValidationException` vía `lanzarSiTieneErroresDeEntrada`). Conforme a la convención del proyecto (idéntico a los 13 `Command.crear` de `fichas`/`seguridad`; el `ControllerTest` asserta `isBadRequest()`, ninguno asserta 500). Recomendación no bloqueante: alinear el texto de las CA del plan a 400.
2. **`:shared:message:test` — 1 fallo (`CatalogoCargaTest.debeSustituirParametros`).** No-regresión confirmada: mojibake de charset en Windows sobre un mensaje de `fichas` (`"El título ya existe"`); todos los checks estructurales del catálogo de `solicitudes` pasan.
3. **Mappers bidireccionales poco usados + `UsuarioCommandOutputAdapter.registrar` ≡ `.actualizar`.** Simetría prescrita por CLAUDE.md / upsert legítimo vía `save()`.
4. **`EnviarSolicitudNovedadCoordinadorValidator` con dos métodos** (`validarExistenciaUsuarios` / `validarUnicidad`) en vez de `validar(...)` único. Justificado por el orden de validación (plan §7); pura orquestación de Rules, cero `if`.

**Observación de proceso (ronda 5, para el owner del proceso — no de esta HU):** FASE 4 debería incluir un arranque real del `ApplicationContext` agregado cuando un plan introduce un bounded context nuevo — el gate por módulo no detecta colisiones de bean cross-contexto (fue lo que reventó el arranque en ronda 5).

---

## Confirmaciones de arquitectura (acumulado rondas 1–7)

Hexagonal + CQRS · `domain` solo ve `shared:domain` · sin imports entre bounded contexts · sin paquete `query/` (checks de existencia por `command` `OutputPort` + `Finder`) · Notification Pattern en agregados (constructor privado, campos no-`final`, setters que cortan con `return`, `crear`/`reconstruir`, centinela `VACIO`+`esVacio()`) · `Validator`/`Rule`/`Finder` puros · puertos que hablan `Entity` · `CommandOutputAdapter`s sin `try/catch`, `save` (no `saveAndFlush`), `existePor…` primitivo, `logger.debug` en escritura · evento por el patrón único (`EventPublisher` de `shared:application` + outbox Spring Modulith + externalización AMQP central; sin puerto/adaptador local) · `@Transactional(transactionManager = "solicitudesTransactionManager")` en el interactor (atomicidad de outbox) · **2 migraciones** con timestamp en `db/migration/solicitudes/`, `event_publication` byte-idéntica a `fichas`, sin FK cruzada, **sin archivo de seed** · `UtilFecha` único punto de `LocalDateTime.now()` · nombres de bean únicos vía `FullyQualifiedAnnotationBeanNameGenerator` · autorización por constante `SolicitudesAuthorities.Expresiones.HAS_SOLICITUD_CREATE`, client role `solicitudes:solicitud:create` kebab-case · DTOs `record` desnudos + `RequestMapper` externo · identificadores del body como `String` validados en `Command.crear` con `ValidatorUUID`, remitente del `Jwt` · catálogo de mensajes con aridad correcta, registrado en `ClavesCatalogo`/`ContextosCatalogo` · enum `TipoSolicitud` en `domain/tiposolicitud/` con `desde`/`esValido`/`getId`/`getNombre`, nunca `valueOf` fuera del enum · rutas placeholders base+sub con `rutas-solicitudes.yml` en el módulo + grupo OpenAPI `09-solicitudes` · **la app arranca, Tomcat 8080/api, Flyway migra las 2 de `solicitudes`**.

---

## §14 — Trazabilidad del flujo (contenido a persistir)

Reemplaza la fila `Validación / @validator-analyze` y ajusta el histórico así (añadiendo las filas de ajuste post-pruebas ronda 6 y 7 si no están, y dejando las de rondas 1-5 como están):

| Etapa | Agente | Estado | Fecha | Notas |
|---|---|---|---|---|
| Ajuste post-pruebas (ronda 6) | @implementador | ✅ Completado — **superado por ronda 7** | 2026-08-28 | Seed demo sacado de migración Flyway (`V20260827144742` borrada) a script suelto `db/seed/seed-demo-solicitudes.sql`. **Superado:** en ronda 7 el script se eliminó por completo del repo. |
| Ajuste post-pruebas (ronda 7) | @implementador | ✅ Completado | 2026-08-28 | Script `db/seed/seed-demo-solicitudes.sql` + carpeta `db/seed/` **eliminados del repo** (observación O-1 de @validator-analyze ronda 6: sin convención `db/seed/`, ruido inerte en el jar). Conocimiento preservado en el plan §11.2 como bloque SQL de referencia (placeholders `<sub-estudiante>`/`<sub-coordinador>`, `ON CONFLICT DO NOTHING`; `remitente`/`destinatario` NO se siembran — get-or-create). Módulo `solicitudes` bajo `db/`: solo las 2 migraciones. Plan: §0 P1/P8, §1, §6.2, §11.2, §13, §14. Ningún Java tocado. Gate completo verde + `assemble`. |
| Validación | @validator-analyze | ✅ **APROBADO** — Score ≈98/100, 0 bloqueantes | 2026-08-28 | **Ronda 7 (final).** Verificado: (1) cero rastro de `db/seed/` en módulo y jar (jar solo con las 2 migraciones); (2) knowledge de seeding preservado en §11.2 (bloque SQL + nota get-or-create para roles); (3) rondas 1–6 intactas (2 migraciones, evento camino `fichas`, `FullyQualifiedAnnotationBeanNameGenerator`, `UtilFecha`, rutas, `event_publication`); (4) plan sin refs a archivo de seed inexistente; (5) O-1 resuelta, O-2 vigente para @commit (limpiar `flyway_schema_history` de la migración `V20260827144742` en entornos que ya la aplicaron). 4 menores heredados sin acción (CA 422↔400 = convención; fallo ambiental `:shared:message:test`; mappers bidireccionales; validator de 2 métodos). Historia de rondas: 1) RECHAZADO por `LocalDateTime.now()` directo en dominio → 2) corregido con `UtilFecha` + dedup `settings.gradle` + Javadoc infra → APROBADO → 3) evento migrado de costura in-process al camino estándar de `fichas` → 4) patrón de rutas + grupo OpenAPI → 5) `FullyQualifiedAnnotationBeanNameGenerator` (fix de arranque) → 6) seed fuera de Flyway → 7) seed eliminado del repo. Gate final verde: tests + 3× `jacocoTestCoverageVerification` (90.0/98.6/100 %) + `checkstyleMain`/`checkstyleTest` + `assemble`. |
| Reporte | @validator-report | ✅ Persistido | 2026-08-28 | Ronda 7 (final). `.workspace/validator/validator-HU-081.md`. |

---

## Datos para la entrega

**Mensaje:** `feat(solicitudes): enviar solicitud de novedad para el coordinador y bootstrapping del contexto`

**Cuerpo:**
- Bootstrapping del contexto `solicitudes`: 4 módulos Gradle, base `solicitudes`, `SolicitudesDataSourceConfig`, **2 migraciones** (esquema base + catálogos `tipo_solicitud`/`estado_respuesta`; `event_publication` outbox), `SolicitudesUsuariosQueueConfig`, `rutas-solicitudes.yml`, grupo Swagger `09-solicitudes`. Sin archivo de datos demo (documentado en el plan §11.2).
- Caso de uso HU-081 en 3 capas: `SolicitudDomain` + objeto de acción `EnvioSolicitudNovedadCoordinadorDomain` + `EnviarSolicitudNovedadCoordinadorMapper` (P10); `Validator` puro + 3 `Rule`s + 4 `Finder`s; endpoint `POST /api/solicitudes/novedad-coordinador` (`solicitudes:solicitud:create`).
- Evento `SolicitudNovedadCoordinadorEnviadaEvent` por el camino estándar: `EventPublisher` → outbox `event_publication` en la tx del interactor → externalización AMQP a `arquisoft.events` (routing key `solicitudes.solicitud.novedad_coordinador_enviada`). Solo lado publicación (sin consumidor, sin `notificaciones`, sin `Queue`/`Binding`).
- Réplicas locales `usuario`/`remitente`/`destinatario` con get-or-create idempotente; `UsuarioCreadoConsumer` inerte hasta el contrato enriquecido de `usuarios` (P1 / §4.4).
- `ArquisoftApplication`: `nameGenerator = FullyQualifiedAnnotationBeanNameGenerator` (colisión de nombres de bean simples entre contextos).
- `shared:util`: `UtilFecha.generarFechaHoraActual()` + `UtilFecha.FECHA_HORA_VACIA` (+ `UtilFechaTest`).
- `shared:message`: `SolicitudesCodes`/`Fields`/`Limits`/`ApiMessages`, `SolicitudKey`/`TipoSolicitudKey`/`UsuarioReplicaKey`, `catalogo/solicitudes.properties`, registro en `ClavesCatalogo`/`ContextosCatalogo`.
- Config raíz: `OpenApiConfig` (grupo `09-solicitudes`), `application.yml` (`config.import` de `rutas-solicitudes.yml`).

**Rama:** `feature/HU-081-enviar-solicitud-novedad-coordinador`

**Archivos a incluir:** todo `solicitudes/` (sin `build/`; `db/migration/solicitudes/` con 2 archivos, `rutas-solicitudes.yml`), `settings.gradle`, `init-db.sql`, `docker-compose.yml`, `.env.example`, `src/main/java/com/arquisoft/ArquisoftApplication.java`, `src/main/java/com/arquisoft/config/OpenApiConfig.java`, `src/main/resources/application.yml`, `src/main/resources/application-prod.yml`, `catalogo/solicitudes.properties`, `catalogo/cargar.sh`, `catalogo/podar.sh`, `shared/util/.../UtilFecha.java`, `shared/util/src/test/.../UtilFechaTest.java`, `shared/message/.../constant/SolicitudesCodes.java`, `SolicitudesFields.java`, `SolicitudesLimits.java`, `annotation/SolicitudesApiMessages.java`, `key/solicitudes/*`, `ClavesCatalogo.java`, `ContextosCatalogo.java` + `.workspace/h-plan/PLAN-HU-081.md` + `.workspace/validator/validator-HU-081.md`

**Nota de despliegue (para @commit — O-2):** en cualquier entorno que ya haya aplicado `V20260827144742__seed_demo_solicitudes.sql` (migración de seed borrada en ronda 6), borrar esa fila de `flyway_schema_history` y las filas demo antes de desplegar esta rama, o Flyway abortará el arranque con "applied migration not resolved". El servidor de pruebas `172.16.1.10` ya fue limpiado.

**Endpoints documentados:** Sí — `POST /api/solicitudes/novedad-coordinador`, grupo Swagger `09-solicitudes`, con `@Tag`/`@Operation`/`@ApiResponses`/`@SecurityRequirement`.
