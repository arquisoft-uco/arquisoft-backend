---
name: gh-docs-reader
description:
  Consulta archivos markdown del repositorio privado de documentación de arquisoft-uco (arquisoft-docs) usando el GitHub CLI. Permite leer historias de usuario, event storming, modelos de dominio, funcionalidades críticas y atributos de calidad sin clonar el repositorio. Usar antes de planificar cualquier Historia de Usuario.
license: MIT
compatibility: opencode
metadata:
  org: arquisoft-uco
  repo: arquisoft-docs
  branch: main
---

# Skill: gh-docs-reader

Consulta el repositorio privado `arquisoft-uco/arquisoft-docs` usando el GitHub CLI (`gh`).
Usa este skill en la FASE 0 del agente planificador, antes de hacer cualquier pregunta al usuario.

---

## Prerequisito

Verificar autenticación antes de cualquier consulta:

```bash
gh auth status
```

Si no está autenticado o no tiene acceso a la organización, detente y notifica:

> "El GitHub CLI no está autenticado con acceso a `arquisoft-uco`. Ejecuta `gh auth login`
> y asegúrate de otorgar acceso a la organización. Ver guía en `.opencode/docs/gh-auth-setup.md`."

---

## Variables Base

```
ORG=arquisoft-uco
REPO=arquisoft-docs
BRANCH=main
BASE=repos/${ORG}/${REPO}
```

---

## Comandos Disponibles

### 1. Listar todos los archivos del repositorio

```bash
gh api repos/${ORG}/${REPO}/git/trees/${BRANCH} \
  --jq '.tree[] | select(.type=="blob") | .path'
```

Úsalo para explorar la estructura antes de saber qué leer.

### 2. Leer un archivo específico

```bash
gh api repos/${ORG}/${REPO}/contents/{ruta/al/archivo.md} \
  --jq '.content' | base64 -d
```

Ejemplo — leer historias priorizadas:

```bash
gh api repos/${ORG}/${REPO}/contents/historias_usuario_priorizadas.md \
  --jq '.content' | base64 -d
```

### 3. Buscar archivos por patrón de nombre

```bash
# Buscar archivos de event storming
gh api repos/${ORG}/${REPO}/git/trees/${BRANCH} \
  --jq '.tree[] | select(.path | contains("event_storming")) | .path'

# Buscar archivos de un contexto específico
gh api repos/${ORG}/${REPO}/git/trees/${BRANCH} \
  --jq '.tree[] | select(.path | contains("fichas")) | .path'

# Buscar archivos de modelo de dominio
gh api repos/${ORG}/${REPO}/git/trees/${BRANCH} \
  --jq '.tree[] | select(.path | contains("modelo_dominio")) | .path'
```

### 4. Listar carpeta específica

```bash
gh api repos/${ORG}/${REPO}/contents/{carpeta}/ \
  --jq '.[].name'
```

Ejemplo — listar carpeta de atributos de calidad:

```bash
gh api repos/${ORG}/${REPO}/contents/atributos_calidad/ \
  --jq '.[].name'
```

---

## Mapa de Archivos del Repositorio

Consulta estos archivos según el contexto de la HU que estás planificando:

| Prioridad | Archivo / Carpeta | Cuándo consultarlo |
|-----------|-------------------|--------------------|
| 🔴 Siempre | `historias_usuario_priorizadas.md` | Para validar prioridad, dependencias y sprint de la HU, aquí se encuentra el hu con su id |
| 🔴 Siempre | `funcionalidades_criticas.md` | Para verificar si la HU toca una funcionalidad crítica del sistema |
| 🟠 Contexto | `event_storming/{contexto}.md` | Si la HU produce o consume eventos de dominio |
| 🟠 Contexto | `modelo_dominio/{contexto}_anemico.md` | Para identificar entidades y atributos existentes del contexto |
| 🟠 Contexto | `modelo_dominio/{contexto}_enriquecido.md` | Para identificar comportamientos y reglas de negocio ya modeladas |
| 🟡 Calidad | `atributos_calidad/` | Si la HU tiene implicaciones de rendimiento, seguridad o disponibilidad |

---

## Protocolo de Consulta para el Planificador

Sigue este orden en la FASE 0:

```
1. gh auth status                          → verificar acceso
2. Listar árbol completo                   → entender estructura actual del repo
3. Leer historias_usuario_priorizadas.md   → contexto general de prioridades
4. Leer funcionalidades_criticas.md        → riesgos e impacto de la HU
5. Identificar bounded context de la HU
6. Leer event_storming/{contexto}.md       → eventos relevantes
7. Leer modelo_dominio/{contexto}_anemico.md     → entidades existentes
8. Leer modelo_dominio/{contexto}_enriquecido.md → comportamientos modelados
9. Si aplica: leer atributos_calidad/      → restricciones de calidad
10. Registrar en Metadata del plan: archivos consultados
```

---

## Manejo de Errores

| Error | Causa probable | Acción |
|-------|---------------|--------|
| `HTTP 401` | Token expirado o sin permisos | Ejecutar `gh auth refresh` o `gh auth login` |
| `HTTP 404` | Archivo no existe en esa ruta | Listar carpeta padre para encontrar la ruta real |
| `HTTP 403` | Sin acceso a la organización | Solicitar al admin de la org que otorgue acceso al token |
| `base64: invalid input` | El archivo está vacío o es binario | Verificar que el archivo sea texto plano `.md` |
| `gh: command not found` | CLI no instalado | Ver guía de instalación en `.opencode/docs/gh-auth-setup.md` |