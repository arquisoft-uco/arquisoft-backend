# Arquitectura Hexagonal Modular - Documentación Completa

## Índice
1. [Visión General](#visión-general)
2. [Arquitectura Hexagonal (Puertos y Adaptadores)](#arquitectura-hexagonal-puertos-y-adaptadores)
3. [Estructura del Proyecto](#estructura-del-proyecto)
4. [Módulos: Ventas y Seguridad](#módulos-ventas-y-seguridad)
5. [Ejemplos Prácticos](#ejemplos-prácticos)
6. [Configuración y Build](#configuración-y-build)
7. [Flujos de Datos](#flujos-de-datos)

---

## Visión General

Este proyecto implementa una **Arquitectura Hexagonal Modular** usando **Spring Boot 3.2.4** y **Gradle** como herramienta de construcción. La arquitectura se basa en el patrón de **Puertos y Adaptadores**, también conocido como arquitectura hexagonal, que proporciona:

### Ventajas Principales
- **Independencia de frameworks**: La lógica de negocio es agnóstica a tecnologías externas
- **Modularidad**: Cada dominio (ventas, seguridad) es completamente independiente
- **Testabilidad**: Las dependencias se invierten, facilitando pruebas unitarias
- **Escalabilidad**: Los módulos pueden crecer sin afectar otros
- **Mantenibilidad**: El código está organizado por responsabilidades claras

---

## Arquitectura Hexagonal (Puertos y Adaptadores)

### Concepto Fundamental

La arquitectura hexagonal divide una aplicación en tres capas principales:

```
┌─────────────────────────────────────────┐
│      ADAPTADORES DE ENTRADA (IN)        │
│  Controllers, REST APIs, Event Listeners│
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│        PUERTOS DE ENTRADA (IN)          │
│   Use Cases, Interfaces de Negocio      │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│      DOMINIO - NÚCLEO DEL NEGOCIO       │
│    Entidades, Modelos, Lógica Pura      │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│        PUERTOS DE SALIDA (OUT)          │
│   Repository Ports, Service Ports       │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│     ADAPTADORES DE SALIDA (OUT)         │
│  Repositorios, Servicios Externos       │
└─────────────────────────────────────────┘
```

### Componentes Clave

#### 1. **Dominio (Domain)**
- Contiene la lógica de negocio pura
- Modelos (entidades)
- Puertos (interfaces) que definen contratos
- **Sin dependencias** a frameworks o bases de datos

#### 2. **Puertos de Entrada (In)**
- Interfaces que definen casos de uso
- Métodos que la aplicación debe ofrecer
- Ejemplo: `CreateTaskUseCase`, `RetrieveTaskUseCase`

#### 3. **Puertos de Salida (Out)**
- Interfaces que definen dependencias externas
- Ejemplo: `TaskRepositoryPort`, `ExternalServicePort`
- La implementación real viene de los adaptadores

#### 4. **Aplicación (Application)**
- Implementa los puertos de entrada (casos de uso)
- Orquesta la lógica de negocio
- Se comunica con puertos de salida (inyección de dependencias)
- Contiene DTOs para transformación de datos

#### 5. **Infraestructura (Infrastructure)**
- Implementa los puertos de salida
- Contiene adaptadores de entrada (Controllers)
- Gestiona bases de datos, APIs externas, etc.
- Configuración de Spring Boot

---

## Estructura del Proyecto

```
base-hexagonal-modular/
│
├── build.gradle                          # Configuración principal de Gradle
├── settings.gradle                       # Definición de módulos
├── gradle.properties                     # Versiones de dependencias
├── docker-compose.yml                    # Orquestación de contenedores
│
├── gradle/
│   └── wrapper/                          # Gradle Wrapper
│
├── src/
│   ├── main/
│   │   ├── java/com/demo/
│   │   │   └── MainApplication.java      # Punto de entrada Spring Boot
│   │   └── resources/
│   │       └── application.yml           # Configuración global
│   └── test/
│
├── ventas/                               # MÓDULO 1: Gestión de Ventas
│   ├── build.gradle
│   ├── domain/                           # Capa de Dominio
│   │   ├── build.gradle
│   │   └── src/main/java/com/demo/domain/
│   │       ├── model/
│   │       │   ├── Task.java             # Entidad de negocio
│   │       │   └── AdditionalTaskInfo.java
│   │       └── port/
│   │           ├── in/                   # Puertos de entrada (casos de uso)
│   │           │   ├── CreateTaskUseCase.java
│   │           │   ├── RetrieveTaskUseCase.java
│   │           │   ├── UpdateTaskUseCase.java
│   │           │   ├── DeleteTaskUseCase.java
│   │           │   └── GetAdditionalTaskInfoUseCase.java
│   │           └── out/                  # Puertos de salida (dependencias)
│   │               ├── TaskRepositoryPort.java
│   │               └── ExternalServicePort.java
│   │
│   ├── application/                      # Capa de Aplicación
│   │   ├── build.gradle
│   │   └── src/main/java/com/demo/application/
│   │       ├── dto/
│   │       │   └── TaskDTO.java          # Data Transfer Object
│   │       ├── usecase/                  # Implementación de casos de uso
│   │       │   ├── CreateTaskUseCaseImpl.java
│   │       │   ├── RetrieveTaskUseCaseImpl.java
│   │       │   ├── UpdateTaskUseCaseImpl.java
│   │       │   ├── DeleteTaskUseCaseImpl.java
│   │       │   └── GetAdditionalTaskInfoUseCaseImpl.java
│   │       ├── facade/
│   │       │   └── TaskFacade.java       # Fachada unificada
│   │       └── service/                  # Servicios de aplicación
│   │
│   └── infrastructure/                   # Capa de Infraestructura
│       ├── build.gradle
│       └── src/main/java/com/demo/infrastructure/
│           ├── adapter/
│           │   ├── in/
│           │   │   └── TaskController.java      # Adaptador de entrada (REST)
│           │   └── out/
│           │       ├── TaskRepositoryAdapter.java   # Implementa TaskRepositoryPort
│           │       └── ExternalServiceAdapter.java  # Implementa ExternalServicePort
│           ├── common/
│           │   └── SqlLoader.java        # Utilidades comunes
│           └── resources/
│               ├── db/migration/         # Flyway migrations
│               │   └── V1.0__schema.sql
│               └── sql/
│                   └── getAllTasks.sql
│
└── seguridad/                            # MÓDULO 2: Gestión de Seguridad
    ├── build.gradle
    ├── dominio/                          # Capa de Dominio
    │   ├── build.gradle
    │   └── src/main/java/com/demo/seguridad/
    │       ├── modelo/
    │       │   ├── Usuario.java
    │       │   ├── Rol.java
    │       │   ├── Permiso.java
    │       │   ├── Organizacion.java
    │       │   ├── EstadoUsuario.java
    │       │   └── EstadoPermiso.java
    │       ├── puerto/entrada/          # Puertos de entrada
    │       │   ├── usuario/
    │       │   └── rol/
    │       └── puerto/salida/           # Puertos de salida
    │           ├── usuario/
    │           ├── rol/
    │           ├── permiso/
    │           ├── credencial/
    │           └── aplicacion/
    │
    ├── aplicacion/                       # Capa de Aplicación
    │   ├── build.gradle
    │   └── src/main/java/com/demo/seguridad/
    │       ├── dto/
    │       ├── servicio/
    │       └── fachada/
    │
    └── infraestructura/                  # Capa de Infraestructura
        ├── build.gradle
        └── src/main/java/com/demo/seguridad/
            ├── adaptador/
            │   ├── in/
            │   └── out/
            └── comun/
```

### Convenciones de Nomenclatura

| Capa | Nomenclatura (Español) | Nomenclatura (English) |
|------|------------------------|----------------------|
| **Domain** | `modelo/`, `puerto/entrada`, `puerto/salida` | `model/`, `port/in`, `port/out` |
| **Application** | `dto/`, `servicio/`, `fachada/`, `casouso/` | `dto/`, `service/`, `facade/`, `usecase/` |
| **Infrastructure** | `adaptador/`, `comun/` | `adapter/`, `common/` |

**Nota**: El proyecto utiliza ambas convenciones (español para `seguridad`, inglés para `ventas`) como ejemplo de flexibilidad.

---

## Módulos: Ventas y Seguridad

### Módulo Ventas (English Convention)

#### Estructura Modular
```
ventas/
├── domain          # Independiente
├── application     # Depende de domain
└── infrastructure  # Depende de application y domain
```

#### Dependencias Gradle
```gradle
// ventas:application depende de ventas:domain
dependencies {
    implementation project(':ventas:domain')
}

// ventas:infrastructure depende de application y domain
dependencies {
    implementation project(':ventas:domain')
    implementation project(':ventas:application')
    implementation "org.springframework.boot:spring-boot-starter-jdbc:${springBootVersion}"
    implementation "org.flywaydb:flyway-core:${flywaydbVersion}"
    runtimeOnly 'org.postgresql:postgresql:42.7.2'
}
```

#### Tecnologías
- **Base de Datos**: PostgreSQL (producción) / H2 (testing)
- **Migraciones**: Flyway
- **ORM**: JdbcTemplate (bajo nivel, control explícito)

---

### Módulo Seguridad (Spanish Convention)

#### Estructura Modular
```
seguridad/
├── dominio         # Independiente
├── aplicacion      # Depende de dominio
└── infraestructura # Depende de aplicacion y dominio
```

#### Conceptos de Dominio
- **Usuario**: Entidad principal del sistema
- **Rol**: Agrupación de permisos
- **Permiso**: Acciones autorizadas
- **Organizacion**: Contexto empresarial
- **Credencial**: Autenticación (usuario/contraseña)

#### Flujos Principales
1. **Registro de Administrador** → `RegistrarAdministradorImpl`
2. **Gestión de Usuarios** → Activar, Inactivar, Modificar, Listar
3. **Asignación de Roles** → `AsignarRol`
4. **Gestión de Permisos** → Control granular de acceso

---

## Ejemplos Prácticos

### Ejemplo 1: Crear una Tarea (Módulo Ventas)

#### Flujo Completo

```
HTTP Request
    ↓
TaskController.createTask() [Adapter IN]
    ↓
TaskFacade.createTask() [Facade]
    ↓
CreateTaskUseCaseImpl.createTask() [Use Case Implementation]
    ↓
TaskRepositoryPort.save() [Port OUT - Interface]
    ↓
TaskRepositoryAdapter.save() [Adapter OUT - Implementation]
    ↓
JdbcTemplate.update() [Database]
    ↓
HTTP Response
```

#### Código Completo

**1. Definir la Entidad en Domain**
```java
// ventas/domain/src/main/java/com/demo/domain/model/Task.java
package com.demo.domain.model;

import java.time.LocalDateTime;

public class Task {
    private final Long id;
    private final String title;
    private final String description;
    private final LocalDateTime creationDate;
    private final boolean completed;

    private Task(Long id, String title, String description, LocalDateTime creationDate, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.creationDate = creationDate;
        this.completed = completed;
    }

    // Factory method para crear nuevas tareas
    public static Task build(Long id, String title, String description, LocalDateTime creationDate, boolean completed) {
        return new Task(id, title, description, creationDate, completed);
    }

    // Factory method para reconstruir desde persistencia
    public static Task rebuild(Long id, String title, String description, LocalDateTime creationDate, boolean completed) {
        return new Task(id, title, description, creationDate, completed);
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getCreationDate() { return creationDate; }
    public boolean isCompleted() { return completed; }
}
```

**2. Definir Puertos en Domain**

```java
// ventas/domain/src/main/java/com/demo/domain/port/in/CreateTaskUseCase.java
package com.demo.domain.port.in;

import com.demo.domain.model.Task;

public interface CreateTaskUseCase {
    Task createTask(Task task);
}
```

```java
// ventas/domain/src/main/java/com/demo/domain/port/out/TaskRepositoryPort.java
package com.demo.domain.port.out;

import com.demo.domain.model.Task;
import java.util.List;
import java.util.Optional;

public interface TaskRepositoryPort {
    Task save(Task task);
    Optional<Task> findById(Long id);
    List<Task> findAll();
    Optional<Task> update(Task task);
    boolean deleteById(Long id);
}
```

**3. Implementar en Application**

```java
// ventas/application/src/main/java/com/demo/application/usecase/CreateTaskUseCaseImpl.java
package com.demo.application.usecase;

import com.demo.domain.model.Task;
import com.demo.domain.port.in.CreateTaskUseCase;
import com.demo.domain.port.out.TaskRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateTaskUseCaseImpl implements CreateTaskUseCase {
    private final TaskRepositoryPort taskRepositoryPort;

    @Override
    public Task createTask(Task task) {
        // Aquí iría lógica de validación/negocio
        return taskRepositoryPort.save(task);
    }
}
```

**4. Crear DTO para transferencia de datos**

```java
// ventas/application/src/main/java/com/demo/application/dto/TaskDTO.java
package com.demo.application.dto;

import com.demo.domain.model.Task;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime creationDate;
    private boolean completed;

    // Transformar DTO a entidad de dominio
    public Task toDomain() {
        return Task.build(id, title, description, creationDate, completed);
    }
}
```

**5. Crear Fachada en Application**

```java
// ventas/application/src/main/java/com/demo/application/facade/TaskFacade.java
package com.demo.application.facade;

import com.demo.domain.model.Task;
import com.demo.domain.port.in.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskFacade implements CreateTaskUseCase, RetrieveTaskUseCase, 
                                    UpdateTaskUseCase, DeleteTaskUseCase,
                                    GetAdditionalTaskInfoUseCase {

    private final CreateTaskUseCase createTaskUseCase;
    private final RetrieveTaskUseCase retrieveTaskUseCase;
    // ... otros casos de uso

    @Override
    public Task createTask(Task task) {
        return createTaskUseCase.createTask(task);
    }

    @Override
    public Optional<Task> getTask(Long id) {
        return retrieveTaskUseCase.getTask(id);
    }

    // ... otros métodos
}
```

**6. Implementar Adaptador en Infrastructure**

```java
// ventas/infrastructure/src/main/java/com/demo/infrastructure/adapter/out/TaskRepositoryAdapter.java
package com.demo.infrastructure.adapter.out;

import com.demo.domain.model.Task;
import com.demo.domain.port.out.TaskRepositoryPort;
import com.demo.infrastructure.common.SqlLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TaskRepositoryAdapter implements TaskRepositoryPort {
    private final SqlLoader sqlLoader;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Task save(Task task) {
        String sql = "INSERT INTO tasks (title, description, creation_date, completed) " +
                     "VALUES (?, ?, ?, ?)";
        
        jdbcTemplate.update(sql, 
            task.getTitle(),
            task.getDescription(),
            task.getCreationDate(),
            task.isCompleted()
        );
        
        return task;
    }

    @Override
    public Optional<Task> findById(Long id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        RowMapper<Task> rowMapper = (rs, rowNum) -> Task.rebuild(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getTimestamp("creation_date").toLocalDateTime(),
            rs.getBoolean("completed")
        );
        
        return Optional.ofNullable(
            jdbcTemplate.queryForObject(sql, rowMapper, id)
        );
    }

    @Override
    public List<Task> findAll() {
        String sql = sqlLoader.getSqlQuery("getAllTasks.sql");
        RowMapper<Task> rowMapper = (rs, rowNum) -> Task.rebuild(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getTimestamp("creation_date").toLocalDateTime(),
            rs.getBoolean("completed")
        );
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public Optional<Task> update(Task task) {
        String sql = "UPDATE tasks SET title = ?, description = ?, completed = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql,
            task.getTitle(),
            task.getDescription(),
            task.isCompleted(),
            task.getId()
        );
        
        return rowsAffected > 0 ? Optional.of(task) : Optional.empty();
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }
}
```

**7. Crear Controlador en Infrastructure**

```java
// ventas/infrastructure/src/main/java/com/demo/infrastructure/adapter/in/TaskController.java
package com.demo.infrastructure.adapter.in;

import com.demo.application.dto.TaskDTO;
import com.demo.application.facade.TaskFacade;
import com.demo.domain.model.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskFacade taskFacade;

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody TaskDTO task) {
        Task createdTask = taskFacade.createTask(task.toDomain());
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long taskId) {
        return ResponseEntity.of(taskFacade.getTask(taskId));
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskFacade.getAllTasks());
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(@PathVariable Long taskId, @RequestBody TaskDTO task) {
        return ResponseEntity.of(taskFacade.updateTask(taskId, task.toDomain()));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long taskId) {
        if (taskFacade.deleteTask(taskId)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/{taskId}/additionalInfo")
    public ResponseEntity<AdditionalTaskInfo> getAdditionalInfo(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskFacade.getAdditionalTaskInfo(taskId));
    }
}
```

#### Request HTTP Ejemplo
```bash
curl -X POST http://localhost:8081/api/task \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Implementar módulo de reportes",
    "description": "Crear reportes de ventas mensuales",
    "completed": false
  }'
```

#### Response
```json
{
  "id": 1,
  "title": "Implementar módulo de reportes",
  "description": "Crear reportes de ventas mensuales",
  "creationDate": "2026-01-08T10:30:00",
  "completed": false
}
```

---

### Ejemplo 2: Testabilidad - Test Unitario

Un gran beneficio de la arquitectura hexagonal es que los tests son simples:

```java
// ventas/application/src/test/java/com/demo/application/usecase/CreateTaskUseCaseImplTest.java
package com.demo.application.usecase;

import com.demo.domain.model.Task;
import com.demo.domain.port.out.TaskRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTaskUseCaseImplTest {

    private CreateTaskUseCaseImpl createTaskUseCase;

    @Mock
    private TaskRepositoryPort taskRepositoryPort;

    @BeforeEach
    void setUp() {
        createTaskUseCase = new CreateTaskUseCaseImpl(taskRepositoryPort);
    }

    @Test
    void shouldCreateTaskSuccessfully() {
        // Arrange
        Task taskToCreate = Task.build(
            null,
            "Nueva tarea",
            "Descripción",
            LocalDateTime.now(),
            false
        );

        Task savedTask = Task.build(
            1L,
            "Nueva tarea",
            "Descripción",
            LocalDateTime.now(),
            false
        );

        when(taskRepositoryPort.save(taskToCreate)).thenReturn(savedTask);

        // Act
        Task result = createTaskUseCase.createTask(taskToCreate);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Nueva tarea", result.getTitle());
    }
}
```

**Ventajas del Testing**:
- El mock de `TaskRepositoryPort` es simple
- La lógica de negocio se prueba aislada
- No requiere base de datos real

---

### Ejemplo 3: Agregar una Nueva Funcionalidad

Supongamos que queremos agregar una **búsqueda de tareas por título**:

#### Paso 1: Agregar Puerto de Entrada en Domain
```java
// ventas/domain/src/main/java/com/demo/domain/port/in/SearchTasksUseCase.java
package com.demo.domain.port.in;

import com.demo.domain.model.Task;
import java.util.List;

public interface SearchTasksUseCase {
    List<Task> searchByTitle(String title);
}
```

#### Paso 2: Actualizar Puerto de Salida en Domain
```java
// En TaskRepositoryPort, agregar:
List<Task> findByTitle(String title);
```

#### Paso 3: Implementar en Application
```java
// ventas/application/src/main/java/com/demo/application/usecase/SearchTasksUseCaseImpl.java
package com.demo.application.usecase;

import com.demo.domain.model.Task;
import com.demo.domain.port.in.SearchTasksUseCase;
import com.demo.domain.port.out.TaskRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchTasksUseCaseImpl implements SearchTasksUseCase {
    private final TaskRepositoryPort taskRepositoryPort;

    @Override
    public List<Task> searchByTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        return taskRepositoryPort.findByTitle(title);
    }
}
```

#### Paso 4: Implementar en Infraestructura
```java
// En TaskRepositoryAdapter, agregar:
@Override
public List<Task> findByTitle(String title) {
    String sql = "SELECT * FROM tasks WHERE LOWER(title) LIKE LOWER(?)";
    RowMapper<Task> rowMapper = (rs, rowNum) -> Task.rebuild(
        rs.getLong("id"),
        rs.getString("title"),
        rs.getString("description"),
        rs.getTimestamp("creation_date").toLocalDateTime(),
        rs.getBoolean("completed")
    );
    return jdbcTemplate.query(sql, rowMapper, "%" + title + "%");
}
```

#### Paso 5: Agregar Endpoint en Controlador
```java
// En TaskController, agregar:
@GetMapping("/search")
public ResponseEntity<List<Task>> searchTasks(@RequestParam String title) {
    List<Task> tasks = taskFacade.searchByTitle(title);
    return ResponseEntity.ok(tasks);
}
```

#### Paso 6: Actualizar Fachada
```java
// En TaskFacade, agregar:
private final SearchTasksUseCase searchTasksUseCase;

@Override
public List<Task> searchByTitle(String title) {
    return searchTasksUseCase.searchByTitle(title);
}
```

**Request HTTP**:
```bash
curl http://localhost:8081/api/task/search?title=reportes
```

---

## Configuración y Build

### Versiones de Dependencias (gradle.properties)
```properties
javaVersion=17
springBootVersion=3.2.4
jUnitVersion=5.10.2
lombokVersion=1.18.30
h2Version=2.2.224
flywaydbVersion=10.10.0
```

### Configuración Spring Boot (application.yml)
```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres
    username: admin
    password: admin

logging:
  level:
    com.demo: INFO
    org.hibernate.SQL: DEBUG
```

### Construir el Proyecto
```bash
# Build completo
./gradlew build

# Build sin tests
./gradlew build -x test

# Ejecutar tests
./gradlew test

# Run la aplicación
./gradlew bootRun
```

### Docker Compose
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: postgres
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: admin
    ports:
      - "5432:5432"
```

**Ejecutar**:
```bash
docker-compose up -d
```

---

## Flujos de Datos

### Flujo de Lectura (GET /api/task/1)

```
TaskController.getTaskById(1)
    ↓
TaskFacade.getTask(1)
    ↓
RetrieveTaskUseCaseImpl.getTask(1)
    ↓
TaskRepositoryPort.findById(1)  [interface]
    ↓
TaskRepositoryAdapter.findById(1)  [implementation]
    ↓
JdbcTemplate.queryForObject()
    ↓
Mapeo SQL → Task (entidad)
    ↓
Task [dominio]
    ↓
HTTP Response 200 OK
```

### Flujo de Escritura (POST /api/task)

```
HTTP Request [JSON]
    ↓
TaskController.createTask(TaskDTO)
    ↓
TaskDTO.toDomain() → Task
    ↓
TaskFacade.createTask(Task)
    ↓
CreateTaskUseCaseImpl.createTask(Task)
    ↓ [Validaciones de negocio aquí]
    ↓
TaskRepositoryPort.save(Task)  [interface]
    ↓
TaskRepositoryAdapter.save(Task)  [implementation]
    ↓
JdbcTemplate.update()
    ↓
Insert en BD
    ↓
Task [actualizado]
    ↓
HTTP Response 201 CREATED
```

### Flujo de Eliminación (DELETE /api/task/1)

```
TaskController.deleteTaskById(1)
    ↓
TaskFacade.deleteTask(1)
    ↓
DeleteTaskUseCaseImpl.deleteTask(1)
    ↓
TaskRepositoryPort.deleteById(1)
    ↓
TaskRepositoryAdapter.deleteById(1)
    ↓
JdbcTemplate.update("DELETE FROM tasks WHERE id = ?")
    ↓
Verificar rowsAffected > 0
    ↓
HTTP Response 204 NO CONTENT (éxito)
HTTP Response 404 NOT FOUND (no existía)
```

---

## Principios de Diseño Aplicados

### 1. **Separación de Responsabilidades**
- Cada capa tiene una responsabilidad clara
- Domain: Lógica pura
- Application: Orquestación
- Infrastructure: Detalles técnicos

### 2. **Inversión de Dependencias**
```
sin hexagonal:  Controller → Repository → Database
con hexagonal:  Controller → Port (interface) ← Repository
```

### 3. **Independencia de Frameworks**
- El dominio NO importa Spring
- Los casos de uso son POJOs
- Fácil cambiar de BD, framework, etc.

### 4. **Testabilidad**
- Mocks simples de puertos
- Lógica de negocio aislada
- Tests rápidos y confiables

### 5. **Escalabilidad Modular**
- Nuevos módulos sin afectar existentes
- Cada módulo es un mini-proyecto
- Reutilización de patrones

---

## Checklist para Agregar un Nuevo Módulo

1. **Crear estructura básica**
   ```
   nuevo_modulo/
   ├── domain/build.gradle
   ├── application/build.gradle
   └── infrastructure/build.gradle
   ```

2. **Definir entidades en domain**
   - Crear modelo (entidad)
   - Usar factory methods (`build`, `rebuild`)

3. **Definir puertos**
   - Puertos IN: casos de uso (interfaces)
   - Puertos OUT: dependencias (interfaces)

4. **Implementar casos de uso en application**
   - Una clase por caso de uso
   - Inyectar puertos de salida
   - Orquestar lógica de negocio

5. **Crear adaptadores en infrastructure**
   - Adaptadores IN: Controllers
   - Adaptadores OUT: Repositories, External Services
   - Configuración de Spring

6. **Registrar en settings.gradle**
   ```gradle
   include 'nuevo_modulo'
   include 'nuevo_modulo:domain'
   include 'nuevo_modulo:application'
   include 'nuevo_modulo:infrastructure'
   ```

7. **Agregar dependencias en build.gradle raíz**
   ```gradle
   subprojects.each { subproject ->
       if (subproject.name.endsWith('infrastructure')) {
           implementation project(subproject.path)
       }
   }
   ```

---

## Resumen Arquitectónico

| Aspecto | Descripción |
|--------|-------------|
| **Patrón** | Arquitectura Hexagonal (Puertos y Adaptadores) |
| **Modularidad** | Multi-módulo Gradle (ventas, seguridad) |
| **Capas** | Domain → Application → Infrastructure |
| **Framework** | Spring Boot 3.2.4 |
| **BD** | PostgreSQL (producción) / H2 (testing) |
| **Migraciones** | Flyway |
| **ORM** | JdbcTemplate (bajo nivel) |
| **Build** | Gradle 7+ con Wrapper |
| **Java** | 17+ |
| **Testing** | JUnit 5 + Mockito |
| **Inyección Depencias** | Spring (@Component, @Service) |

---

## Referencias y Lecturas Recomendadas

- **Arquitectura Hexagonal**: https://alistair.cockburn.us/hexagonal-architecture/
- **Domain-Driven Design**: Eric Evans, "Domain-Driven Design" (2003)
- **Clean Architecture**: Robert C. Martin, "Clean Architecture" (2017)
- **Spring Documentation**: https://spring.io/projects/spring-boot

---

**Documento generado**: Enero 8, 2026  
**Versión del Proyecto**: 0.0.1-SNAPSHOT  
**Autor**: Análisis Automatizado
