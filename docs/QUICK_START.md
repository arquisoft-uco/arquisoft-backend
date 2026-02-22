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
- PostgreSQL: `localhost:5432` (7 schemas creados automáticamente)
- RabbitMQ: `localhost:5672` (UI: `localhost:15672`)
- Redis: `localhost:6379`
- Keycloak: `localhost:8081`
- Nextcloud: `localhost:8082`

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
shared/                          ← Componentes reutilizables (8 sub-módulos)
├── domain/                      ← DomainEvent, AggregateRoot
├── exceptions/                  ← DomainException base
├── amqp/                        ← EventPublisher (RabbitMQ)
├── postgres/                    ← BaseRepository (JPA)
├── redis/                       ← RedisClient
├── web/                         ← HttpClient
├── validation/                  ← @ValidEmail
├── notifications/               ← NotificationService
└── example/                     ← Ejemplo de referencia (leer primero)

seguridad/                       ← CONTEXTO 1: Autenticación/Autorización
├── domain/                      ← UserRole, JWT, CurrentUser ports
├── application/                 ← LoginDTO, AuthenticatedUserDTO
└── infrastructure/              ← SecurityConfig, AuthController, Keycloak

fichas/                          ← CONTEXTO 2: Fichas
proyectos/                       ← CONTEXTO 3: Proyectos
artefactos/                      ← CONTEXTO 4: Artefactos
repositorio_artefactos/          ← CONTEXTO 5: Repositorio Artefactos
entregables/                     ← CONTEXTO 6: Entregables
evaluaciones/                    ← CONTEXTO 7: Evaluaciones
```

---

## Acceso a Servicios

| Servicio | URL | Usuario | Contraseña |
|----------|-----|--------|-----------|
| **Backend** | http://localhost:8080/api | - | - |
| **RabbitMQ** | http://localhost:15672 | guest | guest |
| **Keycloak** | http://localhost:8081/admin | admin | admin |
| **Nextcloud** | http://localhost:8082 | admin | admin123 |

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

> Para un ejemplo completo con código, ver: `shared/example/README.md`

### 1. Definir Entidad en Domain

```java
// fichas/domain/src/main/java/com/arquisoft/fichas/domain/model/Ficha.java
package com.arquisoft.fichas.domain.model;

public class Ficha {
    private final Long id;
    private final String titulo;
    private final String descripcion;
    private final String areaConocimiento;

    private Ficha(Long id, String titulo, String descripcion, String areaConocimiento) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.areaConocimiento = areaConocimiento;
    }

    public static Ficha build(String titulo, String descripcion, String areaConocimiento) {
        return new Ficha(null, titulo, descripcion, areaConocimiento);
    }

    // Getters...
}
```

### 2. Definir Puerto de Entrada

```java
// fichas/domain/src/main/java/com/arquisoft/fichas/domain/port/in/CrearFichaUseCase.java
public interface CrearFichaUseCase {
    Ficha crearFicha(Ficha ficha);
}
```

### 3. Definir Puerto de Salida

```java
// fichas/domain/src/main/java/com/arquisoft/fichas/domain/port/out/FichaRepositoryPort.java
public interface FichaRepositoryPort {
    Ficha save(Ficha ficha);
    Optional<Ficha> findById(Long id);
    List<Ficha> findAll();
}
```

### 4. Implementar UseCase en Application

```java
// fichas/application/src/main/java/com/arquisoft/fichas/application/usecase/CrearFichaUseCaseImpl.java
@Component
@RequiredArgsConstructor
public class CrearFichaUseCaseImpl implements CrearFichaUseCase {
    private final FichaRepositoryPort fichaRepositoryPort;

    @Override
    public Ficha crearFicha(Ficha ficha) {
        return fichaRepositoryPort.save(ficha);
    }
}
```

### 5. Crear Controller en Infrastructure

```java
// fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/in/FichaController.java
@RestController
@RequestMapping("/api/fichas")
@RequiredArgsConstructor
public class FichaController {
    private final CrearFichaUseCase crearFichaUseCase;

    @PostMapping
    public ResponseEntity<FichaDTO> crear(@RequestBody FichaDTO dto) {
        Ficha ficha = crearFichaUseCase.crearFicha(dto.toDomain());
        return ResponseEntity.status(HttpStatus.CREATED).body(FichaDTO.fromDomain(ficha));
    }
}
```

### 6. Crear Repository Adapter en Infrastructure

```java
// fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/FichaRepositoryAdapter.java
@Repository
@RequiredArgsConstructor
public class FichaRepositoryAdapter implements FichaRepositoryPort {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Ficha save(Ficha ficha) {
        String sql = "INSERT INTO fichas.ficha (titulo, descripcion, area) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, ficha.getTitulo(), ficha.getDescripcion(), ficha.getAreaConocimiento());
        return ficha;
    }
}
```

### 7. Event Listener en Otro Contexto (Opcional)

```java
// proyectos/infrastructure/src/main/java/com/arquisoft/proyectos/infrastructure/adapter/in/ProyectosEventListener.java
@Component
@RequiredArgsConstructor
public class ProyectosEventListener {

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue("proyectos.fichas-events"),
        exchange = @Exchange("arquisoft.events"),
        key = "fichas.creada"
    ))
    public void onFichaCreada(String message) {
        // Procesar evento de ficha creada
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
- [ ] Leer la estructura de referencia (`shared/example/README.md`)
- [ ] Crear primer modelo de dominio en tu contexto
- [ ] Crear primer caso de uso
- [ ] Crear tests unitarios
- [ ] Crear evento de dominio (si aplica)

---

## Documentación Completa

- **README.md** — Documentación completa del proyecto
- **ARQUITECTURA_Y_ESTRUCTURA.md** — Arquitectura hexagonal y ejemplos
- **ARQUITECTURA_ASINCRONICO_ARQUISOFT.md** — Arquitectura asincrónica con eventos
- **shared/example/README.md** — Ejemplo de referencia con código completo

---

**Versión**: 1.0.0
