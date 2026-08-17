> [!WARNING]
> **SOLO LECTURA — NO USAR COMO CONTEXTO DE AGENTES O IA**
>
> Este archivo es documentación de referencia para desarrolladores humanos.
> **No debe ser leído ni indexado por agentes, asistentes de IA ni herramientas de generación de código.**
> El contexto autoritativo del proyecto para agentes reside exclusivamente en `AGENTS.md` (raíz del repositorio)
> y en los skills de `.opencode/skills/`. Usar este archivo como contexto puede producir código incorrecto,
> versiones desactualizadas o convenciones que no reflejan el estado real del proyecto.

# Guía de Inicio Rápido - Arquisoft Backend

Esta guía te ayudará a comenzar con el desarrollo del backend de Arquisoft basado en Arquitectura Hexagonal Modular.

## 5 Pasos para Empezar

### Paso 1: Clonar el Repositorio

```bash
git clone <url-del-repo>
cd arquisoft-backend
```

### Paso 2: Levantar Servicios con Docker Compose

```bash
docker-compose up -d
```

Espera ~30 segundos para que todos los servicios se inicien:
- PostgreSQL: `localhost:5432` (8 bases de datos creadas automáticamente, una por contexto con implementación o scaffolding, más `keycloak`)
- RabbitMQ: `localhost:5672` (UI: `localhost:15672`)
- Redis: `localhost:6379`
- Keycloak: `localhost:8081`
- MinIO: `localhost:9000` (API), `localhost:9001` (consola)

### Paso 3: Compilar el Proyecto

```bash
# Windows
.\gradlew.bat build

# Linux/Mac
chmod +x gradlew && ./gradlew build
```

### Paso 4: Ejecutar la Aplicación

```bash
# Con perfil de desarrollo (recomendado)
./gradlew bootRun --args='--spring.profiles.active=dev'
```

La aplicación estará disponible en: **http://localhost:8080/api**

### Paso 5: Verificar que Funciona

```bash
curl http://localhost:8080/api/actuator/health
```

---

## Estructura Rápida

```
shared/                          ← Componentes reutilizables (12 sub-módulos)
├── util/                        ← UtilText, UtilUUID, UtilCollection, UtilDate, UtilNumber, UtilObject
├── exception/                   ← BaseException/BaseError y las 5 excepciones base (DomainException, ...)
├── validation/                  ← DomainValidator, ValidationResult (Notification Pattern)
├── domain/                      ← DomainEvent, AggregateRoot
├── logger/                      ← AppLogger
├── redis/                       ← RedisClient
├── amqp/                        ← EventPublisher (RabbitMQ)
├── web/                         ← HttpClient, TraceIdFilter, GlobalAppExceptionHandler
├── minio/                       ← Cliente MinIO
├── postgres/                    ← BaseRepository (JPA)
├── message/                     ← CatalogoMensajes (catálogo de mensajes)
└── notification/                ← EnvioNotificacionOutputPort (SMTP)

seguridad/                       ← CONTEXTO: Autenticación/Autorización (sin DB propia)
├── domain/                      ← SesionDomain, TokenDomain, secondaryport/
├── application/                 ← AutenticarUsuarioCommand, primaryport/interactor, usecase
└── infrastructure/               ← SeguridadConfig, AutenticacionCommandController, Keycloak

usuarios/                        ← CONTEXTO: Usuarios (implementación real, mínima)
fichas/                          ← CONTEXTO: Fichas (implementación real, la más completa)
notificaciones/                  ← CONTEXTO: Notificaciones (implementación real)
proyectos/                       ← CONTEXTO: Proyectos (scaffolding, sin código de dominio aún)
artefactos/                      ← CONTEXTO: Artefactos (scaffolding)
repositorio_artefactos/          ← CONTEXTO: Repositorio Artefactos (scaffolding)
entregables/                     ← CONTEXTO: Entregables (scaffolding)
evaluaciones/                    ← CONTEXTO: Evaluaciones (scaffolding)
```

---

## Acceso a Servicios

| Servicio | URL | Usuario | Contraseña |
|----------|-----|--------|-----------|
| **Backend** | http://localhost:8080/api | - | - |
| **RabbitMQ** | http://localhost:15672 | guest | guest |
| **Keycloak** | http://localhost:8081/admin | admin | admin |
| **MinIO** | http://localhost:9001 | minioadmin | minioadmin |

---

## Comandos Útiles

```bash
# Compilar
./gradlew build

# Ejecutar (desarrollo)
./gradlew bootRun --args='--spring.profiles.active=dev'

# Tests
./gradlew test

# Compilar un módulo específico
./gradlew fichas:build

# Ver estructura de módulos
./gradlew projects

# Limpiar caché
./gradlew clean
```

---

## Crear un Nuevo Caso de Uso (Ejemplo: Fichas)

### 1. Definir el Aggregate Root en Domain

```java
// fichas/domain/src/main/java/com/arquisoft/fichas/domain/ficha/FichaDomain.java
package com.arquisoft.fichas.domain.ficha;

import com.arquisoft.shared.events.AggregateRoot;
import com.arquisoft.shared.validation.DomainValidator;
import com.arquisoft.shared.validation.ValidationResult;

import java.util.UUID;

public final class FichaDomain extends AggregateRoot {
    private UUID id;
    private String titulo;
    private String areaConocimiento;

    private FichaDomain() {}

    // Factory para NUEVA ficha — valida invariantes
    public static FichaDomain crear(String titulo, String areaConocimiento) {
        var result = new ValidationResult();
        DomainValidator.noEnBlanco(titulo, "titulo", "FICHA_TITULO_REQUERIDO", result);
        result.lanzarSiTieneErrores();

        var ficha = new FichaDomain();
        ficha.id = UUID.randomUUID();
        ficha.titulo = titulo;
        ficha.areaConocimiento = areaConocimiento;
        return ficha;
    }

    // Factory para RECONSTRUIR desde persistencia — dato ya confiable, sin re-validar
    public static FichaDomain reconstruir(UUID id, String titulo, String areaConocimiento) {
        var ficha = new FichaDomain();
        ficha.id = id;
        ficha.titulo = titulo;
        ficha.areaConocimiento = areaConocimiento;
        return ficha;
    }

    public UUID getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAreaConocimiento() { return areaConocimiento; }
}
```

