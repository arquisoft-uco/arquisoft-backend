# AGENTS.md — Arquisoft Backend

## Descripcion del Proyecto

Monolito modular en Java 21 / Spring Boot 3.2.4 construido con Gradle 8.6.
Arquitectura Hexagonal (Puertos y Adaptadores) con Diseno Dirigido por Dominio (DDD).
Siete contextos acotados (bounded contexts), cada uno como multi-proyecto Gradle
con tres capas: `domain`, `application`, `infrastructure`.

Infraestructura: PostgreSQL 15, RabbitMQ 3.12, Redis 7, Keycloak 22 (OAuth2/OIDC).

## Comandos de Compilacion y Ejecucion

```bash
./gradlew build                    # Compilar + ejecutar tests
./gradlew build -x test            # Compilar sin tests
./gradlew clean build              # Limpiar y compilar desde cero
./gradlew bootRun --args='--spring.profiles.active=dev'  # Ejecutar con perfil dev
./gradlew projects                 # Listar todos los modulos Gradle
```

## Comandos de Tests

```bash
./gradlew test                     # Ejecutar TODOS los tests
./gradlew seguridad:infrastructure:test   # Tests de un modulo especifico
./gradlew fichas:application:test         # Tests de la capa application de fichas
./gradlew test --tests "com.arquisoft.fichas.application.usecase.CrearFichaUseCaseImplTest"  # Una clase de test
./gradlew test --tests "*.CrearFichaUseCaseImplTest.debeCrearFicha_cuandoDatosValidos"       # Un metodo de test
./gradlew jacocoTestReport         # Reporte de cobertura (minimo 75%)
```

Stack de tests: JUnit 5 + Mockito + AssertJ. H2 en memoria para tests de repositorio.
Spring Security Test para tests de autenticacion en controllers.

## Docker

```bash
docker compose up -d               # Levantar infraestructura (Postgres, RabbitMQ, Redis, Keycloak)
docker compose up --build           # Reconstruir e iniciar todo incluyendo el backend
```

## Reglas de Arquitectura

### Estructura Hexagonal (por contexto acotado)

```
{contexto}/
├── domain/          # Logica de negocio pura — SIN dependencias de framework
│   ├── model/       # Entidades (inmutables, factory methods), Value Objects, Enums
│   ├── port/in/     # Puertos de entrada: interfaces de casos de uso ({Accion}{Entidad}UseCase)
│   ├── port/out/    # Puertos de salida: interfaces de repositorio ({Entidad}RepositoryPort)
│   └── exception/   # Excepciones de dominio (extienden RuntimeException)
├── application/     # Capa de orquestacion
│   ├── dto/         # DTOs con metodos toDomain() / fromDomain()
│   └── usecase/     # Implementaciones de casos de uso ({Accion}{Entidad}UseCaseImpl)
└── infrastructure/  # Detalles tecnicos y de framework
    ├── adapter/in/  # Controllers REST, listeners de eventos, manejadores de excepciones
    ├── adapter/out/ # Adaptadores de repositorio, clientes de APIs externas
    ├── config/      # Clases @Configuration de Spring
    ├── filter/      # Filtros HTTP (auditoria, rate limiting)
    └── resources/db/migration/  # Migraciones SQL de Flyway
```

### Direccion de Dependencias (estrictamente forzada via Gradle)

```
Domain ← Application ← Infrastructure
```

- Domain tiene CERO dependencias de framework (Java puro + shared:domain)
- Application depende solo de domain
- Infrastructure depende de ambas + Spring/JPA/etc.
- Los contextos NUNCA dependen directamente entre si; se comunican via eventos RabbitMQ

## Estilo de Codigo

### Convenciones de Nomenclatura

| Elemento | Convencion | Ejemplo |
|----------|-----------|---------|
| Clases | PascalCase | `AuthController`, `KeycloakAuthServiceImpl` |
| Interfaces (Puertos) | PascalCase, sin prefijo `I` | `KeycloakAuthService`, `FichaRepositoryPort` |
| Implementaciones | Sufijo `Impl` | `KeycloakAuthServiceImpl` |
| DTOs | PascalCase + sufijo `DTO` | `LoginRequestDTO`, `ErrorResponseDTO` |
| Excepciones | PascalCase + sufijo `Exception` | `InvalidCredentialsException` |
| Enums | PascalCase; valores `SCREAMING_SNAKE_CASE` | `UserRole.ASESOR_FICHA` |
| Configuraciones | Sufijo `Config` | `SecurityConfig`, `CorsConfig` |
| Filtros | Sufijo `Filter` | `AuditFilter`, `RateLimitingFilter` |
| Metodos | camelCase, verbo primero | `authenticate`, `extractUserFromToken` |
| Constantes | `SCREAMING_SNAKE_CASE` | `EMAIL_PATTERN` |
| Metodos de test | `debeHacerAlgo_cuandoCondicion` | `debeCrearFicha_cuandoDatosValidos` |
| Paquetes | minusculas, espanol para contextos | `seguridad`, `fichas`, `proyectos` |

### Regla Bilingue

- **Espanol** para terminos de negocio/dominio: `ProyectoGrado`, `Ficha`, `crearFicha`, `fechaCreacion`
- **Ingles** para sufijos tecnicos: `UseCase`, `Adapter`, `DTO`, `Controller`, `Port`, `Repository`
- **Espanol** para Javadoc, mensajes de error y textos visibles al usuario
- **Ingles** para paquetes estructurales: `domain`, `application`, `infrastructure`, `model`, `port`, `adapter`, `config`

