---
name: gh-docs-reader
description:
  Consulta archivos markdown del repositorio privado de documentacion de arquisoft-uco (arquisoft-docs) usando el GitHub CLI. Permite leer historias de usuario (HU, en propuestas-hu/), historias tecnicas (HT, en docs/stories/), event storming, modelos de dominio, funcionalidades criticas, atributos de calidad, ADRs y flujos de arquitectura sin clonar el repositorio. Usar antes de planificar cualquier Historia de Usuario.
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

Verificar autenticacion antes de cualquier consulta:

```bash
gh auth status
```

Si no esta autenticado o no tiene acceso a la organizacion, detente y notifica:

> "El GitHub CLI no esta autenticado con acceso a `arquisoft-uco`. Ejecuta `gh auth login`
> y asegurate de otorgar acceso a la organizacion."

---

## Variables Base

```
ORG=arquisoft-uco
REPO=arquisoft-docs
BRANCH=main
```

---

## Estructura Real del Repositorio (validada)

```
arquisoft-docs/
├── PENDIENTES.md                                    # Temas pendientes de decision
├── CONTRIBUTING.md                                  # Guia de contribucion
├── README.md
│
├── artefactos/
│   ├── estrategicos/
│   │   ├── event-storming/                          # Event Storming por contexto
│   │   │   ├── {Contexto} - Event Storming.md       # Ej: "Ficha Perfil - Event Storming.md"
│   │   │   └── excel/                               # Versiones Excel (no legibles)
│   │   ├── modelo-dominio/
│   │   │   ├── anemico/
│   │   │   │   ├── modelo_dominio_anemico.md        # Indice general anemico
│   │   │   │   ├── documentacion/                   # Un .md por contexto (numerados)
│   │   │   │   │   ├── 05_delimitar_contextos_usuarios.md
│   │   │   │   │   ├── 06_delimitar_contextos_fichas_trabajos_grado.md
│   │   │   │   │   ├── 07_delimitar_contextos_artefactos.md
│   │   │   │   │   ├── 08_delimitar_contextos_repositorio_artefactos.md
│   │   │   │   │   ├── 09_delimitar_contextos_mapas_ruta.md
│   │   │   │   │   ├── 10_delimitar_contextos_proyectos_grado.md
│   │   │   │   │   ├── 11_delimitar_contextos_entregables_proyectos_grado.md
│   │   │   │   │   ├── 12_delimitar_contextos_evaluaciones_definitivas.md
│   │   │   │   │   ├── 14_delimitar_contextos_biblioteca.md
│   │   │   │   │   └── 15_delimitar_contextos_solicitudes.md
│   │   │   │   └── diagramas/                       # Archivos .drawio.xml (no legibles)
│   │   │   └── enriquecido/
│   │   │       ├── modelo_dominio_enriquecido.md    # Indice general enriquecido
│   │   │       ├── documentacion/                   # Un .md por contexto (numerados)
│   │   │       │   ├── 05_usuarios_modelo_enriquecido.md
│   │   │       │   ├── 06_fichas_trabajos_grado_modelo_enriquecido.md
│   │   │       │   ├── 07_artefactos_modelo_enriquecido.md
│   │   │       │   ├── 08_repositorio_artefactos_modelo_enriquecido.md
│   │   │       │   ├── 09_mapas_ruta_modelo_enriquecido.md
│   │   │       │   ├── 10_proyectos_grado_modelo_enriquecido.md
│   │   │       │   ├── 11_entregables_proyectos_grado_modelo_enriquecido.md
│   │   │       │   ├── 12_evaluaciones_definitivas_modelo_enriquecido.md
│   │   │       │   ├── 14_biblioteca_modelo_enriquecido.md
│   │   │       │   └── 15_solicitudes_modelo_enriquecido.md
│   │   │       └── excel/                           # Versiones Excel (no legibles)
│   │   ├── propuestas-hu/
│   │   │   └── historias_usuario_priorizadas.md      # HU priorizadas por release (Actor, Objeto, Comando)
│   │   ├── mapa-impacto/
│   │   │   ├── mapa_impacto.md                      # Version actual
│   │   │   └── versiones/                           # Historial de versiones
│   │   └── vision/
│   │       ├── vision.md                            # Vision actual del proyecto
│   │       └── versiones/                           # Historial de versiones
│   └── tecnicos/
│       └── diseno-arquitectonico/
│           └── drivers-arquitectonicos/
│               ├── funcionalidades-criticas/
│               │   └── funcionalidades_criticas.md  # Funcionalidades criticas del sistema
│               ├── atributos-calidad/
│               │   ├── atributos_calidad.md         # Indice de atributos
│               │   ├── listado_caracteristicas_escenarios.md
│               │   ├── QA-2-flexibilidad-escalabilidad.md
│               │   ├── QA-3-compatibilidad-interoperabilidad.md
│               │   ├── QA-3-eficiencia-desempeno.md
│               │   ├── QA-4-fiabilidad.md
│               │   ├── QA-5-mantenibilidad.md
│               │   ├── QA-5-seguridad.md
│               │   ├── QA-5-usabilidad.md
│               │   └── tacticas/                    # Tacticas por atributo
│               │       ├── TAC-FLE-flexibilidad-escalabilidad.md
│               │       ├── TAC-COM-compatibilidad-interoperabilidad.md
│               │       ├── TAC-EFI-eficiencia-desempeno.md
│               │       ├── TAC-FIA-fiabilidad.md
│               │       ├── TAC-MAN-mantenibilidad.md
│               │       ├── TAC-SEG-seguridad.md
│               │       └── TAC-USA-usabilidad.md
│               ├── restricciones-negocio/
│               │   └── restricciones_negocio.md
│               └── restricciones-tecnicas/
│                   └── restricciones_tecnicas.md
│
├── docs/
│   ├── stories/                                     # Historias TECNICAS (HT-XXX), NO historias de usuario
│   │   ├── README.md
│   │   ├── HT-001.despliegue-infraestructura-desarrollo.story.md
│   │   ├── HT-002.configuracion-ambiente-produccion.story.md
│   │   ├── HT-003.creacion-repositorios-git.story.md
│   │   ├── HT-004.configuracion-cicd-github-actions.story.md
│   │   ├── HT-005.scaffolding-spring-boot.story.md
│   │   ├── HT-006.configuracion-postgresql-flyway.story.md
│   │   ├── HT-007.integracion-keycloak-spring-security.story.md
│   │   ├── HT-008.integracion-rabbitmq-eventos.story.md
│   │   ├── HT-009.configuracion-logging-observabilidad.story.md
│   │   ├── HT-010.scaffolding-react.story.md
│   │   └── HT-011.configuracion-realm-keycloak.story.md
│   ├── architecture/                                # Documentacion de arquitectura
│   │   ├── index.md                                 # Indice de arquitectura
│   │   ├── coding-standards.md                      # Estandares de codigo
│   │   ├── cicd-pipelines.md                        # Pipelines CI/CD
│   │   ├── dod-pivots.md                            # Definition of Done
│   │   ├── flujo-autenticacion-sso-uco.md
│   │   ├── flujo-ciclo-vida-ficha-perfil.md
│   │   ├── flujo-creacion-proyecto-grado.md
│   │   ├── flujo-evaluacion-asesor-jurado.md
│   │   ├── flujo-gestion-artefactos-versionados.md
│   │   ├── flujo-gestion-solicitudes.md
│   │   ├── flujo-planificacion-mapa-ruta.md
│   │   ├── flujo-publicacion-consulta-biblioteca.md
│   │   ├── flujo-repositorio-artefactos-plantillas.md
│   │   ├── flujo-subida-entregables.md
│   │   ├── decisions/                               # ADRs (Architecture Decision Records)
│   │   │   ├── ADR-001-arquitectura-monolito-modular-asincrono.md
│   │   │   ├── ADR-002-base-datos-postgresql.md
│   │   │   ├── ADR-003-autenticacion-keycloak.md
│   │   │   ├── ADR-004-almacenamiento-minio.md
│   │   │   ├── ADR-005-logging-monitoreo.md
│   │   │   ├── ADR-006-seguridad-criptografica-keycloak.md
│   │   │   ├── ADR-007-version-java-21.md
│   │   │   ├── ADR-008-migracion-spring-boot-4.0.x.md      # Spring Boot 4.0.5, Gradle 9, Virtual Threads automaticos
│   │   │   ├── ADR-009-migracion-postgresql-15-a-18.md     # PostgreSQL 18, EOL 2030, sin breaking changes en Flyway/JPA
│   │   │   ├── ADR-010-migracion-rabbitmq-313-a-42.md      # RabbitMQ 4.2.5, AMQP 0-9-1 compatible, Khepri store
│   │   │   ├── ADR-011-documentacion-api-springdoc-openapi.md  # springdoc-openapi 2.8.17, plugin 1.9.0, Swagger UI
│   │   │   ├── ADR-INFRA-especificaciones-servidor.md
│   │   │   └── guides/                                     # Guias tecnicas de referencia (10 .md, no son ADRs)
│   │   └── risks/
│   │       └── matriz_riesgos_tecnicos.md
│   ├── spikes/                                      # Investigaciones tecnicas
│   │   ├── SPIKE-001-orquestacion-infraestructura.md
│   │   ├── SPIKE-002-versionamiento-minio.md
│   │   ├── SPIKE-003-backup-restore.md
│   │   ├── SPIKE-004-keycloak-azure-ad.md
│   │   └── SPIKE-005-rendimiento-carga.md
│   ├── actas/                                       # Actas de reunion
│   ├── bpd_completo/                                # Diagramas de proceso completos (.mmd)
│   ├── bpd_mvp/                                     # Diagramas de proceso MVP (.mmd)
│   └── propuesta/04-arquitectura/                   # Propuesta arquitectonica
│
├── mer/                                             # Modelo Entidad-Relacion
│   ├── modelo_entidad_relacion.md                   # Indice completo con todas las tablas por contexto
│   ├── 01_base_datos_y_esquemas.sql                 # Creacion de esquemas y base de datos
│   ├── 02_tablas_usuarios.sql                       # Tablas: Usuario, roles (Estudiante, Asesor, etc.)
│   ├── 03_tablas_fichas_perfil.sql                  # Tablas: FichaPerfil, EstadoFicha, Item, Revision...
│   ├── 04_tablas_artefactos.sql                     # Tablas: Artefacto, VersionArtefacto, RevisionAsesor...
│   ├── 05_tablas_repositorio_artefactos.sql         # Tablas: RepositorioArtefacto, VersionRepositorio...
│   ├── 06_tablas_mapas_ruta.sql                     # Tablas: MapaRuta, PlanEstimado, PlanReal, Tarea
│   ├── 07_tablas_proyectos_grado.sql                # Tablas: ProyectoGrado, AsesorProyecto, Estudiante...
│   ├── 08_tablas_entregables.sql                    # Tablas: EntregableProyectoGrado, ArtefactoEntregable
│   └── 09_tablas_evaluaciones.sql                   # Tablas: Evaluacion, EvaluacionAsesor, EvaluacionJurado...
│
└── templates/
    └── ARQUITECTURA_Y_ESTRUCTURA.md
```

