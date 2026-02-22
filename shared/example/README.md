# Ejemplo de Referencia — Estructura Hexagonal por Contexto

Este documento describe la estructura estándar que debe seguir cada bounded context en el proyecto Arquisoft.
Su objetivo es servir como **guía viva**: no solo muestra *dónde* va cada archivo, sino *qué hace* cada clase y *por qué* es imprescindible dentro de la Arquitectura Hexagonal.

---

## ¿Por qué Arquitectura Hexagonal?

La Arquitectura Hexagonal (también llamada *Puertos y Adaptadores*) resuelve un problema clásico: **el acoplamiento entre la lógica de negocio y los detalles técnicos**.

Sin ella, un cambio en la base de datos o en el framework HTTP obliga a modificar reglas de negocio. Con ella, cada capa tiene un contrato claro:

```
┌─────────────────────────────────────────────────┐
│         INFRASTRUCTURE (detalles técnicos)      │
│  Controllers, Repos, Configs, Filtros, BD       │
│                                                 │
│    ┌───────────────────────────────────────┐     │
│    │       APPLICATION (orquestación)      │     │
│    │  DTOs, UseCaseImpl                    │     │
│    │                                       │     │
│    │    ┌─────────────────────────────┐     │     │
│    │    │      DOMAIN (núcleo)        │     │     │
│    │    │  Entidades, Puertos,        │     │     │
│    │    │  Excepciones de negocio     │     │     │
│    │    └─────────────────────────────┘     │     │
│    └───────────────────────────────────────┘     │
└─────────────────────────────────────────────────┘
```

**La regla de oro**: las flechas de dependencia siempre apuntan hacia adentro. El dominio no sabe que existe Spring, PostgreSQL ni RabbitMQ.

---

## Estructura de un Contexto

Cada contexto de negocio sigue la arquitectura con tres sub-módulos Gradle:

```
{contexto}/
├── build.gradle                          # Configuración automática del padre
├── domain/                               # CAPA DE DOMINIO (lógica pura)
│   ├── build.gradle
│   └── src/
│       ├── main/java/com/arquisoft/{contexto}/domain/
│       │   ├── model/                    # Entidades y Value Objects
│       │   │   └── {Entidad}.java
│       │   ├── port/
│       │   │   ├── in/                   # Puertos de entrada (interfaces de casos de uso)
│       │   │   │   └── {Accion}{Entidad}UseCase.java
│       │   │   └── out/                  # Puertos de salida (interfaces de repositorios)
│       │   │       └── {Entidad}RepositoryPort.java
│       │   └── exception/               # Excepciones de dominio (si aplica)
│       │       └── {Entidad}Exception.java
│       └── test/java/com/arquisoft/{contexto}/domain/
│           └── model/
│               └── {Entidad}Test.java
│
├── application/                          # CAPA DE APLICACIÓN (orquestación)
│   ├── build.gradle
│   └── src/
│       ├── main/java/com/arquisoft/{contexto}/application/
│       │   ├── dto/                      # Data Transfer Objects
│       │   │   └── {Entidad}DTO.java
│       │   └── usecase/                  # Implementaciones de casos de uso
│       │       └── {Accion}{Entidad}UseCaseImpl.java
│       └── test/java/com/arquisoft/{contexto}/application/
│           └── usecase/
│               └── {Accion}{Entidad}UseCaseImplTest.java
│
└── infrastructure/                       # CAPA DE INFRAESTRUCTURA (detalles técnicos)
    ├── build.gradle
    └── src/
        ├── main/
        │   ├── java/com/arquisoft/{contexto}/infrastructure/
        │   │   ├── adapter/
        │   │   │   ├── in/               # Adaptadores de entrada (Controllers REST)
        │   │   │   │   └── {Entidad}Controller.java
        │   │   │   └── out/              # Adaptadores de salida (Repositorios, Clientes)
        │   │   │       └── {Entidad}RepositoryAdapter.java
        │   │   └── config/               # Configuraciones específicas del contexto
        │   │       └── {Contexto}Config.java
        │   └── resources/
        │       └── db/migration/         # Migraciones Flyway
        │           └── V1.0__{contexto}_schema.sql
        └── test/java/com/arquisoft/{contexto}/infrastructure/
            └── adapter/
                └── out/
                    └── {Entidad}RepositoryAdapterTest.java
```

