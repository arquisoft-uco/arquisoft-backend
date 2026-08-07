# Refactor de la línea base técnica — recomendaciones del asesor

Registro de las recomendaciones dadas por el asesor Wider Farid Sánchez en la asesoría
técnica sobre el contexto `fichas` (línea base del producto), qué se hizo con cada una y
qué queda pendiente. El objetivo del asesor: dejar la línea base aprobada para replicarla
al resto de contextos sin arrastrar deuda técnica.

## Recomendaciones atendidas

| # | Recomendación | Implementación |
|---|---------------|----------------|
| 1 | Códigos de respuesta y textos Swagger quemados en los controladores | `ApiCodes` (`shared:web`) y `FichasApiDocs` (`shared:message`). Los tags quedaron unificados: antes convivían "Fichas Perfil", "Fichas de Perfil" y "Fichas" para el mismo recurso |
| 2 | Authorities quemadas en `@PreAuthorize` | `FichasAuthorities`: authority cruda para los tests y expresión SpEL precompuesta para la anotación. Cambiar el nombre de un permiso ahora se hace en un solo lugar |
| 3 | Solución acoplada a SLF4J | Puerto `AppLogger` + `Slf4jAppLogger` (estrategia por defecto) en `shared:logger`; `fichas` migrado completo |
| 4 | Trazabilidad punta a punta | `TraceIdFilter` acepta `X-Correlation-Id` y `traceparent` entrantes, devuelve el id en la respuesta y `ErrorResponseDTO` lo expone como `traceId` |
| 5 | Mensajes y longitudes de validación quemados en los DTOs | `message =` y `max =` referencian constantes de `FichasMessages`; se eliminó la duplicación entre la anotación y el catálogo |
| 6 | UUID recibido como `UUID` produce error genérico de Jackson | Los identificadores del body viajan como `String` con `@UuidValido`; el error indica el campo y el formato esperado, y se acumula con el resto en `fieldErrors[]`. `GlobalAppExceptionHandler` además identifica el campo ofensor cuando Jackson sí falla |
| 7 | Nombres de columna en el contrato (`asesorFichaId`) | Contrato objetual: `asesorFicha`, `estudiantes`, `evaluacionFichaPerfil`, `estadoEvaluacion` |
| 8 | Comentarios que documentan el código (`// POL-03: validar...`) | Eliminados; las validaciones son métodos o validators con nombre expresivo |
| 9 | Validaciones inline que engordan el use case | `FichaPerfilValidator`, `EstudiantesFichaValidator`, `ItemFichaPerfilValidator`, `EvaluacionFichaPerfilValidator`, `EstadoEvaluacionFichaValidator`. La detección de duplicados, que estaba implementada dos veces de forma distinta, quedó en `UtilCollection.firstDuplicate` |
| 10 | La transacción no se maneja desde el use case | Diez interactores (`{Accion}{Entidad}Interactor`) implementan el `InputPort` y delimitan la transacción. Corrige de paso un defecto real: `ModificarItemFichaPerfilUseCase` persistía sin transacción |
| 11 | Orden de validaciones | Integridad → existencia/unicidad → negocio. En `RegistrarFichaPerfil` los duplicados de la lista se validaban **después** de guardar la ficha y su estado inicial |
| 12 | Puertos con parámetros sueltos | `PropietarioFichaCriteria` y `PropietarioEvaluacionCriteria` reemplazan las firmas de dos `UUID` adyacentes, donde invertir los argumentos compilaba sin error |
| 13 | Nomenclatura inconsistente de puertos | `existsBy` / `existsPor` / `existePor` unificados en `existePor...` |
| 14 | Errores genéricos del handler | Literales movidos a `AppMessages.Http`; `traceId` en todas las respuestas de error |
| 15 | Reglas de validación faltantes | `DomainValidator.validUUID/notEmpty/maxSize/sinDuplicados`. Corregido `UtilUUID`, cuyo regex rechazaba UUIDs en mayúsculas pese a ser válidos según RFC 4122 |

## Cambio incompatible en la API

El contrato REST de `fichas` cambió y **los frontends deben actualizarse**:

| Antes | Ahora |
|-------|-------|
| `"asesorFichaId": "<uuid>"` | `"asesorFicha": "<uuid>"` |
| `"estudiantesIds": ["<uuid>"]` | `"estudiantes": ["<uuid>"]` |
| `"evaluacionFichaPerfilId"`, `"estadoEvaluacionId"` | `"evaluacionFichaPerfil"`, `"estadoEvaluacion"` |

Un UUID mal formado ya no devuelve un 400 genérico: devuelve 400 con `fieldErrors[]`
indicando el campo (incluido el índice del elemento en listas, p. ej. `estudiantes[1]`).
Todas las respuestas de error incluyen ahora `traceId`.

## Pendientes

- **OpenTelemetry / Micrometer Tracing.** El asesor pidió "estrategia OpenTelemetry". Se
  implementó la correlación punta a punta con MDC y headers, que cubre el requisito
  funcional (reconstruir la historia de una transacción), y se dejó el bridge OTel como
  spike aparte: introducirlo toca los ocho contextos, obliga a decidir propagación W3C
  frente a correlación propia y colisiona con la clave MDC `idTraza` que ya consumen
  `logback-spring.xml` y Loki. El filtro ya acepta `traceparent`, lo que facilita la
  migración.
- **Operadores `IN` / `NOT IN` en el filtrado.** `FiltroOperador` ya soporta contiene,
  empieza, termina, igual y comparadores; faltan `EN` / `NO_EN`. Añadirlos toca
  `QueryJpaSpecification`, compartido por todos los contextos.
- **Replicar el patrón a los otros siete contextos**: interactor, `AppLogger`,
  authorities centralizadas, `ApiCodes` y orden de validaciones.
- **Tabla espejo de usuarios en `fichas`.** `RegistrarUsuarioUseCase` sigue siendo un stub
  que solo loguea: las tablas réplica (`asesor_ficha`, `estudiante`,
  `representante_comite_curriculum`) contra las que se valida existencia no las alimenta
  ningún consumidor de eventos todavía.
- **Autoservicio de Keycloak** (recuperación de contraseña sin intervención del
  administrador): decisión funcional planteada por el asesor, fuera del alcance de este
  refactor.
