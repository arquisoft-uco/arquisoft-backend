---
name: gh-docs-reader
description: Consulta archivos markdown del repositorio privado de documentacion de arquisoft-uco (arquisoft-docs) usando el GitHub CLI. Permite leer historias de usuario (HU, en propuestas-hu/priorizacion/ mas los backlogs por fase en propuestas-hu/backlog/), historias tecnicas (HT, en docs/stories/), event storming, modelos de dominio, funcionalidades criticas, atributos de calidad, ADRs y flujos de arquitectura sin clonar el repositorio. Usar antes de planificar cualquier Historia de Usuario.
---

# Skill: gh-docs-reader

Consulta el repositorio privado `arquisoft-uco/arquisoft-docs` (rama `main`) con el GitHub CLI.
Se usa en la FASE 0 del planificador, antes de preguntarle nada al usuario.

## Prerequisito

`gh auth status`. Si no esta autenticado o no tiene acceso a la organizacion, detente y notifica:

> "El GitHub CLI no esta autenticado con acceso a `arquisoft-uco`. Ejecuta `gh auth login`
> y asegurate de otorgar acceso a la organizacion."

## Estructura del repositorio (validada)

```
arquisoft-docs/
├── PENDIENTES.md                                   # Temas pendientes de decision
├── artefactos/
│   ├── estrategicos/
│   │   ├── event-storming/{Contexto} - Event Storming.md   # nombres con espacios
│   │   ├── modelo-dominio/
│   │   │   ├── anemico/documentacion/{NN}_delimitar_contextos_{contexto}.md
│   │   │   └── enriquecido/documentacion/{NN}_{contexto}_modelo_enriquecido.md
│   │   ├── propuestas-hu/                          # ver seccion propia
│   │   │   ├── priorizacion/historias_usuario_priorizadas.md   # FICHA de cada HU — fuente de verdad
│   │   │   ├── backlog/                            # README, fase-1-mvp, fase-2-entrega-completa, fase-3-consolidacion
│   │   │   └── fase-2/                             # reparto nominal del Equipo 2 (no es fuente de negocio)
│   │   ├── mapa-impacto/mapa_impacto.md
│   │   └── vision/vision.md
│   └── tecnicos/diseno-arquitectonico/drivers-arquitectonicos/
│       ├── funcionalidades-criticas/funcionalidades_criticas.md
│       ├── atributos-calidad/                      # atributos_calidad.md, QA-*.md, tacticas/TAC-*.md
│       ├── restricciones-negocio/restricciones_negocio.md
│       └── restricciones-tecnicas/restricciones_tecnicas.md
├── docs/
│   ├── stories/HT-XXX.{slug}.story.md              # Historias TECNICAS, no HU
│   ├── architecture/
│   │   ├── flujo-*.md                              # flujos de proceso de negocio
│   │   ├── coding-standards.md · dod-pivots.md · cicd-pipelines.md
│   │   ├── decisions/ADR-*.md                      # ADRs (+ guides/, que no son ADRs)
│   │   └── risks/matriz_riesgos_tecnicos.md
│   ├── spikes/SPIKE-*.md
│   ├── bpd_mvp/ · bpd_completo/                    # diagramas de proceso (.mmd)
│   └── hus/planes/ · hus/validaciones/             # planes y reportes publicados por @4c-commit
└── mer/
    ├── modelo_entidad_relacion.md                  # indice: todas las tablas por contexto
    ├── 01_base_datos_y_esquemas.sql                # esquemas y grafo de dependencias
    ├── {NN}_tablas_{contexto}.sql                  # DDL exacto
    └── data/{NN}_data_{contexto}.sql               # INSERTs: LOS VALORES de cada catalogo
```

Los `.xlsx` y `.drawio.xml` no son legibles como texto: ignoralos, todo esta en el `.md` de al lado.

## Comandos

```bash
# Leer un archivo (contenido crudo; no dependas de base64 -d, que en Windows puede faltar)
gh api "repos/arquisoft-uco/arquisoft-docs/contents/{ruta}" -H "Accept: application/vnd.github.raw+json"

# Listar una carpeta (filtra con jq: endswith(".md"), startswith("flujo-"), select(.type=="file")…)
gh api "repos/arquisoft-uco/arquisoft-docs/contents/{carpeta}" --jq '.[] | "\(.type)\t\(.name)"'

# Buscar por patron en todo el repo
gh api "repos/arquisoft-uco/arquisoft-docs/git/trees/main?recursive=1" \
  --jq '.tree[] | select(.type=="blob" and (.path | ascii_downcase | contains("ficha"))) | .path'
```