---

## Dependencias entre Capas (build.gradle)

```
domain/build.gradle:
  → Sin dependencias externas (solo shared:domain si necesita DomainEvent/AggregateRoot)

application/build.gradle:
  → implementation project(':{contexto}:domain')

infrastructure/build.gradle:
  → implementation project(':{contexto}:domain')
  → implementation project(':{contexto}:application')
  → + dependencias de framework (Spring, JPA, Flyway, PostgreSQL, etc.)
```

### Regla fundamental

```
Domain ← Application ← Infrastructure
(sin deps)  (usa domain)  (usa todo + frameworks)
```

La capa de **dominio** NUNCA depende de la de aplicación ni de infraestructura.

**¿Por qué?** Porque si el dominio importara clases de Spring o de JPA, cualquier cambio de framework obligaría a reescribir las reglas de negocio. Al mantenerlo aislado, el dominio es portable, reutilizable y testeable sin levantar un servidor.

---

## Ejemplo Completo: Contexto "Fichas"

A continuación se explica clase por clase qué hace, por qué existe y cómo se conecta con las demás.

---

### 1. Entidad de Dominio — `Ficha.java`

**¿Qué es?** La representación en código de un concepto de negocio. `Ficha` encapsula los datos y las reglas que definen qué es una ficha de trabajo de grado.

**¿Por qué es fundamental?**
- Es el **corazón** del contexto: toda la lógica de negocio gira en torno a esta clase.
- No depende de ningún framework. No tiene `@Entity`, no tiene `@Column`. Es Java puro.
- Usa **constructor privado + factory methods** (`build` y `rebuild`) para controlar cómo se crean las instancias, garantizando que una ficha nueva siempre nace en estado `"BORRADOR"` y con fecha de creación automática.
- Los campos son `final` (inmutables): una vez construida, la ficha no puede mutar de forma incontrolada. Esto previene errores en entornos concurrentes y hace el código más predecible.

**¿Cuándo usar `build` vs `rebuild`?**
- `build(...)` → para **crear fichas nuevas** desde la capa de aplicación (no tienen `id` aún, el estado inicial es automático).
- `rebuild(...)` → para **reconstruir fichas** desde la base de datos (ya tienen `id`, estado y fecha existentes).

```java
// fichas/domain/src/main/java/com/arquisoft/fichas/domain/model/Ficha.java
package com.arquisoft.fichas.domain.model;

import java.time.LocalDateTime;

public class Ficha {
    private final Long id;
    private final String titulo;
    private final String descripcion;
    private final String areaConocimiento;
    private final String estado;
    private final LocalDateTime fechaCreacion;

    // Constructor privado: nadie puede hacer "new Ficha(...)" desde afuera.
    // Esto obliga a usar los factory methods, donde viven las reglas de creación.
    private Ficha(Long id, String titulo, String descripcion, 
                  String areaConocimiento, String estado, LocalDateTime fechaCreacion) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.areaConocimiento = areaConocimiento;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    // Factory method: crear ficha NUEVA.
    // El estado siempre es "BORRADOR" y la fecha es "ahora".
    // Esto es una regla de negocio: toda ficha empieza como borrador.
    public static Ficha build(String titulo, String descripcion, String areaConocimiento) {
        return new Ficha(null, titulo, descripcion, areaConocimiento, "BORRADOR", LocalDateTime.now());
    }

    // Factory method: reconstruir ficha EXISTENTE desde persistencia.
    // Aquí se aceptan todos los campos tal cual vienen de la BD.
    public static Ficha rebuild(Long id, String titulo, String descripcion,
                                String areaConocimiento, String estado, LocalDateTime fechaCreacion) {
        return new Ficha(id, titulo, descripcion, areaConocimiento, estado, fechaCreacion);
    }

    // Solo getters, no setters: la entidad es inmutable.
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getAreaConocimiento() { return areaConocimiento; }
    public String getEstado() { return estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}
```

