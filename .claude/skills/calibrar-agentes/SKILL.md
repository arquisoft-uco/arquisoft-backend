---
name: calibrar-agentes
description: Guía meta para agregar, ajustar o podar instrucciones en los subagentes de `.claude/agents/` (1-planificador, 2-implementador, 3-tester, 4a/4b/4c) y en las skills `arquisoft-*`, calibrándolas al punto justo entre demasiado específico y demasiado vago. Úsala en la sesión principal siempre que el usuario pida agregar un detalle, una regla o un ajuste a un agente o a una skill, cuente que un agente hizo algo mal, o pregunte dónde debería vivir una instrucción, aunque no diga "calibrar". No es para subagentes: ningún agente del pipeline ni skill de negocio la carga.
---

# Skill: calibrar-agentes

## Quién la usa

Solo la sesión principal, trabajando con el mantenedor de los agentes. **Si eres un subagente del
pipeline (1-planificador … 4c-commit), no sigas leyendo:** tu contrato es tu propio archivo más las
skills `arquisoft-*`. Esta skill sirve para *reescribir* esos contratos, y un agente que la aplicara a
mitad de una HU acabaría editando sus propias instrucciones o las de otro en vez de entregar su fase.

Por la misma razón ningún agente ni skill `arquisoft-*` la menciona. Si encuentras una referencia a
`calibrar-agentes` fuera de esta carpeta, elimínala.

## La escala

Una instrucción de agente puede fallar por los dos extremos, y cada uno falla de una forma distinta.

| | Cómo se ve aquí | Por qué falla |
|---|---|---|
| **Demasiado específico** | "Si el contexto es `solicitudes` y la HU crea una novedad, entonces…"; listas exhaustivas de casos; árboles de `si A → haz X, si B → haz Y`; una regla escrita desde un incidente que nombra las clases de ese incidente | El modelo la aplica al pie de la letra: lo que no está en la lista lo lee como permitido. Cada incidente añade una rama nueva, las ramas acaban contradiciéndose y el agente crece sin mejorar |
| **Punto justo** | La regla en una frase, su porqué, la señal que indica cuándo aplica y un archivo real del repo como referencia | El modelo entiende la *intención* y la extiende sola al caso parecido que nadie escribió |
| **Demasiado vago** | "Sigue la arquitectura del proyecto", "escribe código de calidad", "valida bien" | El modelo rellena el hueco con lo que sabe por defecto (el Spring genérico de su entrenamiento), que es justo lo que este repo contradice |

La prueba práctica: **¿esta instrucción habría evitado también el siguiente caso parecido, pero no
idéntico?** Si solo cubre el incidente que la motivó, es demasiado específica. Si no habría cambiado
nada en el incidente, es demasiado vaga.

## Procedimiento cuando el usuario pide un ajuste

### 1. Entiende el síntoma antes que la regla