Las rutas con espacios (Event Storming) funcionan dentro de las comillas dobles de la URL.

## Mapeo: contexto del backend → archivos en arquisoft-docs

| Contexto Backend | Event Storming | Modelo Anemico | Modelo Enriquecido | SQL del MER | Data de referencia |
|------------------|---------------|----------------|-------------------|-------------|--------------------|
| `usuarios` y `seguridad` | `Usuario - Event Storming.md` | `05_delimitar_contextos_usuarios.md` | `05_usuarios_modelo_enriquecido.md` | `02_tablas_usuarios.sql` | `data/02_data_usuarios.sql` |
| `fichas` | `Ficha Perfil - Event Storming.md` | `06_delimitar_contextos_fichas_trabajos_grado.md` | `06_fichas_trabajos_grado_modelo_enriquecido.md` | `03_tablas_fichas_perfil.sql` | `data/03_data_fichas_perfil.sql` |
| `artefactos` | `Artefactos - Event Storming.md` | `07_delimitar_contextos_artefactos.md` | `07_artefactos_modelo_enriquecido.md` | `04_tablas_artefactos.sql` | `data/04_data_artefactos.sql` |
| `repositorio_artefactos` | `Repositorio Artefactos - Event Storming.md` | `08_delimitar_contextos_repositorio_artefactos.md` | `08_repositorio_artefactos_modelo_enriquecido.md` | `05_tablas_repositorio_artefactos.sql` | *(ninguna — no tiene catalogos)* |
| `proyectos` | `Proyecto Grado - Event Storming.md` | `10_delimitar_contextos_proyectos_grado.md` | `10_proyectos_grado_modelo_enriquecido.md` | `07_tablas_proyectos_grado.sql` | `data/07_data_proyectos_grado.sql` |
| `entregables` | `Entregables Proyectos de Grado - Event Storming.md` | `11_delimitar_contextos_entregables_proyectos_grado.md` | `11_entregables_proyectos_grado_modelo_enriquecido.md` | `08_tablas_entregables.sql` | `data/08_data_entregables.sql` |
| `evaluaciones` | `Evaluaciones Definitivas - Event Storming.md` | `12_delimitar_contextos_evaluaciones_definitivas.md` | `12_evaluaciones_definitivas_modelo_enriquecido.md` | `09_tablas_evaluaciones.sql` | `data/09_data_evaluaciones.sql` |
| (mapas_ruta)* | `Mapa Ruta - Event Storming.md` | `09_delimitar_contextos_mapas_ruta.md` | `09_mapas_ruta_modelo_enriquecido.md` | `06_tablas_mapas_ruta.sql` | `data/06_data_mapas_ruta.sql` |
| `biblioteca` | `Biblioteca - Event Storming.md` | `14_delimitar_contextos_biblioteca.md` | `14_biblioteca_modelo_enriquecido.md` | `10_tablas_biblioteca.sql` | `data/10_data_biblioteca.sql` |
| `solicitudes` | `Solicitudes - Event Storming.md` | `15_delimitar_contextos_solicitudes.md` | `15_solicitudes_modelo_enriquecido.md` | `11_tablas_solicitudes.sql` | `data/11_data_solicitudes.sql` |

Rutas base: Event Storming en `artefactos/estrategicos/event-storming/`, modelos en
`artefactos/estrategicos/modelo-dominio/{anemico|enriquecido}/documentacion/`, SQL y data en `mer/`.

\* `mapas_ruta` tiene documentacion y DDL, pero no es un bounded context del backend (los vigentes
son los de `CLAUDE.md`). Modelarlo exige antes su modulo Gradle, su `{Contexto}DataSourceConfig` y su
base en `init-db.sql`: no lo asumas planificando.