---

## Comandos Disponibles

### 1. Leer un archivo (metodo recomendado — sin decodificar base64)

Usar el header `Accept: application/vnd.github.raw+json` para obtener el contenido directamente:

```bash
gh api "repos/arquisoft-uco/arquisoft-docs/contents/{ruta/al/archivo.md}" \
  -H "Accept: application/vnd.github.raw+json"
```

IMPORTANTE: Este metodo funciona en Windows y Linux sin depender de `base64 -d`.

Ejemplos con rutas reales:

```bash
# Historias de usuario priorizadas (fuente principal de las HU)
gh api "repos/arquisoft-uco/arquisoft-docs/contents/artefactos/estrategicos/propuestas-hu/historias_usuario_priorizadas.md" \
  -H "Accept: application/vnd.github.raw+json"

# Funcionalidades criticas
gh api "repos/arquisoft-uco/arquisoft-docs/contents/artefactos/tecnicos/diseno-arquitectonico/drivers-arquitectonicos/funcionalidades-criticas/funcionalidades_criticas.md" \
  -H "Accept: application/vnd.github.raw+json"

# Leer una historia TECNICA especifica (HT, no HU)
gh api "repos/arquisoft-uco/arquisoft-docs/contents/docs/stories/HT-007.integracion-keycloak-spring-security.story.md" \
  -H "Accept: application/vnd.github.raw+json"

# Event storming de un contexto (NOTA: los nombres tienen espacios)
gh api "repos/arquisoft-uco/arquisoft-docs/contents/artefactos/estrategicos/event-storming/Ficha Perfil - Event Storming.md" \
  -H "Accept: application/vnd.github.raw+json"

# Modelo anemico de un contexto
gh api "repos/arquisoft-uco/arquisoft-docs/contents/artefactos/estrategicos/modelo-dominio/anemico/documentacion/06_delimitar_contextos_fichas_trabajos_grado.md" \
  -H "Accept: application/vnd.github.raw+json"

# Modelo enriquecido de un contexto
gh api "repos/arquisoft-uco/arquisoft-docs/contents/artefactos/estrategicos/modelo-dominio/enriquecido/documentacion/06_fichas_trabajos_grado_modelo_enriquecido.md" \
  -H "Accept: application/vnd.github.raw+json"

# Atributo de calidad especifico
gh api "repos/arquisoft-uco/arquisoft-docs/contents/artefactos/tecnicos/diseno-arquitectonico/drivers-arquitectonicos/atributos-calidad/QA-5-seguridad.md" \
  -H "Accept: application/vnd.github.raw+json"

# Tactica de un atributo de calidad
gh api "repos/arquisoft-uco/arquisoft-docs/contents/artefactos/tecnicos/diseno-arquitectonico/drivers-arquitectonicos/atributos-calidad/tacticas/TAC-SEG-seguridad.md" \
  -H "Accept: application/vnd.github.raw+json"

# Modelo entidad-relacion (indice completo, todas las tablas)
gh api "repos/arquisoft-uco/arquisoft-docs/contents/mer/modelo_entidad_relacion.md" \
  -H "Accept: application/vnd.github.raw+json"

# Schemas y dependencias entre contextos (LEER antes de planificar Flyway con FKs cruzadas)
gh api "repos/arquisoft-uco/arquisoft-docs/contents/mer/01_base_datos_y_esquemas.sql" \
  -H "Accept: application/vnd.github.raw+json"

# SQL del MER por contexto (DDL exacto para Flyway)
gh api "repos/arquisoft-uco/arquisoft-docs/contents/mer/02_tablas_usuarios.sql" \
  -H "Accept: application/vnd.github.raw+json"

gh api "repos/arquisoft-uco/arquisoft-docs/contents/mer/03_tablas_fichas_perfil.sql" \
  -H "Accept: application/vnd.github.raw+json"

gh api "repos/arquisoft-uco/arquisoft-docs/contents/mer/04_tablas_artefactos.sql" \
  -H "Accept: application/vnd.github.raw+json"

gh api "repos/arquisoft-uco/arquisoft-docs/contents/mer/05_tablas_repositorio_artefactos.sql" \
  -H "Accept: application/vnd.github.raw+json"

gh api "repos/arquisoft-uco/arquisoft-docs/contents/mer/06_tablas_mapas_ruta.sql" \
  -H "Accept: application/vnd.github.raw+json"

gh api "repos/arquisoft-uco/arquisoft-docs/contents/mer/07_tablas_proyectos_grado.sql" \
  -H "Accept: application/vnd.github.raw+json"

gh api "repos/arquisoft-uco/arquisoft-docs/contents/mer/08_tablas_entregables.sql" \
  -H "Accept: application/vnd.github.raw+json"

gh api "repos/arquisoft-uco/arquisoft-docs/contents/mer/09_tablas_evaluaciones.sql" \
  -H "Accept: application/vnd.github.raw+json"
```

