<!--
Plantilla completa del reporte de validación. La produce @4a-validator-analyze (como mensaje) y la
persiste @4b-validator-report (como archivo, tal cual, quitando cualquier línea conversacional previa
a la primera almohadilla).

A diferencia de PLAN.md, aquí no hay parte condicional: el reporte lleva siempre las mismas
secciones. Una sección sin hallazgos se deja con "Ninguno", nunca se borra — su ausencia no se
distingue de un olvido.

Destino: .workspace/validator/validator-{HU|HT}-{ID}.md
-->

# Reporte de Validación — {HU|HT}-{ID}

## Metadata
- **Bounded Context:** {contexto}
- **Fecha:** {yyyy-MM-dd} · **Rama propuesta:** `feature/{HU|HT}-{ID}-{descripcion}`
- **Plan validado:** `.workspace/h-plan/PLAN-{HU|HT}-{ID}.md`

## Score

| Nivel | Checks | Pasados | Fallados | Score |
|---|---|---|---|---|
| 1 — Completitud | | | | |
| 2 — Convenciones DDD + Arquisoft | | | | |
| 3 — Compilación | | | | |
| 4 — Tests | | | | ⏳ N/A si no se ejecutaron |
| **Total** | | | | **XX/100** |

**Bloqueantes:** X · **Menores:** X

## Estado Final

> ✅ APROBADO — sin bloqueantes. / ⛔ RECHAZADO — hay X bloqueantes.

Un solo bloqueante = RECHAZADO, sin importar el score.

## Errores Bloqueantes

### [Nivel X.Y] — {título}
- **Archivo:** `ruta/relativa/desde/raiz/del/repo`
- **Problema:** {qué está mal}
- **Referencia:** {check violado}

## Errores Menores

{mismo formato que los bloqueantes, o "Ninguno"}

## Tests

{Si ✅ Completado: total de tests, presupuesto vs estimación, anti-patrones detectados
 (o "ninguno"), tests que afirman 500 (o "ninguno"), coherencia con Tipo de UC.}
{Si ⏳ Pendiente: "Tests no ejecutados — invoca @3-tester y repite el análisis."}

## Datos para la entrega

> Esta sección es el insumo de `@4c-commit`: de aquí saca el mensaje, la rama y los archivos, y del
> Score/Tests/bloqueantes de arriba saca la evidencia para marcar el checklist del PR. Un dato que
> no dejes aquí es una casilla que ese agente **no** podrá marcar.

**Mensaje:** {tipo}({contexto}): {descripción corta}
**Cuerpo:** {bullets: qué se implementó, capas afectadas, eventos emitidos, migración}
**Rama:** `feature/{HU|HT}-{ID}-{descripcion}`
**Archivos a incluir:** {solo código, tests, migraciones y recursos — el plan y este reporte NO
van al repositorio de backend, los publica `@4c-commit` en `arquisoft-docs`}
**Endpoints documentados:** {Sí / N/A — la HU no expone endpoints}

## Próximos pasos

{Si APROBADO: "Invoca @4b-validator-report genera el reporte de {HU|HT}-{ID} y pega este reporte
completo."} {Si RECHAZADO: "El implementador corrige los bloqueantes y se repite el análisis."}
