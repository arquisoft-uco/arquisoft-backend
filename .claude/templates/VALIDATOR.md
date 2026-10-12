<!--
Reporte de validación: lo produce @4a-validator-analyze y lo persiste @4b-validator-report tal cual
en .workspace/validator/validator-{HU|HT}-{ID}.md. Todas las secciones son fijas: una sin hallazgos
dice "Ninguno", nunca se borra.
-->

# Reporte de Validación — {HU|HT}-{ID}

## Metadata
- **Bounded Context:** {contexto}
- **Autor:** {Nombre} <{correo}>   <!-- se copia del campo Autor del plan; si no lo trae, git config -->
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

<!-- "Checks" es la cifra exacta de checks aplicados, sin los N/A — nunca un aproximado ("~60"):
un número que no se puede recontar no demuestra que se recorrió la lista. -->

**Bloqueantes:** X · **Menores:** X
**Compilación:** {cada comando de la FASE 3, literal, con su resultado}

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

> Insumo de `@4c-commit`: mensaje, rama y archivos salen de aquí; la evidencia del checklist del PR,
> del Score/Compilación/Tests de arriba. Un dato ausente es una casilla que no podrá marcar.

**Mensaje:** {tipo}({contexto}): {descripción corta}
**Cuerpo:** {bullets: qué se implementó, capas afectadas, eventos emitidos, migración}
**Rama:** `feature/{HU|HT}-{ID}-{descripcion}`
**Archivos a incluir:** {solo código, tests, migraciones y recursos — el plan y este reporte NO
van al repositorio de backend, los publica `@4c-commit` en `arquisoft-docs`}
```
M  ruta/modificada.java
?? ruta/nueva.java
D  ruta/eliminada.java
```
<!-- una ruta por línea, prefijada con su estado de `git status -s -uall`, sin anotaciones libres -->

**Endpoints documentados:** {Sí / N/A — la HU no expone endpoints}

## Próximos pasos

{Si APROBADO: "Invoca @4b-validator-report genera el reporte de {HU|HT}-{ID} y pega este reporte
completo."} {Si RECHAZADO: "El implementador corrige los bloqueantes y se repite el análisis."}