### 2. Listar archivos de una carpeta

```bash
gh api "repos/arquisoft-uco/arquisoft-docs/contents/{carpeta}" --jq ".[].name"
```

Ejemplos:

```bash
# Listar todas las historias tecnicas (HT, no HU)
gh api "repos/arquisoft-uco/arquisoft-docs/contents/docs/stories" --jq ".[].name"

# Listar event stormings disponibles (solo .md)
gh api "repos/arquisoft-uco/arquisoft-docs/contents/artefactos/estrategicos/event-storming" \
  --jq '.[] | select(.name | endswith(".md")) | .name'

# Listar modelos anemicos
gh api "repos/arquisoft-uco/arquisoft-docs/contents/artefactos/estrategicos/modelo-dominio/anemico/documentacion" \
  --jq ".[].name"

# Listar modelos enriquecidos
gh api "repos/arquisoft-uco/arquisoft-docs/contents/artefactos/estrategicos/modelo-dominio/enriquecido/documentacion" \
  --jq ".[].name"

# Listar atributos de calidad
gh api "repos/arquisoft-uco/arquisoft-docs/contents/artefactos/tecnicos/diseno-arquitectonico/drivers-arquitectonicos/atributos-calidad" \
  --jq '.[] | select(.name | endswith(".md")) | .name'

# Listar tacticas de calidad
gh api "repos/arquisoft-uco/arquisoft-docs/contents/artefactos/tecnicos/diseno-arquitectonico/drivers-arquitectonicos/atributos-calidad/tacticas" \
  --jq ".[].name"

# Listar ADRs (001-011 + INFRA; ignorar carpeta guides/)
gh api "repos/arquisoft-uco/arquisoft-docs/contents/docs/architecture/decisions" \
  --jq '.[] | select(.type=="file" and (.name | endswith(".md"))) | .name'

# Listar guias tecnicas de referencia (decisions/guides/)
gh api "repos/arquisoft-uco/arquisoft-docs/contents/docs/architecture/decisions/guides" \
  --jq '.[].name'

# Listar flujos de arquitectura
gh api "repos/arquisoft-uco/arquisoft-docs/contents/docs/architecture" \
  --jq '.[] | select(.name | startswith("flujo-")) | .name'
```

