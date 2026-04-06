# .opencode — Arquisoft Backend

Configuración de agentes, skills y documentación de setup para el proyecto
Arquisoft Backend en [OpenCode](https://opencode.ai).

IMPORTANTE: Este es un Readme de ayuda, para entender como funcionan los agentes desarrollados, no deberían ser usados para dar contexto a los agentes.

---

## Estructura

```
.opencode/
├── agents/                        # Subagentes del flujo de desarrollo
│   ├── 01-plan-agent.md           # Planificador de Historias de Usuario
│   ├── 02-dev-agent.md            # Implementador de código
│   ├── 03-test-agent.md           # Generador de pruebas unitarias
│   └── 04-validator-agent.md      # Validador y gestor de commits
├── skills/                        # Skills reutilizables por los agentes
│   ├── gh-docs-reader/
│   │   └── SKILL.md               # Lectura del repo arquisoft-docs via GitHub CLI
│   └── context7-stack/
│       └── SKILL.md               # IDs de librerías Context7 del stack
└── docs/                          # Guías de configuración
    ├── gh-auth-setup.md           # Autenticación GitHub CLI con arquisoft-uco
    └── mcp-context7-setup.md      # Configuración del MCP Context7 en opencode
```

---

## Requisitos Previos

Antes de usar los agentes, configura los dos servicios externos que utilizan.

### 1. GitHub CLI — acceso a `arquisoft-docs`

Los agentes consultan el repositorio privado de documentación `arquisoft-uco/arquisoft-docs`
para leer historias de usuario, event storming y modelos de dominio.

```bash
# Verificar si ya tienes acceso
gh auth status
gh api repos/arquisoft-uco/arquisoft-docs --jq '.full_name'
```

Si no tienes acceso, sigue la guía completa:
👉 [`.opencode/docs/gh-auth-setup.md`](./docs/gh-auth-setup.md)

---

### 2. Context7 MCP — documentación actualizada de librerías

Los agentes `02-dev-agent` y `03-test-agent` usan Context7 para consultar
la documentación actualizada de Spring Boot, JPA, RabbitMQ, JUnit 5, etc.

Agrega esto a tu `opencode.json` (proyecto o global `~/.config/opencode/opencode.json`):

```json
{
  "mcp": {
    "context7": {
      "type": "remote",
      "url": "https://mcp.context7.com/mcp",
      "headers": {
        "CONTEXT7_API_KEY": "${CONTEXT7_API_KEY}"
      },
      "enabled": true
    }
  }
}
```

Si no tienes API key (es gratuita), omite el bloque `headers`.
Guía completa: 👉 [`.opencode/docs/mcp-context7-setup.md`](./docs/mcp-context7-setup.md)

---

## Cómo Invocar los Agentes

Todos los agentes son `subagent` con `hidden: true` — no aparecen en el Tab
ni en el autocompletado `@`, pero se invocan directamente por nombre desde
el chat de opencode estando en el agente **Build**.

```
@planificador   →  01-plan-agent.md
@implementador  →  02-dev-agent.md
@tester         →  03-test-agent.md
@validator      →  04-validator-agent.md
```

> **Nota:** si acabas de copiar los archivos, reinicia opencode desde la raíz
> del proyecto para que detecte los nuevos agentes.

---

## Flujo Completo de una Historia de Usuario

```
┌─────────────────────────────────────────────────────────┐
│  PASO 1 — Planificación                                  │
│  @planificador                                           │
│                                                          │
│  El agente:                                              │
│  · Consulta arquisoft-docs via gh-docs-reader skill      │
│  · Lee event storming y modelo de dominio del contexto   │
│  · Hace preguntas de clarificación                       │
│  · Genera /.workspace/h-plan/PLAN-{HU|HT}-{ID}.md    │
└──────────────────────────┬──────────────────────────────┘
                           │ usuario aprueba el plan
                           ▼
┌─────────────────────────────────────────────────────────┐
│  PASO 2 — Implementación                                 │
│  @implementador                                          │
│                                                          │
│  El agente:                                              │
│  · Lee PLAN-{HU|HT}-{ID}.md como contrato          │
│  · Consulta Context7 antes de generar cada archivo       │
│  · Implementa archivo por archivo (aprobación por cada   │
│    uno), respetando orden: domain → application →        │
│    infrastructure                                        │
│  · Compila con ./gradlew tras cada capa                  │
│  · Al finalizar PREGUNTA ACTIVAMENTE:                    │
│    "¿Continúas con @tester (A) o @validator (B)?"        │
└──────────────────────────┬──────────────────────────────┘
                           │
              ┌────────────┴────────────┐
              │                         │
              ▼ (recomendado)           ▼ (opcional)
┌─────────────────────────┐  ┌─────────────────────────────┐
│  PASO 3 — Tests          │  │  PASO 4 — Validación         │
│  @tester                 │  │  @validator                  │
│                          │  │  (sin tests — los deja como  │
│  El agente:              │  │   pendientes en el reporte)  │
│  · Genera tests JUnit 5  │  └─────────────────────────────┘
│    + Mockito por capa     │
│  · Aprobación por capa   │
│    (domain → application │
│    → infrastructure)     │
│  · Ejecuta ./gradlew test│
│  · Verifica cobertura    │
│    mínima 75% (JaCoCo)   │
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────┐
│  PASO 4 — Validación y Commit                            │
│  @validator                                              │
│                                                          │
│  MODO A — Validación automática:                         │
│  · Lee PLAN + código + tests (si existen)               │
│  · Valida 4 niveles:                                     │
│    1. Completitud del plan (archivos, contratos,         │
│       criterios de aceptación de la HU/HT)              │
│    2. Convenciones Arquisoft (arquitectura, naming)      │
│    3. Compilación (./gradlew build -x test)              │
│    4. Tests (si se ejecutó @tester, si no → pendiente)  │
│  · Genera reporte en /.workspace/validator/              │
│  · Propone commit con bloque copiar/pegar incluido       │
│                                                          │
│  MODO B — Ejecución del commit (invocar manualmente):    │
│  · "Ejecuta el commit del reporte de {HU|HT}-{ID}"      │
│  · Verifica estado APROBADO en el reporte                │
│  · Verifica rama — crea con checkout -b si no existe     │
│  · Pide confirmación → ejecuta git commit                │
│  · Actualiza el reporte con el hash del commit           │
└─────────────────────────────────────────────────────────┘
```

---

## Archivos Generados en `.workspace/`

El flujo crea archivos de trabajo en la carpeta `.workspace/` en la raíz del proyecto.
Esta carpeta **no se commitea** — agrégala al `.gitignore`:

```
# .gitignore
.workspace/
```

```
.workspace/
├── h-plan/
│   └── PLAN-{HU|HT}-{ID}.md  # Plan generado por @planificador (HU o HT)
└── validator/
    └── validator-{HU|HT}-{ID}.md  # Reporte generado por @validator
                                 # (incluye commit propuesto y hash tras ejecutarse)
```

---

## Referencia Rápida de Comandos

### Invocar cada agente

```
# 1. Planificar una HU o HT (pegar el ID o el texto en el chat)
@planificador planifica la HU160
@planificador planifica la HT-007

# 2. Implementar desde un plan aprobado
@implementador implementa el PLAN-HU-160
@implementador implementa el PLAN-HT-007

# 3. Generar tests
@tester genera los tests para HU-160
@tester genera los tests para HT-007

# 4a. Validar implementación (el agente pregunta qué sigue al terminar @implementador)
@validator valida la implementacion de HU-160
@validator valida la implementacion de HT-007

# 4b. Ejecutar el commit una vez aprobado el reporte
#     El agente verifica la rama, hace checkout -b si no existe, y ejecuta el commit
@validator ejecuta el commit del reporte de HU-160

# Alternativa: copiar y pegar los comandos que el reporte incluye automáticamente
# git checkout -b feature/HU-160-descripcion
# git add {archivos} && git commit -m "feat(contexto): descripcion"
```

### Comandos Gradle del proyecto

```bash
./gradlew build                           # Compilar + tests
./gradlew build -x test                   # Compilar sin tests
./gradlew :{contexto}:compileJava         # Compilar un módulo
./gradlew :{contexto}:{capa}:test         # Tests de una capa
./gradlew jacocoTestReport                # Reporte de cobertura (mín. 75%)
./gradlew projects                        # Listar todos los módulos
```

### Verificar estado de los servicios externos

```bash
# GitHub CLI
gh auth status
gh api repos/arquisoft-uco/arquisoft-docs --jq '.full_name'

# Context7 MCP (dentro de opencode)
/mcp
```

---

## Skills Disponibles

Los skills son invocados automáticamente por los agentes — no necesitas
invocarlos manualmente.

| Skill | Usado por | Propósito |
|-------|-----------|-----------|
| `gh-docs-reader` | `@planificador` | Lee `arquisoft-docs` via GitHub CLI |
| `context7-stack` | `@implementador`, `@tester` | IDs de librerías Context7 del stack |

---

## Solución de Problemas

**Los agentes no aparecen con `@`**
Los agentes tienen `hidden: true` — no aparecen en el autocompletado pero
sí responden si los invocas directamente escribiendo `@planificador`, etc.
Si no responden, verifica que los archivos estén en `.opencode/agents/` y
reinicia opencode desde la raíz del proyecto.

**Error al consultar `arquisoft-docs`**
Ejecuta `gh auth status` en la terminal. Si el token no tiene acceso a la
organización sigue la guía en `.opencode/docs/gh-auth-setup.md`.

**Context7 no aparece en `/mcp`**
Verifica que el `opencode.json` tiene el bloque `mcp.context7` bien formado:
```bash
cat opencode.json | jq .mcp
```

**El agente no encuentra el plan**
Verifica que el archivo existe en `/.workspace/h-plan/PLAN-{HU|HT}-{ID}.md`
y que el ID de la HU que le indicas coincide con el nombre del archivo.

**Compilación falla en el agente implementador**
El agente reportará el error exacto y esperará tu aprobación antes de
aplicar cualquier corrección. Lee el mensaje, decide la corrección y
confirma para continuar.