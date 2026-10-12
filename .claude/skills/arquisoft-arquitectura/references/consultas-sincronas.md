# Consultas síncronas entre contextos

Referencia de `arquisoft-arquitectura`. Se carga cuando un comando necesita verificar, al momento de
escribir, un dato que es propiedad de otro contexto.

## Consultas síncronas entre contextos

El default es un evento: B publica, A replica y lee su tabla espejo (`eventos.md` → *Replicación entre
contextos*). Una **consulta HTTP síncrona** solo procede si se cumplen las **tres**:

1. El dato es **precondición de una escritura** en A y debe ser correcto al instante — una réplica
   desactualizada dejaría pasar un comando inválido.
2. A **no necesita el dato para nada más**, así que una réplica (con backfill y consumer) sería lastre.
3. B ya **expone una consulta** que responde.

Hoy no hay ningún caso implementado.

| Pieza | Dónde | Regla |
|---|---|---|
| Puerto | `application/{feature}/command/secondaryport/{Concepto}OutputPort` (o su propio paquete fino si no mapea a un agregado) | Devuelve un `boolean`/valor plano. **La `Rule` sigue decidiendo**, el puerto solo responde |
| `Finder` | `application/{feature}/command/finder/` | Lo consume igual que un chequeo contra réplica |
| Adaptador | `infrastructure/{feature}/command/secondaryadapter/webclient/{Concepto}OutputAdapter`, `@Component` | Habla por **`shared:web-client`** — nunca `RestClient`/`WebClient` inline ni un cliente generado que importe B. Reenvía el bearer del llamante. Fallo de transporte → `InfrastructureException` (503): un peer caído falla la petición, no salta el chequeo |

**`shared:web-client` no existe todavía** (lo trae una HT aparte). Hasta entonces se embarcan puerto,
`Rule` y `Finder` **cableados y activos**, con el adaptador como **stub** documentado (devuelve el valor
permisivo, loguea `warn`) y registrado en `CLAUDE.md` → *Desviaciones conocidas*; activarlo es cambiar
solo el adaptador. Si A necesita el dato para algo más que un chequeo puntual de escritura, sigue
siendo réplica.
