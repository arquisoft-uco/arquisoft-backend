# Consultas síncronas entre contextos

Referencia de `arquisoft-arquitectura`. Se carga cuando un comando necesita verificar, al momento de
escribir, un dato que es propiedad de otro contexto.

## Consultas síncronas entre contextos

El default para "el contexto A necesita algo del contexto B" es un evento: B publica, A replica
local, A lee su tabla espejo (ver `eventos.md` → *Replicación entre contextos*). Se recurre a una **consulta HTTP síncrona** solo si
se cumplen las **tres**:

1. El dato es **precondición de una escritura** en A y debe ser correcto al instante de escribir, no
   eventualmente — una réplica desactualizada dejaría pasar un comando inválido.
2. A **no necesita el dato para nada más**, así que mantener una réplica (más su backfill y su
   consumer) es puro lastre.
3. B ya **expone una consulta** que responde.

Primer caso: el asesor/coordinador asignado al estudiante, verificado cuando `solicitudes` crea una
solicitud de novedad / cambio de asesor (`AsignacionProyectoOutputPort` → `proyectos`).

Forma, espejando cualquier otro puerto secundario:

| Pieza | Dónde | Regla |
|---|---|---|
| Puerto | `application/{feature}/command/secondaryport/{Concepto}OutputPort` (o su propio paquete fino si no mapea a un agregado, p. ej. `application/asignacionproyecto/command/secondaryport/`) | Devuelve un `boolean`/valor plano. **La `Rule` sigue decidiendo**, el puerto solo responde |
| `Finder` | `application/{feature}/command/finder/` | Lo consume igual que un chequeo contra réplica |
| Adaptador | `infrastructure/{feature}/command/secondaryadapter/webclient/{Concepto}OutputAdapter`, `@Component` | Habla por **`shared:web-client`** — nunca `RestClient`/`WebClient` inline, nunca un cliente generado que importe B. Reenvía el bearer del llamante. Fallo de transporte → `InfrastructureException` (503): un peer caído falla la petición, no salta el chequeo |

**`shared:web-client` no existe todavía** (lo trae una HT aparte). Hasta entonces, un contexto que
necesita esto embarca el puerto + la `Rule` + el `Finder` **cableados y activos**, con el adaptador
un **stub** documentado (devuelve el valor permisivo, loguea `warn`) — registrado en
`CLAUDE.md` → *Desviaciones conocidas*. Activarlo es cambiar un solo archivo (el adaptador).

Esto **no** reemplaza a la réplica por eventos para "A necesita el dato de B para algo más que un
chequeo puntual de escritura" — ahí sigue siendo tabla espejo.