Averigua qué hizo el agente y qué se esperaba. El usuario suele traer la regla ya formulada ("agrega
que el implementador no haga X"), pero el incidente es lo que indica a qué altura escribirla y dónde.
Si el incidente no está en la conversación, pídelo o búscalo en el plan, el reporte del validator o
el diff.

### 2. Decide dónde vive, que es el paso que más se equivoca

| El detalle es… | Va en… | El agente… |
|---|---|---|
| Una convención de código: dónde va una clase, cómo se nombra, qué patrón seguir | `arquisoft-arquitectura` o `arquisoft-estandares` (o su `references/` si solo aplica a veces) | la cita, no la copia |
| Un comportamiento del agente: cuándo preguntar, cuándo detenerse, qué leer, qué entregar | el agente, en la FASE donde actúa | — |
| Un contrato entre fases: qué pasa del plan al implementador o del validator al commit | `.claude/templates/PLAN.md` / `VALIDATOR.md`, más el agente que lo produce y el que lo consume | — |
| Algo que el validator debe detectar | un check en el Nivel 2.x de `4a-validator-analyze`, además de su fuente | — |
| Un hecho del proyecto que va a cambiar: un stub, una HU a medias, una decisión abierta | `CLAUDE.md` → *Desviaciones conocidas*, o memoria | no lo lleva |

Una convención escrita dentro de un agente es una segunda copia de la skill, y dos copias acaban
divergiendo. Cuando divergen, el agente elige una sin decirlo.

### 3. Busca antes de escribir

Haz `grep` del concepto en `.claude/agents/` y `.claude/skills/`. Si la regla ya existe, el trabajo es
afinarla o moverla, no añadir una segunda versión.

Que ya exista es además una pista de diagnóstico. Ejemplo real: "que el implementador no use
`saveAndFlush`" ya está en `arquisoft-arquitectura`, en la FASE 3 de `2-implementador` y como ❌ en el
Nivel 2.1 de `4a-validator-analyze`. Una cuarta copia no cambiaría nada. Lo útil es preguntarse
**por qué no se aplicó**: ¿el agente no cargó la skill?, ¿la regla está enterrada en un bloque que no
lee en esa fase?, ¿el plan le indicó lo contrario? La respuesta a eso es el cambio que hay que hacer.

### 4. Redáctala al punto justo

- **La regla, en una frase afirmativa.** Qué hacer, no una lista de lo que no hacer.
- **El porqué, en una o dos frases.** Qué se rompe y cómo, sobre todo si falla en silencio (compila,
  arranca y está mal). Con el porqué, el modelo generaliza; sin él, solo obedece la letra.
- **La señal que indica que aplica.** Si la regla tiene que dispararse siempre, átala a algo que el
  agente pueda comprobar, no a su criterio. Ejemplo ya en uso: "si el plan tiene la sección 10, lee
  `references/eventos.md`". La sección existe o no existe.
- **Un archivo real como referencia,** no un snippet inventado. Las skills del proyecto ya siguen esta
  norma y los agentes también deben seguirla.
- **Generaliza el incidente:** nombra la *clase* de error, no las clases Java del caso.

Los subagentes arrancan en frío y **no ven esta conversación**. "Como hablamos", "lo del otro día" o
"el caso de la HU anterior" no significan nada para ellos: cada instrucción tiene que entenderse sola.

### 5. Ubícala donde se actúa

Pon la regla en la FASE donde el agente toma esa decisión. *Reglas invariantes* es para lo que aplica
en todas las fases; si todo acaba ahí, el agente lo lee antes de tener contexto para entenderlo.

### 6. Controla el tamaño

Cada línea compite por la atención del agente con todas las demás. Cuando agregues algo, busca qué
sobra: una regla que ya cubre la skill, un ejemplo repetido, un matiz que ya no aplica. Mala señal: el
agente crece en cada ajuste y nunca sale nada.

### 7. Mantén la coherencia entre agentes

- Una regla compartida por `2-implementador`, `3-tester` y `4a-validator-analyze` va en los tres con
  la **misma redacción**, como la regla de eventos de la FASE 0.
- Las parejas productor/consumidor se cambian juntas: `1-planificador` ↔ `2-implementador` (a través
  del plan) y `4a` → `4b` → `4c` (a través del reporte). Si cambias lo que produce uno, revisa lo que
  lee el otro.

### 8. Proponlo como diff y verifícalo

Presenta el cambio como bloque actual → bloque nuevo, con el archivo y una frase de razón. Si el
usuario ya pidió aplicarlo, aplícalo y muéstrale el diff.

Después de editar:

- Haz `grep` de las secciones que otros archivos citan por nombre (`arquisoft-arquitectura → *X*`) y
  confirma que siguen existiendo donde apuntan.
- Revisa el `git diff` completo. Una edición hecha con script puede dejar variables sin expandir o
  caracteres sueltos.
- Respeta el final de línea del archivo (`file <ruta>`). Algunos usan CRLF, y reescribirlos en LF
  hace que git marque el archivo entero como cambiado.

## Énfasis y tono

Explica el porqué en vez de subir el volumen. Si todo va en **negrita** o con NUNCA, nada destaca.
Reserva el énfasis para las reglas cuyo fallo es silencioso, que son las que el agente no puede
descubrir compilando. Escribe en español y en el mismo registro que el resto del agente.

## Frontmatter de un agente

- `description` decide cuándo la sesión principal invoca al agente. Describe **cuándo usarlo**, no
  cómo trabaja por dentro.
- `model` se ajusta por agente según la dificultad de su fase. No tiene que ser el mismo en todos.
- Omitir `tools` significa que el agente hereda todas las herramientas. Al restringirlas la lista es
  exhaustiva: un agente cuya FASE 0 carga skills necesita `Skill` en la lista, o trabajará sin sus
  convenciones sin avisar.

## Antipatrones

| Antipatrón | En su lugar |
|---|---|
| Un parche por incidente que nombra sus clases | La clase de error, con su porqué |
| Una lista exhaustiva de casos | El principio más la señal que indica cuándo aplica |
| Copiar una convención de la skill dentro del agente | Citar la sección de la skill |
| Una regla sin porqué | Una o dos frases con lo que se rompe |
| Contar con que el agente recuerde la conversación | Instrucciones que se entienden solas |
| Añadir sin quitar nada | Revisar qué sobra en la misma FASE |
| Añadir una cuarta copia de una regla que ya existe | Diagnosticar por qué no se aplicó |