### 3. Buscar archivos por patron en todo el repo

```bash
gh api "repos/arquisoft-uco/arquisoft-docs/git/trees/main?recursive=1" \
  --jq '.tree[] | select(.type=="blob" and (.path | contains("TERMINO"))) | .path'
```

Ejemplos:

```bash
# Buscar archivos relacionados con fichas
gh api "repos/arquisoft-uco/arquisoft-docs/git/trees/main?recursive=1" \
  --jq '.tree[] | select(.type=="blob" and (.path | ascii_downcase | contains("ficha"))) | .path'

# Buscar archivos relacionados con seguridad
gh api "repos/arquisoft-uco/arquisoft-docs/git/trees/main?recursive=1" \
  --jq '.tree[] | select(.type=="blob" and (.path | ascii_downcase | contains("seguridad"))) | .path'

# Listar TODOS los .md del repo
gh api "repos/arquisoft-uco/arquisoft-docs/git/trees/main?recursive=1" \
  --jq '.tree[] | select(.type=="blob" and (.path | endswith(".md"))) | .path'
```

---

## Mapeo: Contexto del Backend → Archivos en arquisoft-docs

Usa esta tabla para saber que archivos leer segun el bounded context de la HU:

| Contexto Backend | Event Storming | Modelo Anemico | Modelo Enriquecido | SQL del MER |
|------------------|---------------|----------------|-------------------|-------------|
| `seguridad` (usuarios) | `Usuario - Event Storming.md` | `05_delimitar_contextos_usuarios.md` | `05_usuarios_modelo_enriquecido.md` | `02_tablas_usuarios.sql` |
| `fichas` | `Ficha Perfil - Event Storming.md` | `06_delimitar_contextos_fichas_trabajos_grado.md` | `06_fichas_trabajos_grado_modelo_enriquecido.md` | `03_tablas_fichas_perfil.sql` |
| `artefactos` | `Artefactos - Event Storming.md` | `07_delimitar_contextos_artefactos.md` | `07_artefactos_modelo_enriquecido.md` | `04_tablas_artefactos.sql` |
| `repositorio_artefactos` | `Repositorio Artefactos - Event Storming.md` | `08_delimitar_contextos_repositorio_artefactos.md` | `08_repositorio_artefactos_modelo_enriquecido.md` | `05_tablas_repositorio_artefactos.sql` |
| `proyectos` | `Proyecto Grado - Event Storming.md` | `10_delimitar_contextos_proyectos_grado.md` | `10_proyectos_grado_modelo_enriquecido.md` | `07_tablas_proyectos_grado.sql` |
| `entregables` | `Entregables Proyectos de Grado - Event Storming.md` | `11_delimitar_contextos_entregables_proyectos_grado.md` | `11_entregables_proyectos_grado_modelo_enriquecido.md` | `08_tablas_entregables.sql` |
| `evaluaciones` | `Evaluaciones Definitivas - Event Storming.md` | `12_delimitar_contextos_evaluaciones_definitivas.md` | `12_evaluaciones_definitivas_modelo_enriquecido.md` | `09_tablas_evaluaciones.sql` |
| (mapas_ruta)* | `Mapa Ruta - Event Storming.md` | `09_delimitar_contextos_mapas_ruta.md` | `09_mapas_ruta_modelo_enriquecido.md` | `06_tablas_mapas_ruta.sql` |
| (biblioteca)* | `Biblioteca - Event Storming.md` | `14_delimitar_contextos_biblioteca.md` | `14_biblioteca_modelo_enriquecido.md` | *(sin SQL dedicado — ver modelo_entidad_relacion.md §9)* |
| (solicitudes)* | `Solicitudes - Event Storming.md` | `15_delimitar_contextos_solicitudes.md` | `15_solicitudes_modelo_enriquecido.md` | *(sin SQL dedicado — ver modelo_entidad_relacion.md §10)* |