---

### 2. Puerto de Entrada — `CrearFichaUseCase.java`

**¿Qué es?** Una **interfaz** que define *qué* puede hacer el sistema, sin decir *cómo*. Declara la operación "crear ficha" como un contrato.

**¿Por qué es fundamental?**
- Es el **contrato entre el mundo exterior y el dominio**. Cuando un controller quiere crear una ficha, no habla directamente con el repositorio ni con la base de datos: invoca este puerto.
- Al ser una interfaz, **desacopla al consumidor de la implementación**. El controller no sabe si detrás hay validaciones, eventos, auditoría o cualquier otra lógica. Solo sabe que puede llamar a `crearFicha(Ficha)` y recibirá una `Ficha`.
- Vive en `domain/` porque es un contrato de negocio: "el sistema debe poder crear fichas". No es un detalle técnico.
- Si mañana se necesita un nuevo caso de uso (ej: `AprobarFichaUseCase`), se crea otra interfaz aquí. Cada interfaz = una acción de negocio.

**Convención de nombre:** `{Accion}{Entidad}UseCase` → acción en infinitivo + entidad + sufijo UseCase.

```java
// fichas/domain/src/main/java/com/arquisoft/fichas/domain/port/in/CrearFichaUseCase.java
package com.arquisoft.fichas.domain.port.in;

import com.arquisoft.fichas.domain.model.Ficha;

// Recibe una Ficha (dominio) y retorna una Ficha (dominio).
// No recibe DTOs, no retorna ResponseEntity. Puro dominio.
public interface CrearFichaUseCase {
    Ficha crearFicha(Ficha ficha);
}
```

---

### 3. Puerto de Salida — `FichaRepositoryPort.java`

**¿Qué es?** Una **interfaz** que define las operaciones de persistencia que el dominio *necesita*, sin especificar la tecnología.

**¿Por qué es fundamental?**
- Representa la **Inversión de Dependencias** (la "D" de SOLID). El dominio dice "necesito poder guardar y buscar fichas", pero no sabe si se usa PostgreSQL, MongoDB, un archivo CSV o una API externa.
- Vive en `domain/` porque el dominio *define* lo que necesita; la infraestructura lo *implementa*. Así, cambiar de base de datos no toca ni una sola línea de lógica de negocio.
- Los métodos usan tipos de Java estándar (`Optional`, `List`) y la entidad de dominio (`Ficha`), nunca clases de JPA ni de Spring.

**¿Por qué "Port" en el nombre?** Porque en la metáfora hexagonal, un puerto es un punto de conexión. El puerto de salida conecta el dominio con el mundo externo (BD, servicios, archivos).

```java
// fichas/domain/src/main/java/com/arquisoft/fichas/domain/port/out/FichaRepositoryPort.java
package com.arquisoft.fichas.domain.port.out;

import com.arquisoft.fichas.domain.model.Ficha;
import java.util.List;
import java.util.Optional;

// Contrato de qué operaciones de persistencia necesita el dominio.
// La implementación concreta (JdbcTemplate, JPA, etc.) vive en infrastructure/.
public interface FichaRepositoryPort {
    Ficha save(Ficha ficha);              // Persistir una ficha nueva o actualizada
    Optional<Ficha> findById(Long id);    // Buscar por ID (puede no existir → Optional)
    List<Ficha> findAll();                // Listar todas las fichas
    boolean deleteById(Long id);          // Eliminar; retorna true si existía
}
```

---

### 4. DTO — `FichaDTO.java`