**`notificaciones` no tiene fila a proposito.** Es un contexto del backend que no existe en
arquisoft-docs (sin Event Storming, modelos ni tablas en el MER): infraestructura transversal nacida
del backend. Lo que llega ahi es siempre consecuencia de una transicion de otro contexto, asi que se
consulta el Event Storming del **productor**. Sus enums (`TipoNotificacion`, `EstadoNotificacion`) no
salen de `mer/data/` —son `VARCHAR` sin catalogo—: una constante nueva va en el enum de dominio y en
su espejo `TipoNotificacionEvento`, sin migracion. Buscar `13_..._notificaciones.md` o
`data/12_data_notificaciones.sql` da 404: no existen.

## El MER

- `modelo_entidad_relacion.md` → vision de todas las tablas: tipos, PKs, FKs, indices, restricciones.
- `01_base_datos_y_esquemas.sql` → grafo de dependencias entre contextos: de donde viene un dato y,
  por tanto, que evento AMQP hay que consumir.
- `{NN}_tablas_{contexto}.sql` → DDL exacto para las migraciones Flyway del plan.
- `data/{NN}_data_{contexto}.sql` → los valores de cada catalogo (abajo).

**Una dependencia entre contextos nunca es una FK.** El MER habla de schemas dentro de una base; el
backend crea **una base por contexto** (`init-db.sql`), con su `DataSource` y su Flyway en
`db/migration/{contexto}/`, asi que un `REFERENCES` cruzado es imposible. Se resuelve con una tabla
replica local poblada por eventos, como `asesor_ficha` y `estudiante` en `fichas`.

```
usuarios              → (raiz)
fichas_perfil         → usuarios
repositorio_artefactos→ usuarios
proyectos_grado       → usuarios, fichas_perfil
mapas_ruta            → usuarios, proyectos_grado
artefactos            → usuarios, proyectos_grado, repositorio_artefactos
entregables           → usuarios, proyectos_grado, artefactos
evaluaciones          → usuarios, entregables
biblioteca            → usuarios
solicitudes           → usuarios
```

### `mer/data/` — la fuente de verdad de los enums de catalogo

Si la HU toca un estado o un tipo, este archivo es de lectura obligatoria y manda sobre el Event
Storming (que nombra estados en prosa), el modelo enriquecido (que a veces lista de mas) y cualquier
plan viejo. Ya paso: se implemento un `EN_REVISION` que el MER no tiene y hubo que quitarlo.

| Columna | Es | En el codigo |
|---|---|---|
| `id` | `Enum.name()` en UPPER_SNAKE_CASE (ADR-012) | La constante; lo que devuelve `getId()` y se persiste |
| `nombre` | Etiqueta legible | `getNombre()` — se queda en Java, no va al catalogo Redis |
| `descripcion` | Texto explicativo | Solo documentacion |

El conjunto de filas **es** el conjunto de constantes. La unica constante sin fila es el centinela
`VACIO`, que nunca se persiste.

### Ancho de las tablas de catalogo (ADR-012 v1.1, enmienda 2026-08-25)

Una tabla de catalogo **nueva** lleva `id VARCHAR(60)`, `nombre VARCHAR(60)`,
`descripcion VARCHAR(300)`, y sus FK el mismo ancho (nunca `UUID`). Las tres de `fichas` son
excepciones documentadas y **no se migran**:

| Tabla | id | nombre | descripcion |
|---|---|---|---|
| `estado_ficha` | `VARCHAR(50)` | `VARCHAR(30)` | `VARCHAR(200)` |
| `tipo_item` | `VARCHAR(50)` | `VARCHAR(20)` | `VARCHAR(500)` |
| `estado_evaluacion` | `VARCHAR(50)` | `VARCHAR(100)` | `VARCHAR(255)` |

Un `ALTER TABLE` para "alinearlas" seria un breaking-change sobre un catalogo vivo con FKs, a cambio
de nada. Copia el ancho del `{NN}_tablas_{contexto}.sql` de la tabla concreta.

## `propuestas-hu/` — reestructurada el 2026-09-08

`historias_usuario_priorizadas.md` se movio a `priorizacion/`: la ruta plana anterior da 404, y ese
404 significa ruta vieja, no HU inexistente.

| Subcarpeta | Que es | Cuando la lees |
|---|---|---|
| `priorizacion/` | La **ficha** de cada una de las 277 HU: Actor, Objeto de Dominio, Comando, Descripcion, MoSCoW, tallaje | **Siempre.** De aqui sale el bounded context |
| `backlog/` | **Derivado**: Fase 1 (114, Equipo 1) y Fase 2 (163, Equipo 2), disjuntas, mas Fase 3 (HU278-HU280). Aporta fase, flujo (F01-F10), equipo y **prioridad de ejecucion** | Para fase/flujo, que la habilita, o si es HU278-HU280 |
| `fase-2/` | Reparto nominal del trabajo del Equipo 2 | Solo para asignacion de personas. No es fuente de negocio |