*Contextos documentados en arquisoft-docs que aun no tienen bounded context en el backend.

**Rutas base para los archivos del mapeo:**
- Event Storming: `artefactos/estrategicos/event-storming/{nombre archivo}`
- Modelo Anemico: `artefactos/estrategicos/modelo-dominio/anemico/documentacion/{nombre archivo}`
- Modelo Enriquecido: `artefactos/estrategicos/modelo-dominio/enriquecido/documentacion/{nombre archivo}`
- SQL del MER: `mer/{nombre archivo}`

**Para que usar cada fuente:**
- `modelo_entidad_relacion.md` → vision completa de todas las tablas, tipos de dato, PKs, FKs, indices y restricciones
- `01_base_datos_y_esquemas.sql` → orden de creacion de schemas y **grafo de dependencias entre contextos** (LEER SIEMPRE antes de planificar migraciones Flyway que involucren FKs cruzadas entre schemas)
- `{NN}_tablas_{contexto}.sql` → DDL exacto listo para Flyway; columnas, constraints y nombres de tabla tal como van a la BD
- El agente planificador DEBE consultar el SQL del MER del contexto correspondiente para definir las migraciones Flyway en el plan

**Dependencias entre schemas (extraidas de `01_base_datos_y_esquemas.sql`):**

```
usuarios              → (ninguna — schema raiz)
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

**Regla para migraciones Flyway con FKs cruzadas:** si una tabla del contexto A referencia una tabla del schema B (dependencia), la migracion de A debe ejecutarse DESPUES de que el schema B ya exista y tenga sus tablas creadas. Verificar en `01_base_datos_y_esquemas.sql` si el contexto tiene dependencias antes de definir el orden de los scripts `V{n}__*.sql`.

---

## Protocolo de Consulta para el Planificador

Sigue este orden en la FASE 0. Distingue entre **HU** (Historias de Usuario) y **HT** (Historias Tecnicas):

- **HU** = funcionalidad de negocio → viven en `artefactos/estrategicos/propuestas-hu/historias_usuario_priorizadas.md`
- **HT** = infraestructura tecnica → viven en `docs/stories/HT-XXX.*.story.md`

```
 1. gh auth status                                     → Verificar acceso
 2. Leer historias_usuario_priorizadas.md              → Buscar la HU por ID (ej. HU160)
    Extraer: Actor, Objeto de Dominio, Comando, Descripcion, Prioridad
 3. Con el Objeto de Dominio, identificar el bounded context (usar tabla de mapeo abajo)
 4. Leer el Event Storming del contexto                → Buscar el Comando exacto de la HU
    Extraer del comando: Actores, Descripcion, Informacion externa / Read Models,
    Politicas (POL-XX), Sistemas externos, Eventos generados,
    Aspectos por solucionar, Eventos previos, Comandos posteriores
 5. Leer el Modelo Anemico del contexto                → Entidades y atributos
 6. Leer el Modelo Enriquecido del contexto            → Comportamientos y reglas
 7. Leer funcionalidades_criticas.md                   → Riesgos e impacto
 8. Si el Event Storming revela Aspectos por solucionar → Registrarlos para preguntar en FASE 2
 9. Si las Politicas mencionan atributos de calidad poco claros → Leer QA relevantes
