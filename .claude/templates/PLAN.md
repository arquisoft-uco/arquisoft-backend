<!--
Plantilla de la cabecera invariable de un plan. La usan @planificador (al generar) y
@validator-analyze (al leer). Copia este bloque tal cual y sustituye los {marcadores}.

Lo que sigue a la sección 3 NO vive aquí: las secciones 4 a 14 son condicionales — dependen de si
la HU escribe o consulta, de si emite eventos, de si toca la base de datos — y su forma la decide
@planificador según sus preguntas. Esta plantilla fija lo que no cambia nunca: el título, la
metadata y las tres secciones que toda HU/HT tiene, escriba lo que escriba.

Destino: .workspace/h-plan/PLAN-{HU|HT}-{ID}.md
-->

# PLAN: {Título}

## Metadata
- **ID Historia:** {HU|HT}-{ID}
- **Bounded Context:** {contexto}
- **Tipo de Use Case:** {Escritura/Consulta/Mixto}
- **Módulos Gradle afectados:** `{contexto}:domain`, `:application`, `:infrastructure`
- **Fecha de plan:** {yyyy-MM-dd}
- **Rama sugerida:** `feature/{HU|HT}-{ID}-{descripcion_snake_case}`
- **Fuentes consultadas:** {archivos de arquisoft-docs}
- **Observaciones del usuario:** {o "Ninguna"}

## 1. Resumen Funcional

{2-4 oraciones: qué hace, qué NO cubre}

## 2. Criterios de Aceptación

| # | Criterio | Resultado esperado |
|---|---|---|

## 3. Reglas de Negocio

> Invariante LOCAL (formato, longitud, obligatoriedad de la propia instancia) → dentro del
> `{Entidad}Domain`, acumulado en `ValidationResult` → 422 con `fieldErrors[]`, sin clase de
> excepción propia. Restricción de CONJUNTO (unicidad, existencia, propiedad) → `{Concepto}Rule`
> de dominio con su record de entrada, orquestada por el `{Accion}{Entidad}Validator` sobre lo que
> los `Finder`s ya trajeron → 422 con su propia `DomainException`. **Nunca `if/throw` en el use
> case, y no hay caso 403 para "no eres el dueño".** Ver skill `arquisoft-estandares`.
>
> Antes de declarar una `Rule`, pregúntate si el caso debe **lanzar**. Si la consulta solo decide si
> vale la pena seguir y su resultado no es un error de negocio — el corte de idempotencia de un
> consumidor AMQP es el caso típico — entonces **no hay `Rule`**: es un `Finder` que el use case
> consulta directo con `if (...) return;`. Declararlo como `Rule` haría que lanzara, mandando el
> mensaje a la DLQ por una reentrega normal del broker. Y si la HU no tiene ninguna restricción de
> conjunto, tampoco hay `Validator`: no planifiques una capa vacía.

| # | Regla | Dónde se valida (Domain / Rule) | Finder que trae el dato | Excepción → HTTP |
|---|---|---|---|---|