- Ante discrepancia gana `priorizacion/`, salvo HU278-HU280, que **solo existen** en
  `backlog/fase-3-consolidacion.md` (cierran Revision Item / Observacion Item y dependen de HU033).
- La priorizacion por Release/Sprint quedo obsoleta: el orden vigente es la *Prioridad de ejecucion*
  de `backlog/` (menor = primero). No cites "Release N". Las prioridades de Fase 3 (1043/1047/1051) no
  son comparables con las de Fase 1 y 2.
- Una HU es de Fase 1 si y solo si algun `.mmd` de `docs/bpd_mvp/` la referencia; el resto es Fase 2.

## Protocolo de Consulta para el Planificador

**HU** = negocio, ficha en `propuestas-hu/priorizacion/` (HU278-HU280 en `backlog/fase-3-consolidacion.md`).
**HT** = infraestructura tecnica, en `docs/stories/HT-XXX.*.story.md`.

```
 1. gh auth status
 2. priorizacion/historias_usuario_priorizadas.md → la HU por ID: Actor, Objeto de Dominio,
    Comando, Descripcion, MoSCoW, tallaje.
 2b. Backlog de su fase → fase, flujo, equipo, prioridad de ejecucion, HU que la habilitan.
 3. Con el Objeto de Dominio, el bounded context (tabla de mapeo).
 4. Event Storming del contexto → el Comando exacto: Actores, Descripcion, Read Models, Politicas
    (POL-XX), Sistemas externos, Eventos generados, Aspectos por solucionar, Eventos previos,
    Comandos posteriores.
 5. Modelo Anemico → entidades y atributos.
 6. Modelo Enriquecido → comportamientos y reglas.
 7. funcionalidades_criticas.md → riesgos e impacto.
 8. Aspectos por solucionar del Event Storming → registrarlos para preguntar en la FASE 2.
 9. Politicas con atributos de calidad poco claros → leer los QA relevantes.
10. SQL del MER del contexto (OBLIGATORIO):
    a) 01_base_datos_y_esquemas.sql → cada dependencia se anota como "replica local + evento AMQP
       a consumir", nunca como FK ni como orden entre migraciones.
    b) {NN}_tablas_{contexto}.sql → tablas, columnas, tipos, constraints, indices unicos para las
       migraciones del plan; anchos de catalogo tal cual vienen.
    c) Si la HU toca un estado o tipo: data/{NN}_data_{contexto}.sql (OBLIGATORIO) → listar en el
       plan cada fila (id, nombre). Si otra fuente nombra un estado sin fila aqui, gana este archivo
       y se anota como discrepancia para preguntar.
11. Si aplica, el ADR relacionado. Los del stack: ADR-008 (Spring Boot 4.0.5 + Gradle 9 + Virtual
    Threads), ADR-009 (PostgreSQL 18), ADR-010 (RabbitMQ 4.2.5), ADR-011 (springdoc, @Tag/@Operation
    obligatorios), ADR-012 v1.1 (PK semantica de catalogo = Enum.name(); renombrar es breaking-change).
12. Si aplica, el flujo de arquitectura (docs/architecture/flujo-*.md).
13. Si aplica, las HT relacionadas de docs/stories/.
14. Registrar en la Metadata del plan los archivos consultados.
```

## Errores

| Error | Accion |
|-------|--------|
| `HTTP 401` | `gh auth refresh` o `gh auth login` |
| `HTTP 403` | Pedir al admin de la org acceso para el token |
| `HTTP 404` | Listar la carpeta padre y usar el nombre exacto (espacios, tildes) |
| `HTTP 404` en `propuestas-hu/historias_usuario_priorizadas.md` | Ruta previa al 2026-09-08: usar `propuestas-hu/priorizacion/...`. No concluyas que la HU no existe |
| Contenido en base64 o vacio | Faltó `-H "Accept: application/vnd.github.raw+json"` |
| `gh: command not found` | Instalar desde https://cli.github.com/ |