10. Leer el SQL del MER del contexto (OBLIGATORIO)     → Tablas, columnas exactas, tipos, PKs, FKs
    a) Leer primero `01_base_datos_y_esquemas.sql` para identificar si el contexto tiene
       dependencias de otros schemas (FK cruzadas). Si las tiene, anotar cuales schemas
       deben preexistir antes de correr las migraciones Flyway del contexto actual.
       Comando:
         gh api "repos/arquisoft-uco/arquisoft-docs/contents/mer/01_base_datos_y_esquemas.sql" \
           -H "Accept: application/vnd.github.raw+json"
    b) Leer el SQL especifico del contexto usando la columna "SQL del MER" de la tabla de mapeo.
       Comando:
         gh api "repos/arquisoft-uco/arquisoft-docs/contents/mer/{NN}_tablas_{contexto}.sql" \
           -H "Accept: application/vnd.github.raw+json"
    Extraer: nombres de tabla, columnas, tipos de dato, constraints, indices unicos.
    Usar estos datos para definir las migraciones Flyway en la seccion correspondiente del plan.
11. Si aplica: leer ADR relacionado                    → Decisiones arquitectonicas previas
    ADRs clave del stack actual:
    - ADR-008: Spring Boot 4.0.5 + Gradle 9 + Virtual Threads automaticos
    - ADR-009: PostgreSQL 18 (EOL 2030, compatible con Flyway/JPA sin cambios)
    - ADR-010: RabbitMQ 4.2.5 (AMQP 0-9-1 compatible, Khepri store)
    - ADR-011: springdoc-openapi 2.8.17 + plugin 1.9.0 (Swagger UI, @Tag/@Operation obligatorios)