**¿Qué es?** Un **Data Transfer Object**: un objeto simple que transporta datos entre capas, especialmente entre la API REST y el dominio.

**¿Por qué es fundamental?**
- **Protege al dominio de la API**: si el JSON que envía el cliente cambia (agregar un campo, renombrar otro), solo se modifica el DTO. La entidad `Ficha` permanece intacta.
- **Controla qué se expone**: el DTO puede omitir campos internos (estado, fechaCreación) que el cliente no necesita enviar al crear, pero sí se incluyen cuando se devuelve la respuesta.
- **Evita dependencias circulares**: el controller no necesita importar la entidad directamente; trabaja con DTOs y usa `toDomain()` / `fromDomain()` para convertir.
- Vive en `application/` porque es un concepto de orquestación, no de negocio. El dominio no sabe que existen DTOs.

**Métodos clave:**
- `toDomain()` → convierte el DTO en entidad de dominio (para entrada: request del cliente → dominio).
- `fromDomain(Ficha)` → convierte la entidad en DTO (para salida: dominio → response al cliente).

```java
// fichas/application/src/main/java/com/arquisoft/fichas/application/dto/FichaDTO.java
package com.arquisoft.fichas.application.dto;

import com.arquisoft.fichas.domain.model.Ficha;
import lombok.Data;

@Data  // Lombok genera getters, setters, equals, hashCode y toString automáticamente.
public class FichaDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String areaConocimiento;

    // Convierte este DTO → entidad de dominio.
    // Nótese que usa Ficha.build() (no rebuild), porque viene del cliente
    // y no tiene id ni estado: se está creando una ficha nueva.
    public Ficha toDomain() {
        return Ficha.build(this.titulo, this.descripcion, this.areaConocimiento);
    }

    // Convierte entidad de dominio → DTO para la respuesta HTTP.
    // Solo incluye los campos que el cliente necesita ver.
    public static FichaDTO fromDomain(Ficha ficha) {
        FichaDTO dto = new FichaDTO();
        dto.setId(ficha.getId());
        dto.setTitulo(ficha.getTitulo());
        dto.setDescripcion(ficha.getDescripcion());
        dto.setAreaConocimiento(ficha.getAreaConocimiento());
        return dto;
    }
}
```

---

### 5. Implementación de Use Case — `CrearFichaUseCaseImpl.java`

**¿Qué es?** La clase que **implementa** el puerto de entrada `CrearFichaUseCase`. Aquí vive la lógica de orquestación del caso de uso "crear ficha".

**¿Por qué es fundamental?**
- Es el **director de orquesta**: coordina qué pasos ocurren cuando se crea una ficha (validar datos, guardar en BD, publicar eventos, etc.).
- Implementa la interfaz del dominio (`CrearFichaUseCase`), pero vive en `application/` porque orquesta — no define reglas puras de negocio.
- Depende del **puerto de salida** (`FichaRepositoryPort`), no de la implementación concreta. Spring inyectará el `FichaRepositoryAdapter` en tiempo de ejecución gracias a `@RequiredArgsConstructor`.
- `@Component` la registra como un bean de Spring para que sea inyectable donde se necesite.

**¿Dónde van las validaciones de negocio?** Aquí. Si antes de guardar una ficha hay que verificar que el título no esté duplicado, o que el usuario tenga permisos, la lógica va en este método (o delegada a un servicio de dominio).

```java
// fichas/application/src/main/java/com/arquisoft/fichas/application/usecase/CrearFichaUseCaseImpl.java
package com.arquisoft.fichas.application.usecase;

import com.arquisoft.fichas.domain.model.Ficha;
import com.arquisoft.fichas.domain.port.in.CrearFichaUseCase;
import com.arquisoft.fichas.domain.port.out.FichaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component            // Spring lo registra como bean → inyectable automáticamente
@RequiredArgsConstructor  // Lombok genera constructor con todos los campos final
public class CrearFichaUseCaseImpl implements CrearFichaUseCase {

    // Se inyecta la INTERFAZ (puerto), no la implementación concreta.
    // Spring resolverá qué clase concreta usar en tiempo de ejecución.
    private final FichaRepositoryPort fichaRepositoryPort;

    @Override
    public Ficha crearFicha(Ficha ficha) {
        // Aquí iría lógica de negocio adicional:
        // - Validar que el título no esté duplicado
        // - Verificar permisos del usuario
        // - Publicar un evento FichaCreadaEvent
        return fichaRepositoryPort.save(ficha);
    }
}
```