### Imports

- Imports explicitos siempre (nunca wildcard `*`)
- Orden: proyecto (`com.arquisoft.*`) > Jakarta > Lombok > Spring > Java stdlib

### DTOs

```java
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class FichaDTO {
    // campos...
    public Ficha toDomain() { ... }
    public static FichaDTO fromDomain(Ficha ficha) { ... }
}
```

- Usar `@Valid @RequestBody` en parametros de controllers para validacion
- Anotaciones de validacion Jakarta: `@NotBlank`, `@Email`, `@Size`
- `@JsonInclude(JsonInclude.Include.NON_NULL)` en DTOs de respuesta de error
- `@Builder.Default` para valores por defecto en campos

### Entidades de Dominio

- Inmutables: constructor privado, campos `final`, solo getters
- Factory methods: `build(...)` para instancias nuevas, `rebuild(...)` desde persistencia
- Sin Lombok, sin anotaciones de framework — Java puro

### Inyeccion de Dependencias

- Siempre inyeccion por constructor via `@RequiredArgsConstructor` (Lombok)
- Nunca usar `@Autowired`
- Inyectar interfaces (puertos), nunca implementaciones

### Logging

- `@Slf4j` (Lombok) en toda clase que necesite logging
- `log.warn()` para errores de cliente (4xx), `log.error()` para errores de servidor (5xx)

### Manejo de Errores

- Todas las excepciones de dominio extienden `RuntimeException` (no verificadas)
- Jerarquia: `DomainException` (base compartida con campo `errorCode`)
- Excepciones por contexto: ej. `AuthenticationException` > `InvalidCredentialsException`
- `@RestControllerAdvice` centralizado (`GlobalExceptionHandler`) por contexto
- `ErrorResponseDTO` estandarizado: `error`, `message`, `status`, `path`, `timestamp`, `fieldErrors`
- Los servicios capturan excepciones de infraestructura y las traducen a excepciones de dominio

### Patrones de Testing

- **Tests unitarios**: `@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`
- **Patron AAA**: Arrange (preparar datos + mocks), Act (ejecutar), Assert (verificar)
- **Sin contexto Spring** en tests unitarios — Mockito puro
- **Tests de integracion**: `@SpringBootTest` con H2 para la capa de repositorio
- Ubicacion de tests refleja la estructura de fuentes: `src/test/java/com/arquisoft/{contexto}/...`

### Configuracion

- Externalizar via `@Value("${propiedad.nombre:valorPorDefecto}")` con defaults razonables
- Perfiles: `dev` (localhost, logging debug), `prod` (variables de entorno, rate limiting)
- Versiones centralizadas en `gradle.properties`

## Convenciones de Git

- **Commits**: Conventional Commits en espanol — `feat(contexto): descripcion corta`
  - Tipos: `feat`, `fix`, `refactor`, `docs`, `style`, `test`, `chore`
- **Ramas**: `<prefijo>/<id>-<descripcion_snake_case>` desde `develop`
  - Prefijos: `feature/`, `fix/`, `refactor/`, `hotfix/`, `docs/`, `test/`, `chore/`, `spike/`
  - Ejemplo: `feature/HT-005-scaffolding_spring_boot`
- **PRs**: Hacia `develop`, requieren 1 aprobacion, usar `.github/PULL_REQUEST_TEMPLATE.md`

## Context7 MCP

Cuando necesites documentación de librerías, configuración de dependencias o ejemplos
de código de cualquier framework del stack, usa automáticamente las herramientas de
Context7 sin que el usuario te lo pida explícitamente:

- `resolve-library-id` para obtener el ID de la librería
- `query-docs` para obtener la documentación específica de esa versión

Librerías prioritarias para este proyecto:
- Spring Boot → `/spring-projects/spring-boot` (versión 3.2.4)
- Spring AMQP (RabbitMQ) → `/spring-projects/spring-amqp`
- Spring Data JPA → `/spring-projects/spring-data-jpa`
- Spring Security → `/spring-projects/spring-security`
- Flyway → `/flyway/flyway`
- Keycloak Admin Client → `/keycloak/keycloak`
- Gradle → `/gradle/gradle`
- JUnit 5 → `/junit-team/junit5`
- Mockito → `/mockito/mockito`
- AssertJ → `/assertj/assertj-core`

## Archivos Clave

| Archivo | Proposito |
|---------|----------|
| `build.gradle` | Configuracion raiz, auto-incluye todos los subproyectos `*:infrastructure` |
| `settings.gradle` | Declara los 38 subproyectos Gradle |
| `gradle.properties` | Versiones centralizadas de dependencias |
| `src/main/resources/application.yml` | Configuracion base de Spring |
| `src/main/resources/application-dev.yml` | Sobrecargas del perfil de desarrollo |
| `init-db.sql` | Crea 7 esquemas PostgreSQL (uno por contexto acotado) |
| `docker-compose.yml` | Stack completo (Postgres, RabbitMQ, Redis, Keycloak, Nextcloud) |
| `shared/example/README.md` | Guia de referencia con ejemplo completo del contexto Fichas |
| `CONTRIBUTING.md` | Flujo de contribucion y convenciones del proyecto |