12. Si aplica: leer flujo de arquitectura              → Flujo del proceso de negocio
13. Si aplica: listar docs/stories/ y leer HTs relacionadas → Contexto tecnico complementario
14. Registrar en Metadata del plan: archivos consultados
```

---

## Manejo de Errores

| Error | Causa probable | Accion |
|-------|---------------|--------|
| `HTTP 401` | Token expirado o sin permisos | Ejecutar `gh auth refresh` o `gh auth login` |
| `HTTP 404` | Archivo no existe en esa ruta | Listar carpeta padre con el comando de listar para encontrar la ruta real |
| `HTTP 403` | Sin acceso a la organizacion | Solicitar al admin de la org que otorgue acceso al token |
| `could not find` + `404` | Ruta con caracteres especiales mal codificados | Listar la carpeta padre y usar el nombre exacto del archivo |
| `gh: command not found` | CLI no instalado | Instalar desde https://cli.github.com/ |
| Contenido vacio o basura | Se uso `--jq '.content'` sin decodificar | Usar `-H "Accept: application/vnd.github.raw+json"` en vez de decodificar base64 |

### Notas importantes

- **Archivos con espacios en el nombre**: Los archivos de Event Storming tienen espacios (ej: `Ficha Perfil - Event Storming.md`). Usar comillas dobles en la URL del `gh api` para que funcione correctamente.
- **No usar `base64 -d`**: En Windows puede no estar disponible. Siempre usar el header `-H "Accept: application/vnd.github.raw+json"` que devuelve el contenido crudo directamente.
- **Archivos `.xlsx` y `.drawio.xml`**: No son legibles como texto. Ignorarlos y usar solo los archivos `.md`.