---

### 6. Controller — `FichaController.java` (Adaptador de Entrada)

**¿Qué es?** El punto de entrada HTTP de la API REST. Recibe solicitudes del cliente, las convierte en llamadas al dominio, y devuelve respuestas HTTP.

**¿Por qué es fundamental?**
- Es un **adaptador de entrada**: traduce el "lenguaje" de HTTP (JSON, status codes, headers) al "lenguaje" del dominio (entidades, casos de uso).
- Depende del **puerto de entrada** (`CrearFichaUseCase`), no de la implementación. Esto significa que el controller no sabe si el caso de uso valida, publica eventos o audita — solo sabe que puede crear fichas.
- Vive en `infrastructure/adapter/in/` porque HTTP es un detalle de infraestructura. Si mañana la entrada fuera por GraphQL, gRPC o una cola de mensajes, se crearía otro adaptador aquí — sin tocar el dominio ni la aplicación.

**Flujo dentro del controller:**
1. Recibe `FichaDTO` como JSON (`@RequestBody`).
2. Convierte DTO → entidad de dominio (`dto.toDomain()`).
3. Invoca el caso de uso (`crearFichaUseCase.crearFicha(ficha)`).
4. Convierte el resultado → DTO (`FichaDTO.fromDomain(ficha)`).
5. Retorna `ResponseEntity` con status `201 CREATED`.

**¿Por qué no retorna la entidad directamente?** Porque la entidad de dominio no debe tener anotaciones de serialización (`@JsonProperty`, etc.). Además, el DTO permite controlar exactamente qué campos se exponen al cliente.

```java
// fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/in/FichaController.java
package com.arquisoft.fichas.infrastructure.adapter.in;

import com.arquisoft.fichas.application.dto.FichaDTO;
import com.arquisoft.fichas.domain.model.Ficha;
import com.arquisoft.fichas.domain.port.in.CrearFichaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController                        // Marca esta clase como controller REST de Spring
@RequestMapping("/api/fichas")         // Base path para todos los endpoints de fichas
@RequiredArgsConstructor               // Lombok inyecta el use case vía constructor
public class FichaController {

    // Depende de la INTERFAZ (puerto de entrada), no de la implementación.
    // Si se crearan múltiples implementaciones del use case, se puede
    // intercambiar sin modificar este controller.
    private final CrearFichaUseCase crearFichaUseCase;

    @PostMapping
    public ResponseEntity<FichaDTO> crear(@RequestBody FichaDTO dto) {
        // 1. DTO → Dominio: traducir del mundo HTTP al mundo de negocio
        Ficha ficha = crearFichaUseCase.crearFicha(dto.toDomain());

        // 2. Dominio → DTO: traducir del mundo de negocio al mundo HTTP
        return ResponseEntity.status(HttpStatus.CREATED).body(FichaDTO.fromDomain(ficha));
    }
}
```

---

### 7. Repository Adapter — `FichaRepositoryAdapter.java` (Adaptador de Salida)

**¿Qué es?** La implementación concreta del puerto de salida `FichaRepositoryPort`. Aquí se escribe el SQL real, se usa JdbcTemplate, JPA, o cualquier tecnología de persistencia.

