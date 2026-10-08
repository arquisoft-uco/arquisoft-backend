<!--
Cabecera invariable de un plan. La usan @1-planificador (al generar) y @4a-validator-analyze (al
leer). Copia desde "# PLAN" hasta la tabla de la sección 3 y sustituye los {marcadores}. Este
comentario NO se copia al plan.

Las secciones 4 a 14 son condicionales y su forma la decide @1-planificador (FASE 4).

Guía para llenar la sección 3 (no se copia):
- Invariante LOCAL (formato, longitud, obligatoriedad) → dentro del {Entidad}Domain, acumulado en
  ValidationResult → 422 con fieldErrors[], sin excepción propia. "Dónde" = Domain, sin Finder.
- Restricción de CONJUNTO (unicidad, existencia, propiedad) → {Concepto}Rule con su record de
  entrada, orquestada por el {Accion}{Entidad}Validator sobre lo que trajeron los Finders → 422 con
  su DomainException. Nunca if/throw en el use case; no hay 403 para "no eres el dueño".
- Un corte que no es error de negocio (idempotencia de un consumidor AMQP) NO es Rule: es un Finder
  consultado directo que devuelve la variante de su sellada. Como Rule lanzaría y mandaría una
  reentrega normal a la DLQ.
- Sin restricciones de conjunto no hay Validator.
- En una consulta, la política de acceso (existencia, pertenencia, estado) va en esta tabla con su
  {X}QueryFinder y el criterio de la HU del que sale; sin ninguna, escribe bajo la tabla
  "Política de acceso: ninguna".

Destino: .workspace/h-plan/PLAN-{HU|HT}-{ID}.md
-->

# PLAN: {Título}

## Metadata
- **ID Historia:** {HU|HT}-{ID}
- **Bounded Context:** {contexto}
- **Tipo de Use Case:** {Escritura/Consulta/Mixto}
- **Módulos Gradle afectados:** `{contexto}:domain`, `:application`, `:infrastructure`
- **Autor:** {Nombre} <{correo}>
- **Fecha de plan:** {yyyy-MM-dd}
- **Rama sugerida:** `feature/{HU|HT}-{ID}-{descripcion_snake_case}`
- **Fuentes consultadas:** {archivos de arquisoft-docs, separados por coma}
- **Observaciones del usuario:** {una línea por observación, o "Ninguna"}

## 1. Resumen Funcional

{1-2 oraciones: qué hace la historia, en lenguaje de negocio.}
**Fuera de alcance:** {lista corta separada por punto y coma, o "Nada relevante"}

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|---|---|

## 3. Reglas de Negocio

| # | Regla | Dónde (Domain / Rule) | Finder | Excepción → HTTP |
|---|---|---|---|---|
