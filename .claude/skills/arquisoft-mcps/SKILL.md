---
name: arquisoft-mcps
description: MCPs recomendados por defecto para trabajar en Arquisoft Backend (Context7, GitHub, IDEA/IntelliJ, drawio) y cuándo preferirlos sobre el fallback por Bash/CLI. Cargar junto con arquisoft-arquitectura al planificar, implementar, testear o mantener diagramas.
---

# Skill: arquisoft-mcps

MCPs que este proyecto prefiere por defecto **cuando están disponibles en la sesión**. Si un MCP no
está cargado, usa el fallback documentado en cada fila — la ausencia de un MCP nunca bloquea el
flujo, solo cambia la herramienta.

| MCP | Úsalo por defecto para | Fallback si no está disponible |
|---|---|---|
| **Context7** (`mcp__context7__*`) | Documentación actual de librerías/framework del stack (Spring Boot 4.0.5, Java 21, JUnit 6, Flyway, Keycloak, Bucket4j) antes de generar o revisar código que las usa — ver IDs validados en la skill `context7-stack`. Es política global del usuario, no solo de este proyecto. | Basarse en el conocimiento del modelo, dejando explícito que puede estar desactualizado para la versión exacta del stack. |
| **GitHub MCP** (`mcp__github__*`) | Operaciones sobre el repo (`arquisoft-uco/arquisoft-backend`): PRs, issues, ramas, revisar checks de CI. También sirve para leer archivos del repo privado `arquisoft-uco/arquisoft-docs` (HU/HT, event storming, MER) con `get_file_contents` en vez de `gh api` crudo. | El flujo con GitHub CLI (`gh api ... -H "Accept: application/vnd.github.raw+json"`) ya documentado en la skill `gh-docs-reader`. |
| **IDEA MCP** (`mcp__idea__*`) | Compilar (`build_project`), lint (equivalente a `checkstyleMain`/`checkstyleTest`), buscar símbolos/uso (`search_symbol`, `analyze_calls`), y consultar la BD (`execute_sql_query` contra los esquemas Postgres del proyecto) cuando el repo está abierto en IntelliJ — evita relanzar Gradle completo por Bash para verificaciones puntuales. | `./gradlew compileJava/checkstyleMain/checkstyleTest` por Bash; `psql`/cliente SQL manual. |
| **drawio MCP** (`mcp__drawio__*`) | Leer/editar los diagramas reales de `docs/diagramas/` (`diagrama-componentes.drawio`, `diagrama-paquetes.drawio`, `diagrama-secuencia.drawio`) — abrir con `open_drawio_xml`/`open_drawio_mermaid`, listar/editar páginas con `list_pages`/`get_page`/`set_page` en vez de tocar el XML a mano. | Edición manual del XML del `.drawio` (frágil y no recomendada salvo que el MCP no esté disponible). |

## No recomendados para este proyecto

`claude-in-chrome` (backend puro, sin UI que probar en navegador) y los MCPs de
`claude_ai_Microsoft_365`/`claude_ai_Stytch` (sin relación con el stack de Arquisoft) no aplican
aquí — no los invoques a menos que el usuario lo pida explícitamente para una tarea puntual.

## Regla general

Antes de usar un MCP de esta lista, verifica que está cargado en la sesión (aparece en las
herramientas disponibles o vía `ToolSearch`). Si no aparece, usa el fallback sin bloquear el flujo
ni pedirle al usuario que lo instale, salvo que la tarea explícitamente lo requiera.