**¿Por qué es fundamental?**
- Es un **adaptador de salida**: traduce las operaciones abstractas del dominio ("guardar ficha") en operaciones concretas de infraestructura (`INSERT INTO fichas.ficha ...`).
- Implementa la interfaz definida en el dominio (`FichaRepositoryPort`), completando el circuito de la Inversión de Dependencias.
- Vive en `infrastructure/adapter/out/` porque PostgreSQL, JdbcTemplate y SQL son detalles de infraestructura. Si mañana se migrara a MongoDB, solo se reescribe esta clase — el dominio y la aplicación no se enteran.
- `@Repository` le indica a Spring que esta clase es un bean de acceso a datos y habilita la traducción automática de excepciones de BD a excepciones de Spring.

**Relación con el puerto:**

```
FichaRepositoryPort (interfaz en domain/)
        ↑ implementa
FichaRepositoryAdapter (clase en infrastructure/adapter/out/)
        ↓ usa
JdbcTemplate (framework Spring)
        ↓
PostgreSQL (base de datos)
```

```java
// fichas/infrastructure/src/main/java/com/arquisoft/fichas/infrastructure/adapter/out/FichaRepositoryAdapter.java
package com.arquisoft.fichas.infrastructure.adapter.out;

import com.arquisoft.fichas.domain.model.Ficha;
import com.arquisoft.fichas.domain.port.out.FichaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository            // Spring lo detecta como bean de acceso a datos
@RequiredArgsConstructor
public class FichaRepositoryAdapter implements FichaRepositoryPort {

    // JdbcTemplate es la herramienta concreta de acceso a BD.
    // Podría ser EntityManager (JPA), MongoTemplate, etc.
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Ficha save(Ficha ficha) {
        // Aquí va el INSERT real contra PostgreSQL.
        // Usa el schema del contexto: fichas.ficha
        String sql = "INSERT INTO fichas.ficha (titulo, descripcion, area_conocimiento, estado, fecha_creacion) "
                   + "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
            ficha.getTitulo(), ficha.getDescripcion(),
            ficha.getAreaConocimiento(), ficha.getEstado(),
            ficha.getFechaCreacion()
        );
        return ficha;
    }

    @Override
    public Optional<Ficha> findById(Long id) {
        // SELECT con Ficha.rebuild() para reconstruir la entidad desde la fila de BD
        return Optional.empty(); // TODO: implementar
    }

    @Override
    public List<Ficha> findAll() {
        // SELECT ALL con RowMapper que use Ficha.rebuild()
        return List.of(); // TODO: implementar
    }

    @Override
    public boolean deleteById(Long id) {
        // DELETE FROM fichas.ficha WHERE id = ?
        return false; // TODO: implementar
    }
}
```

---

### 8. Test de Use Case — `CrearFichaUseCaseImplTest.java`

**¿Qué es?** Un test unitario que verifica la lógica del caso de uso **sin necesitar base de datos, servidor ni ningún servicio externo**.

**¿Por qué es fundamental?**
- Demuestra la **testabilidad** que proporciona la arquitectura hexagonal: como el use case depende de una *interfaz* (`FichaRepositoryPort`), se puede reemplazar por un *mock* en tests.
- Verifica el **comportamiento**, no la implementación: "cuando creo una ficha, ¿se guarda exactamente una vez en el repositorio?".
- Es rápido (milisegundos) porque no levanta Spring ni conecta a BD.
- Usa `@ExtendWith(MockitoExtension.class)` para habilitar la creación automática de mocks.

**Anatomía del test (patrón AAA):**
- **Arrange** (preparar): crear la ficha de prueba y configurar qué retorna el mock.
- **Act** (ejecutar): invocar el caso de uso.
- **Assert** (verificar): comprobar que el resultado es correcto y que el repositorio fue llamado.