### 2. Definir el Puerto de Salida en Domain

```java
// fichas/domain/src/main/java/com/arquisoft/fichas/domain/ficha/secondaryport/FichaOutputPort.java
package com.arquisoft.fichas.domain.ficha.secondaryport;

import com.arquisoft.fichas.domain.ficha.FichaDomain;

import java.util.UUID;

public interface FichaOutputPort {
    void guardar(FichaDomain ficha);
    boolean existePorId(UUID id);
}
```

### 3. Definir el Command en Application

```java
// fichas/application/src/main/java/com/arquisoft/fichas/application/ficha/command/primaryport/model/CrearFichaCommand.java
package com.arquisoft.fichas.application.ficha.command.primaryport.model;

public record CrearFichaCommand(String titulo, String areaConocimiento) {}
```

### 4. Implementar el UseCase (colaborador interno, sin transacción)

```java
// fichas/application/src/main/java/com/arquisoft/fichas/application/ficha/command/usecase/impl/CrearFichaUseCaseImpl.java
@Component
@RequiredArgsConstructor
public class CrearFichaUseCaseImpl implements CrearFichaUseCase {
    private final FichaOutputPort fichaOutputPort;

    @Override
    public UUID ejecutar(FichaDomain ficha) {
        fichaOutputPort.guardar(ficha);
        return ficha.getId();
    }
}
```

### 5. Implementar el Interactor (entry point, dueño de la transacción)

```java
// fichas/application/src/main/java/com/arquisoft/fichas/application/ficha/command/primaryport/interactor/impl/CrearFichaInteractorImpl.java
@Component
@RequiredArgsConstructor
public class CrearFichaInteractorImpl implements CrearFichaInteractor {
    private final CrearFichaUseCase crearFichaUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(CrearFichaCommand command) {
        FichaDomain ficha = FichaDomain.crear(command.titulo(), command.areaConocimiento());
        return crearFichaUseCase.ejecutar(ficha);
    }
}
```

### 6. Crear el Controller en Infrastructure

```java
// fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/ficha/command/primaryadapter/web/FichaController.java
@RestController
@RequestMapping(FichasRoutes.FICHAS)
@RequiredArgsConstructor
public class FichaController {
    private final CrearFichaInteractor crearFichaInteractor; // se inyecta el Interactor, nunca el UseCase

    @PostMapping
    @PreAuthorize(FichasAuthorities.Expresiones.HAS_ASESOR)
    public ResponseEntity<Void> crear(@Valid @RequestBody CrearFichaRequestDTO dto) {
        UUID id = crearFichaInteractor.ejecutar(CrearFichaRequestMapper.toCommand(dto));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
```

### 7. Crear el Output Adapter en Infrastructure

```java
// fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/ficha/command/secondaryadapter/repository/FichaCommandOutputAdapter.java
@Component
@RequiredArgsConstructor
public class FichaCommandOutputAdapter implements FichaOutputPort {
    private final FichaRepository fichaRepository; // Spring Data JPA
    private final FichaMapper fichaMapper;

    @Override
    public void guardar(FichaDomain ficha) {
        fichaRepository.save(fichaMapper.toEntity(ficha));
    }

    @Override
    public boolean existePorId(UUID id) {
        return fichaRepository.existsById(id);
    }
}
```

### 8. Consumidor de eventos AMQP en Otro Contexto (Opcional)

```java
// proyectos/infrastructure/src/main/java/com/arquisoft/proyectos/infrastructure/ficha/command/primaryadapter/amqp/FichaCreadaInputAdapter.java
@Component
@RequiredArgsConstructor
public class FichaCreadaInputAdapter {

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue("proyectos.fichas-events"),
        exchange = @Exchange("arquisoft.events"),
        key = "fichas.creada"
    ))
    public void onFichaCreada(FichaCreadaEvent evento) {
        // Sincronizar copia local del concepto en el contexto proyectos
    }
}
```

---

## Solucionar Problemas

### "Connection refused" en BD

```bash
docker ps | grep postgres
docker-compose up -d postgres
```

### "Cannot get a connection, pool error"

Espera un minuto para que PostgreSQL esté listo, luego reinicia la aplicación.

### RabbitMQ no responde

```bash
docker-compose restart rabbitmq
```

### Puertos ocupados

Modifica `docker-compose.yml`:
```yaml
ports:
  - "5433:5432"  # Cambiar puerto local
```

---

## Checklist de Desarrollo

- [ ] Clonar el repositorio
- [ ] Levantar Docker Compose
- [ ] Compilar el proyecto
- [ ] Ejecutar la aplicación con perfil `dev`
- [ ] Verificar health check (GET /api/actuator/health)
- [ ] Crear primer modelo de dominio en tu contexto
- [ ] Crear primer caso de uso
- [ ] Crear tests unitarios
- [ ] Crear evento de dominio (si aplica)

---

## Documentación Completa

- **README.md** — Documentación completa del proyecto
- **ARQUITECTURA_Y_ESTRUCTURA.md** — Arquitectura hexagonal y ejemplos
- **ARQUITECTURA_ASINCRONICO_ARQUISOFT.md** — Arquitectura asincrónica con eventos


---

**Versión**: 1.0.0
