> [!WARNING]
> **SOLO LECTURA — NO USAR COMO CONTEXTO DE AGENTES O IA**
>
> Este archivo es documentación de referencia para desarrolladores humanos.
> **No debe ser leído ni indexado por agentes, asistentes de IA ni herramientas de generación de código.**
> El contexto autoritativo del proyecto para agentes reside exclusivamente en `AGENTS.md` (raíz del repositorio)
> y en los skills de `.opencode/skills/`. Usar este archivo como contexto puede producir código incorrecto,
> versiones desactualizadas o convenciones que no reflejan el estado real del proyecto.

# Arquisoft Backend - Arquitectura Hexagonal Modular

Aplicación backend para Arquisoft basada en **Arquitectura Hexagonal Modular** con **7 contextos independientes** y **comunicación asincrónica** mediante RabbitMQ.

## Índice

1. [Contextos de Negocio](#contextos-de-negocio)
2. [Estructura del Proyecto](#estructura-del-proyecto)
3. [Stack Tecnológico](#stack-tecnológico)
4. [Instalación y Configuración](#instalación-y-configuración)
5. [Ejecución](#ejecución)
6. [Arquitectura y Flujos](#arquitectura-y-flujos)
7. [Perfiles de Ejecución](#perfiles-de-ejecución)
8. [Testing](#testing)
9. [Documentación Adicional](#documentación-adicional)

---

## Contextos de Negocio

El proyecto está organizado en **7 contextos independientes**, cada uno representando un dominio de negocio específico:

| # | Contexto | Descripción |
|---|----------|-------------|
| 1 | **Seguridad** | Autenticación OAuth2/JWT (Keycloak), roles, permisos, rate limiting, auditoría |
| 2 | **Fichas** | Fichas de caracterización de trabajos de grado |
| 3 | **Proyectos** | Creación y gestión de proyectos de grado |
| 4 | **Artefactos** | Gestión de documentos y artefactos de proyecto |
| 5 | **Repositorio Artefactos** | Control de versiones y almacenamiento |
| 6 | **Entregables** | Gestión de entregables y hitos del proyecto |
| 7 | **Evaluaciones** | Evaluaciones finales y calificaciones |

---

## Estructura del Proyecto

```
arquisoft-backend/
├── shared/                              # Módulo compartido (7 sub-módulos)
│   ├── domain/                          # Eventos base (DomainEvent, AggregateRoot)
│   ├── exceptions/                      # DomainException base
│   ├── amqp/                            # EventPublisher interface (RabbitMQ)
│   ├── postgres/                        # BaseRepository (JPA)
│   ├── redis/                           # RedisClient interface
│   ├── web/                             # HttpClient interface
│   └── validation/                      # Anotaciones de validación (@ValidEmail)
│
├── seguridad/                           # CONTEXTO 1: Seguridad y Autenticación
│   ├── domain/                          # UserRole, CurrentUserProvider, JwtTokenProvider
│   ├── application/                     # DTOs (Login, Token, AuthenticatedUser)
│   └── infrastructure/                  # SeguridadConfig, AutenticacionCommandControlador, Keycloak, LimiteSolicitudes
│
├── fichas/                              # CONTEXTO 2: Fichas de Trabajo de Grado
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
├── proyectos/                           # CONTEXTO 3: Proyectos de Grado
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
├── artefactos/                          # CONTEXTO 4: Artefactos
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
├── repositorio_artefactos/              # CONTEXTO 5: Repositorio de Artefactos
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
├── entregables/                         # CONTEXTO 6: Entregables
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
├── evaluaciones/                        # CONTEXTO 7: Evaluaciones Definitivas
│   ├── domain/
│   ├── application/
│   └── infrastructure/
│
├── src/
│   └── main/
│       ├── java/com/arquisoft/
│       │   └── ArquisoftApplication.java    # Punto de entrada Spring Boot
│       └── resources/
│           ├── application.yml              # Config base
│           ├── application-dev.yml          # Perfil desarrollo
│           └── application-prod.yml         # Perfil producción
│
├── build.gradle                         # Build principal
├── settings.gradle                      # Definición de módulos (7 contextos + shared)
├── gradle.properties                    # Versiones de dependencias
├── docker-compose.yml                   # Orquestación de servicios
├── Dockerfile                           # Imagen Docker multi-stage
└── init-db.sql                          # Inicialización de BD (7 schemas)
```

---

## Stack Tecnológico

### Backend

| Componente | Tecnología | Versión |
|-----------|-----------|---------|
| Framework | Spring Boot | 4.0.5 |
| Lenguaje | Java | 21 |
| Build | Gradle | 9.0.0 |
| Patrón | Hexagonal (Puertos y Adaptadores) | - |
| Concurrencia | Virtual Threads | Java 21 (auto) |

### Base de Datos

| Componente | Tecnología |
|-----------|-----------|
| Motor | PostgreSQL 18 |
| Migraciones | Flyway 11.20.3 |
| ORM | Spring Data JPA / JdbcTemplate |

### Mensajería y Eventos

| Componente | Tecnología |
|-----------|-----------|
| Message Broker | RabbitMQ 4.2.5 |
| Modo | Topic Exchange (desacoplamiento asincrónico) |
| Dead Letter Queue | Para manejo de errores |

### Cache y Sesiones

| Componente | Tecnología |
|-----------|-----------|
| Cache | Redis 7 |
| Sesiones distribuidas | Redis |

### Autenticación y Autorización

| Componente | Tecnología |
|-----------|-----------|
| Servidor OAuth2 | Keycloak 26.6 |
| Protocolo | OpenID Connect |
| JWT | spring-security-oauth2-jose |
| Rate Limiting | Bucket4j 7.6.0 |

### Almacenamiento de Archivos

| Componente | Tecnología |
|-----------|-----------|
| Servidor | Nextcloud 27+ |
| Protocolo | WebDAV |

### Testing

| Componente | Tecnología | Versión |
|-----------|-----------|---------|
| Tests | JUnit | 6.0.3 |
| Mocking | Mockito + AssertJ | - |
| BD Test | H2 | 2.3.232 |

### Utilidades

- **Lombok** 1.18.30 — Reducción de boilerplate
- **Jackson** — Serialización JSON
- **Spring Boot Actuator** — Métricas y health checks

---

## Instalación y Configuración

### Requisitos Previos

- Java 21+
- Docker y Docker Compose
- Git

### Paso 1: Clonar el Repositorio

```bash
git clone <repositorio-url>
cd arquisoft-backend
```

### Paso 2: Levantar Servicios con Docker Compose

```bash
docker-compose up -d
```

Esto levantará:
- PostgreSQL (puerto 5432) — con 7 schemas creados automáticamente
- RabbitMQ (puertos 5672, 15672)
- Redis (puerto 6379)
- Keycloak (puerto 8081)
- Nextcloud (puerto 8082)

### Paso 3: Compilar y Ejecutar la Aplicación

```bash
# Construir el proyecto
./gradlew build

# Ejecutar con perfil de desarrollo
./gradlew bootRun --args='--spring.profiles.active=dev'

# Ejecutar en producción
./gradlew bootRun --args='--spring.profiles.active=prod'
```

La aplicación estará disponible en: `http://localhost:8080/api`

---

## Acceso a Servicios

| Servicio | URL | Usuario | Contraseña |
|----------|-----|--------|-----------|
| **Backend** | http://localhost:8080/api | - | - |
| **RabbitMQ Management** | http://localhost:15672 | guest | guest |
| **Keycloak Admin** | http://localhost:8081/admin | admin | admin |
| **Nextcloud** | http://localhost:8082 | admin | admin123 |
| **PostgreSQL** | localhost:5432 | arquisoft | arquisoft123 |
| **Redis** | localhost:6379 | - | - |

---

## Arquitectura de Eventos

### Flujo Asincrónico

1. **Request Síncrono**: El cliente envía una solicitud HTTP
2. **Respuesta Inmediata**: El contexto responde rápidamente (< 100ms)
3. **Publicación de Evento**: El dominio emite un evento de negocio
4. **RabbitMQ**: El evento se envía al exchange `arquisoft.events`
5. **Consumo Asincrónico**: Otros contextos consumen el evento en background

```
Usuario (HTTP)
    ↓ (Síncrono)
[CONTEXTO A - Controller]
    ↓
[CONTEXTO A - UseCase]
    ↓
[BD + Evento]
    ↓
HTTP 201 Created ✅ (Usuario satisfecho - ~100ms)
    
    ↓↓↓ (ASINCRÓNICO - Background)
    
[RabbitMQ - Exchange: arquisoft.events]
    ↓
[CONTEXTO B - EventListener]
[CONTEXTO C - EventListener]
    ↓
[Procesamiento independiente]
```

### Eventos Principales por Contexto

| Contexto | Eventos Emitidos | Routing Key |
|----------|-----------------|-------------|
| Fichas | FichaCreada, FichaAprobada | `fichas.creada`, `fichas.aprobada` |
| Proyectos | ProyectoCreado, ProyectoFinalizado | `proyectos.creado`, `proyectos.finalizado` |
| Artefactos | ArtefactoCreado, ArtefactoEvaluado | `artefactos.creado`, `artefactos.evaluado` |
| Repositorio | VersionPublicada | `repositorio.version-publicada` |
| Entregables | EntregableSubido, EntregableEvaluado | `entregables.subido`, `entregables.evaluado` |
| Evaluaciones | EvaluacionCalificada | `evaluaciones.calificada` |

Ver `ARQUITECTURA_ASINCRONICO_ARQUISOFT.md` para detalles completos.

---

## Estructura de Módulos (Gradle)

### Dependencias Entre Capas

```
shared:domain (base)
    ↓
{contexto}:domain (usa shared:domain)
    ↓
{contexto}:application (usa {contexto}:domain)
    ↓
{contexto}:infrastructure (usa {contexto}:application + shared:*)
    ↓
Aplicación Principal (incluye todos los *:infrastructure)
```

### Regla fundamental

```
Domain ← Application ← Infrastructure
(sin deps)  (usa domain)  (usa todo + frameworks)
```

---

## Perfiles de Ejecución

| Perfil | Archivo | Uso |
|--------|---------|-----|
| **default** | `application.yml` | Config base compartida |
| **dev** | `application-dev.yml` | Desarrollo local (debug logs, localhost, rate limit deshabilitado) |
| **prod** | `application-prod.yml` | Producción (env vars, rate limit, file logging) |

```bash
# Desarrollo
./gradlew bootRun --args='--spring.profiles.active=dev'

# Producción
java -jar app.jar --spring.profiles.active=prod
```

---

## Base de Datos

### Esquemas Separados por Contexto

Cada contexto tiene su propio schema en PostgreSQL:

```sql
-- NOTA: el nombre del schema NO coincide con el contexto en 3 casos
CREATE SCHEMA IF NOT EXISTS usuarios;             -- contexto: seguridad
CREATE SCHEMA IF NOT EXISTS fichas_perfil;        -- contexto: fichas
CREATE SCHEMA IF NOT EXISTS proyectos_grado;      -- contexto: proyectos
CREATE SCHEMA IF NOT EXISTS artefactos;
CREATE SCHEMA IF NOT EXISTS repositorio_artefactos;
CREATE SCHEMA IF NOT EXISTS entregables;
CREATE SCHEMA IF NOT EXISTS evaluaciones;
```

### Migraciones con Flyway

```
{contexto}/infrastructure/src/main/resources/db/migration/
├── V1.0__{contexto}_schema.sql
└── V1.1__{contexto}_initial_data.sql
```

---

## Testing

```bash
# Tests de todo el proyecto
./gradlew test

# Tests de un contexto específico
./gradlew fichas:test

# Tests de una capa específica
./gradlew fichas:domain:test
```

---

## Convenciones de Código

### Paquete Base

```
com.arquisoft.{contexto}.domain.model          # Entidades
com.arquisoft.{contexto}.domain.port.in        # Puertos entrada (use cases)
com.arquisoft.{contexto}.domain.port.out       # Puertos salida (repositories)
com.arquisoft.{contexto}.application.dto       # Data Transfer Objects
com.arquisoft.{contexto}.application.usecase   # Implementación use cases
com.arquisoft.{contexto}.infrastructure.adapter.in   # Controllers REST
com.arquisoft.{contexto}.infrastructure.adapter.out  # Repositorios, Clientes
com.arquisoft.{contexto}.infrastructure.config       # Configuraciones
```

### Nombres de Clases

| Tipo | Formato | Ejemplo |
|------|---------|---------|
| Entidad | `{Nombre}.java` | `Ficha.java` |
| Puerto entrada | `{Accion}{Entidad}UseCase.java` | `CrearFichaUseCase.java` |
| Puerto salida | `{Entidad}RepositoryPort.java` | `FichaRepositoryPort.java` |
| DTO | `{Entidad}DTO.java` | `FichaDTO.java` |
| Impl UseCase | `{Accion}{Entidad}UseCaseImpl.java` | `CrearFichaUseCaseImpl.java` |
| Controller | `{Entidad}Controller.java` | `FichaController.java` |
| Repo Adapter | `{Entidad}RepositoryAdapter.java` | `FichaRepositoryAdapter.java` |
| Evento | `{Nombre}Event.java` | `FichaCreadaEvent.java` |
| Migración | `V{ver}__{contexto}_{desc}.sql` | `V1.0__fichas_schema.sql` |

---

## Monitoreo

### Spring Boot Actuator

Endpoints en `http://localhost:8080/api/actuator/`:

```
/health              - Estado de salud
/metrics             - Métricas del sistema
/prometheus          - Métricas en formato Prometheus
```

---

## Deploy

### Construir imagen Docker

```bash
docker build -t arquisoft-backend:latest .
```

### Ejecutar

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=jdbc:postgresql://db:5432/arquisoft \
  arquisoft-backend:latest
```

---

## Documentación Adicional

- **Arquitectura Asincronica**: `ARQUITECTURA_ASINCRONICO_ARQUISOFT.md`
- **AggregateRoot y Eventos**: Ver sección `AggregateRoot y Eventos de Dominio` en `ARQUITECTURA_Y_ESTRUCTURA.md`
- **Virtual Threads**: Ver sección `Virtual Threads (ADR-008)` en `ARQUITECTURA_Y_ESTRUCTURA.md`
- **Estructura config/**: Ver sección `Separación entre config/ raíz y seguridad/infrastructure/config/` en `ARQUITECTURA_Y_ESTRUCTURA.md`
- **Arquitectura y Estructura**: `ARQUITECTURA_Y_ESTRUCTURA.md`
- **Guía de Inicio Rápido**: `QUICK_START.md`
- **Contribuir**: `CONTRIBUTING.md`

---

**Versión**: 1.0.0
**Arquitectura**: Hexagonal Modular + Eventos Asincrónicos