```java
// fichas/application/src/test/java/com/arquisoft/fichas/application/usecase/CrearFichaUseCaseImplTest.java
package com.arquisoft.fichas.application.usecase;

import com.arquisoft.fichas.domain.model.Ficha;
import com.arquisoft.fichas.domain.port.out.FichaRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)  // Habilita @Mock e @InjectMocks sin levantar Spring
class CrearFichaUseCaseImplTest {

    @Mock  // Crea un mock del puerto de salida (no es la BD real)
    private FichaRepositoryPort fichaRepositoryPort;

    @InjectMocks  // Crea la instancia del use case e inyecta el mock automáticamente
    private CrearFichaUseCaseImpl crearFichaUseCase;

    @Test
    void shouldCreateFichaSuccessfully() {
        // ARRANGE: preparar datos de prueba y configurar el mock
        Ficha ficha = Ficha.build("Mi Ficha", "Descripción", "Ingeniería");
        when(fichaRepositoryPort.save(any(Ficha.class))).thenReturn(ficha);

        // ACT: ejecutar el caso de uso
        Ficha result = crearFichaUseCase.crearFicha(ficha);

        // ASSERT: verificar resultado y comportamiento
        assertNotNull(result);                                    // ¿Se retornó algo?
        assertEquals("Mi Ficha", result.getTitulo());             // ¿El título es correcto?
        verify(fichaRepositoryPort, times(1)).save(ficha);        // ¿Se llamó save exactamente 1 vez?
    }
}
```

---

## Resumen: Flujo Completo de una Petición

```
                          INFRASTRUCTURE                    APPLICATION              DOMAIN
                          ─────────────                    ───────────              ──────

  Cliente HTTP ──────►  FichaController                                          
  (JSON)                   │                                                     
                           │ dto.toDomain()  ─────────────────────────────────►  Ficha.build()
                           │                                                        │
                           │ crearFichaUseCase ──►  CrearFichaUseCaseImpl            │
                           │     .crearFicha()         │                             │
                           │                           │ fichaRepositoryPort    CrearFichaUseCase
                           │                           │     .save(ficha)       (interfaz)
                           │                           │         │                  │
                           │                           │         │             FichaRepositoryPort
                           │                           │         │             (interfaz)
                           │                           │         │                  │
  FichaRepositoryAdapter ◄─┼───────────────────────────┼─────────┘                  │
       │                   │                           │                            │
   JdbcTemplate            │                           │                            │
       │                   │                           │                            │
   PostgreSQL              │                           │                            │
       │                   │                           │                            │
   Ficha guardada ─────────┼───────────────────────────┼────────────────────────►  Ficha
                           │                           │                            │
                           │ FichaDTO.fromDomain() ◄───┼────────────────────────────┘
                           │         │                 │
  Cliente HTTP ◄───────────┤ ResponseEntity 201        │
  (JSON)                   │                           │
```

**Lo que logra este flujo:**
1. El **Controller** no sabe cómo se guarda la ficha (podría ser PostgreSQL, MongoDB, un archivo).
2. El **UseCase** no sabe quién lo invoca (podría ser un controller REST, un listener de RabbitMQ, un test).
3. La **Entidad** no sabe que existe Spring, HTTP ni bases de datos. Es Java puro.
4. El **Test** puede verificar toda la lógica sin levantar nada.

---

## Convenciones de Nombres

| Tipo | Formato | Ejemplo | ¿Dónde vive? |
|------|---------|---------|--------------|
| **Entidad** | `{Nombre}.java` | `Ficha.java` | `domain/model/` |
| **Puerto entrada** | `{Accion}{Entidad}UseCase.java` | `CrearFichaUseCase.java` | `domain/port/in/` |
| **Puerto salida** | `{Entidad}RepositoryPort.java` | `FichaRepositoryPort.java` | `domain/port/out/` |
| **Excepción dominio** | `{Nombre}Exception.java` | `FichaNoEncontradaException.java` | `domain/exception/` |
| **DTO** | `{Entidad}DTO.java` | `FichaDTO.java` | `application/dto/` |
| **Impl UseCase** | `{Accion}{Entidad}UseCaseImpl.java` | `CrearFichaUseCaseImpl.java` | `application/usecase/` |
| **Controller** | `{Entidad}Controller.java` | `FichaController.java` | `infrastructure/adapter/in/` |
| **Repo Adapter** | `{Entidad}RepositoryAdapter.java` | `FichaRepositoryAdapter.java` | `infrastructure/adapter/out/` |
| **Config** | `{Contexto}Config.java` | `FichasConfig.java` | `infrastructure/config/` |
| **Evento** | `{Nombre}Event.java` | `FichaCreadaEvent.java` | `domain/model/` o `domain/event/` |
| **Migración** | `V{version}__{contexto}_{desc}.sql` | `V1.0__fichas_schema.sql` | `infrastructure/resources/db/migration/` |

