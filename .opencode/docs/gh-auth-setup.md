# Guía de Autenticación — GitHub CLI para `arquisoft-docs`

Este documento explica cómo instalar y autenticar el GitHub CLI (`gh`) con acceso
al repositorio privado `arquisoft/arquisoft-docs`, requerido por el skill `gh-docs-reader`
y el agente `planificador`.

---

## Prerequisitos

- Tener una cuenta de GitHub con acceso a la organización `arquisoft`
- Node.js 18+ instalado (para verificar: `node --version`)
- Acceso a la terminal del proyecto

---

## Paso 1 — Instalar el GitHub CLI

### macOS

```bash
brew install gh
```

### Linux (Debian / Ubuntu)

```bash
curl -fsSL https://cli.github.com/packages/githubcli-archive-keyring.gpg \
  | sudo dd of=/usr/share/keyrings/githubcli-archive-keyring.gpg

echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/githubcli-archive-keyring.gpg] \
  https://cli.github.com/packages stable main" \
  | sudo tee /etc/apt/sources.list.d/github-cli.list > /dev/null

sudo apt update && sudo apt install gh -y
```

### Windows

```powershell
winget install --id GitHub.cli
```

### Verificar instalación

```bash
gh --version
# Debe mostrar: gh version 2.x.x
```

---

## Paso 2 — Autenticar con GitHub

Ejecuta el login interactivo:

```bash
gh auth login
```

Responde las preguntas así:

```
? Where do you use GitHub?          → GitHub.com
? What is your preferred protocol?  → HTTPS
? Authenticate Git with credentials?→ Yes
? How would you like to authenticate?→ Login with a web browser
```

Se abrirá el navegador. Inicia sesión con tu cuenta que tiene acceso a `arquisoft-uco`.

---

## Paso 3 — Otorgar acceso a la organización

> ⚠️ Este paso es crítico para repos privados de organizaciones.

Después del login, autoriza el acceso a la organización:

```bash
gh auth refresh -h github.com -s read:org,repo
```

Esto abre el navegador nuevamente. En la pantalla de OAuth verás la organización
`arquisoft-uco` — haz clic en **"Grant"** junto a ella antes de aprobar.

Si no aparece la opción de Grant, solicita al administrador de la organización que
apruebe el acceso en: `https://github.com/organizations/arquisoft-uco/settings/oauth_application_policy`

---

## Paso 4 — Verificar acceso al repositorio

```bash
# Verificar estado de autenticación
gh auth status

# Verificar acceso al repo privado
gh api repos/arquisoft-uco/arquisoft-docs --jq '.full_name, .private, .default_branch'
```

Resultado esperado:

```
arquisoft-uco/arquisoft-docs
true
main
```

Si ves `HTTP 404`, el token no tiene acceso al repo. Revisa el Paso 3.
Si ves `HTTP 401`, el token expiró — vuelve a ejecutar `gh auth login`.

---

## Paso 5 — Verificar lectura de archivos

```bash
# Listar archivos del repositorio
gh api repos/arquisoft-uco/arquisoft-docs/git/trees/main \
  --jq '.tree[] | select(.type=="blob") | .path' | head -20

# Leer un archivo específico
gh api repos/arquisoft-uco/arquisoft-docs/contents/historias_usuario_priorizadas.md \
  --jq '.content' | base64 -d | head -30
```

Si ambos comandos devuelven contenido, la autenticación está completa. ✅

---

## Paso 6 — Configurar variable de entorno (opcional pero recomendado)

Para evitar que el token expire durante sesiones largas de opencode, exporta el token:

```bash
# Obtener el token actual
gh auth token

# Agregar al perfil de tu shell (.zshrc, .bashrc, etc.)
echo 'export GH_TOKEN=$(gh auth token)' >> ~/.zshrc
source ~/.zshrc
```

---

## Solución de Problemas

| Error | Causa | Solución |
|-------|-------|----------|
| `HTTP 401 Bad credentials` | Token expirado | `gh auth login` de nuevo |
| `HTTP 404 Not Found` | Sin acceso al repo o la org | Repetir Paso 3, verificar con admin |
| `HTTP 403 Forbidden` | Organización bloquea el OAuth app | Admin debe aprobar en settings de la org |
| `gh: command not found` | CLI no instalado | Repetir Paso 1 |
| `base64: invalid input` | Archivo vacío o binario | Verificar que la ruta del archivo es correcta |
| `Resource not accessible by personal access token` | Token sin scope `read:org` | `gh auth refresh -h github.com -s read:org,repo` |

---

## Verificación Final para el Agente Planificador

Ejecuta este bloque completo para confirmar que el skill `gh-docs-reader` funcionará:

```bash
echo "=== 1. Versión del CLI ===" && gh --version
echo "=== 2. Estado de autenticación ===" && gh auth status
echo "=== 3. Acceso al repositorio ===" && \
  gh api repos/arquisoft-uco/arquisoft-docs --jq '"Repo: \(.full_name) | Privado: \(.private)"'
echo "=== 4. Lectura de árbol ===" && \
  gh api repos/arquisoft-uco/arquisoft-docs/git/trees/main \
  --jq '.tree | length | "Total archivos: \(.)"'
echo "✅ Todo listo — el agente planificador puede acceder a arquisoft-docs"
```