---

## Paquete Base

Todos los contextos usan el paquete base: `com.arquisoft.{contexto}`

```
com.arquisoft.fichas.domain.model           ← Entidades puras
com.arquisoft.fichas.domain.port.in         ← Contratos de entrada (qué puede hacer el sistema)
com.arquisoft.fichas.domain.port.out        ← Contratos de salida (qué necesita el sistema)
com.arquisoft.fichas.domain.exception       ← Errores de negocio

com.arquisoft.fichas.application.dto        ← Objetos de transferencia (API ↔ Dominio)
com.arquisoft.fichas.application.usecase    ← Orquestación de reglas de negocio

com.arquisoft.fichas.infrastructure.adapter.in   ← Puntos de entrada (REST, eventos)
com.arquisoft.fichas.infrastructure.adapter.out  ← Puntos de salida (BD, APIs externas)
com.arquisoft.fichas.infrastructure.config       ← Configuraciones Spring del contexto
```

---

## Comunicación entre Contextos (Eventos)

Los contextos **NUNCA** deben depender directamente entre sí. Si `Proyectos` necesita saber que se creó una ficha, no importa una clase de `Fichas` — escucha un evento asincrónico.

**¿Por qué?** Porque si `Proyectos` importa clases de `Fichas`, un cambio en `Fichas` puede romper `Proyectos`. Con eventos, cada contexto evoluciona de forma independiente.

```java
// En el contexto EMISOR (Fichas): publicar evento tras crear la ficha
eventPublisher.publish(new FichaCreadaEvent(ficha.getId(), ficha.getTitulo()));

// En el contexto RECEPTOR (Proyectos): escuchar el evento de forma asincrónica
@RabbitListener(bindings = @QueueBinding(
    value = @Queue("proyectos.fichas-events"),
    exchange = @Exchange("arquisoft.events"),
    key = "fichas.creada"
))
public void onFichaCreada(String message) {
    // Procesar evento: ej. crear un proyecto asociado a la ficha
}
```

**Beneficio**: el emisor y el receptor no se conocen entre sí. `Fichas` publica y se olvida. Si `Proyectos` deja de existir, `Fichas` no se rompe.

---

## Preguntas Frecuentes

**¿Por qué tantas clases para algo tan simple?**
Porque la arquitectura está diseñada para **escalar**. En un CRUD trivial parece excesivo, pero cuando hay validaciones complejas, auditoría, eventos, transacciones y 7 contextos interconectados, esta separación es la diferencia entre un sistema mantenible y uno ingobernable.

**¿Por qué el puerto de entrada y la implementación están en módulos distintos?**
Para poder testear la interfaz sin la implementación, y para forzar que la interfaz no dependa de Spring. Si ambas están juntas, la tentación de acoplarlas es alta.

**¿Puedo tener más de un UseCase por entidad?**
Sí, y es lo esperado: `CrearFichaUseCase`, `ObtenerFichaUseCase`, `AprobarFichaUseCase`, `EliminarFichaUseCase`. Cada uno con su interfaz y su implementación.

**¿Dónde van los servicios de dominio?**
En `domain/model/` o `domain/service/`, dependiendo del caso. Un servicio de dominio contiene lógica que involucra a más de una entidad y no pertenece a ninguna en particular.

---

**Versión**: 1.1.0
