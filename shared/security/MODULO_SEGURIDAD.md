# Módulo de Seguridad - Documentación Completa

## Descripción General

El módulo `shared/security` proporciona una capa completa de seguridad para todos los contextos de la aplicación usando **Spring Security**, **JWT** y **Keycloak**. Este módulo es **reutilizable** en todos los contextos sin necesidad de modificaciones.

### Características Principales

- ✅ **Autenticación OAuth2/OpenID Connect** con Keycloak
- ✅ **Validación de JWT** con claves públicas de Keycloak
- ✅ **Gestión de Roles y Permisos** basada en claims del JWT
- ✅ **CORS configurable** por origen, método y header
- ✅ **Rate Limiting** por IP para protección contra ataques
- ✅ **Auditoría de accesos** con registros detallados
- ✅ **Refresh Token** para mantener sesiones activas
- ✅ **Estadeless** - sin almacenamiento de sesiones en servidor

---

## Arquitectura

```
shared/security/
├── src/main/java/com/arquisoft/shared/security/
│   ├── domain/
│   │   ├── dto/                    # DTOs de transferencia
│   │   ├── enums/                  # Enumeraciones (UserRole)
│   │   └── exceptions/             # Excepciones de seguridad
│   ├── application/
│   │   ├── services/               # Interfaces de servicios
│   │   └── impl/                   # Implementaciones
│   ├── infrastructure/
│   │   ├── config/                 # Configuración Spring Security, CORS, Rate Limit
│   │   ├── filters/                # Filtros (Rate Limiting, Auditoría)
│   │   ├── keycloak/               # Integración Keycloak
│   │   └── rest/                   # Controladores (AuthController)
│   └── CurrentUserProvider.java    # Interfaz para acceder al usuario actual
└── src/main/resources/
    └── application-security.properties
```

---

## Roles Disponibles

El sistema incluye 8 roles predefinidos que se asignan en Keycloak:

```java
ASESOR_FICHA           // Asesor de Ficha
JURADO                 // Jurado
BIBLIOTECARIO          // Bibliotecario
ADMINISTRADOR          // Administrador
ESTUDIANTE             // Estudiante
ASESOR                 // Asesor
COORDINADOR            // Coordinador
REPRESENTANTE_COMITE_CURRICULUM  // Representante Comité de Curriculum
```

### Cómo usar roles en tu código:

```java
@RestController
@RequestMapping("/api/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {
    
    private final CurrentUserProvider currentUserProvider;
    
    // Usar anotación de Spring Security
    @GetMapping
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ASESOR', 'ADMINISTRADOR')")
    public ResponseEntity<?> listarEstudiantes() {
        // Solo usuarios con estos roles pueden acceder
        return ResponseEntity.ok(...);
    }
    
    // O usar CurrentUserProvider programáticamente
    @PostMapping
    public ResponseEntity<?> crearEstudiante(@RequestBody EstudianteDTO dto) {
        if (!currentUserProvider.hasRole("ADMINISTRADOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // Crear estudiante...
        return ResponseEntity.ok(...);
    }
}
```

---

## DTOs Principales

### 1. **LoginRequestDTO**
Solicitud de autenticación:
```json
{
  "email": "usuario@example.com",
  "password": "password123"
}
```

### 2. **LoginResponseDTO**
Respuesta con tokens:
```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "expiresIn": 3600,
  "tokenType": "Bearer",
  "scope": "openid email profile"
}
```

### 3. **AuthenticatedUserDTO**
Información del usuario actual extraída del JWT:
```java
public class AuthenticatedUserDTO {
    String keycloakUserId;    // ID único del usuario en Keycloak
    String email;             // Email del usuario
    String name;              // Nombre completo
    List<String> roles;       // Roles asignados
    Long issuedAt;            // Timestamp de emisión
    Long expiresAt;           // Timestamp de expiración
}
```

---

## Endpoints de Autenticación

### 1. **Login**
```
POST /api/auth/login
Content-Type: application/json
```

**Request:**
```json
{
  "email": "usuario@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJrZXktaWQifQ...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "tokenType": "Bearer"
}
```

**Error (401):**
```json
{
  "error": "Invalid credentials"
}
```

---

### 2. **Refresh Token**
```
POST /api/auth/refresh
Content-Type: application/json
```

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJrZXktaWQifQ...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "tokenType": "Bearer"
}
```

---

### 3. **Logout**
```
POST /api/auth/logout
Authorization: Bearer {accessToken}
```

**Response (200 OK):**
```json
{
  "message": "Sesión cerrada exitosamente. Por favor, elimina el token almacenado."
}
```

> **Nota:** El logout es del lado del cliente. Simplemente descarta el token. En futuras iteraciones se puede implementar un blacklist con Redis.

---

### 4. **Validate Token**
```
POST /api/auth/validate?token={token}
```

**Response (200 OK - Token válido):**
```json
{
  "valid": true,
  "keycloakUserId": "uuid-del-usuario",
  "email": "usuario@example.com",
  "message": "Token válido"
}
```

**Response (200 OK - Token inválido):**
```json
{
  "valid": false,
  "message": "Token inválido o expirado"
}
```

---

## Cómo Usar en Otros Contextos

El módulo de seguridad se integra en otros contextos simplemente incluyéndolo como dependencia.

### 1. **Agregar dependencia en build.gradle**

```gradle
dependencies {
    implementation project(':shared:security')
}
```

### 2. **Obtener información del usuario actual**

```java
@Service
@RequiredArgsConstructor
public class EstudianteService {
    
    private final CurrentUserProvider currentUserProvider;
    
    public void crearEstudiante(EstudianteDTO dto) {
        // Obtener usuario actual
        AuthenticatedUserDTO currentUser = currentUserProvider.getCurrentUser();
        
        // Acceder a propiedades
        String keycloakUserId = currentUserProvider.getCurrentUserId();
        String email = currentUserProvider.getCurrentEmail();
        
        // Verificar roles
        if (currentUserProvider.hasRole("ADMINISTRADOR")) {
            // Crear estudiante
        }
    }
}
```

### 3. **Usar anotaciones de Spring Security**

```java
@RestController
@RequestMapping("/api/fichas")
public class FichaController {
    
    // Permitir solo usuarios autenticados
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<?> listarFichas() { ... }
    
    // Permitir solo ASESOR y ADMINISTRADOR
    @PreAuthorize("hasAnyRole('ASESOR', 'ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<?> crearFicha(@RequestBody FichaDTO dto) { ... }
    
    // Combinaciones complejas
    @PreAuthorize("hasRole('ASESOR') and @auditoriaService.isOwner(#fichaId)")
    @PutMapping("/{fichaId}")
    public ResponseEntity<?> actualizarFicha(
        @PathVariable Long fichaId,
        @RequestBody FichaDTO dto
    ) { ... }
}
```

### 4. **Proteger rutas en SecurityConfig**

El módulo ya configura automáticamente:
- ✅ **Públicas:** `/api/auth/login`, `/api/auth/refresh`, `/api/auth/validate`, `/actuator/health/**`
- ✅ **Protegidas:** Todos los demás endpoints requieren token JWT válido

Para agregar más rutas públicas, sobrescribe `SecurityConfig`:

```java
@Configuration
public class CustomSecurityConfig extends SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
        );
        return http.build();
    }
}
```

---

## Flujo de Autenticación

```
┌─────────────┐
│   Usuario   │
└──────┬──────┘
       │
       ├─ 1. POST /api/auth/login (email, password)
       │
       v
┌──────────────────┐
│   AuthController │─┐
└──────┬───────────┘ │
       │             │
       ├─ 2. KeycloakAuthService.authenticate()
       │
       v
┌──────────────────────┐
│   Keycloak Server    │
│  (OAuth2/OIDC)       │
└──────┬───────────────┘
       │
       ├─ 3. Valida credenciales
       ├─ 4. Emite JWT con roles en claims
       │
       v
┌──────────────────────┐
│  Login Response      │
│  - accessToken       │
│  - refreshToken      │
│  - expiresIn         │
└──────────────────────┘
       │
       ├─ 5. Cliente almacena tokens
       │
       v
┌──────────────────────┐
│   Cliente            │
│ (SPA/Mobile)         │
│ Authorization Header │
│ Bearer {accessToken} │
└──────────────────────┘
       │
       ├─ 6. Solicita recurso protegido
       │
       v
┌──────────────────┐
│   Spring Boot    │
│   Application    │
└──────┬───────────┘
       │
       ├─ 7. RateLimitingFilter (comprueba límite por IP)
       ├─ 8. AuditFilter (registra acceso)
       ├─ 9. SecurityFilterChain (valida JWT)
       ├─ 10. JwtDecoder (valida firma con clave pública de Keycloak)
       ├─ 11. JwtTokenProviderImpl (extrae roles del JWT)
       ├─ 12. SecurityContext cargado con roles
       │
       v
┌──────────────────┐
│   Controlador    │
│   @PreAuthorize  │
│   Lógica         │
└──────────────────┘
       │
       ├─ 13. Respuesta al cliente
       │
       v
┌──────────────────┐
│   Usuario        │
└──────────────────┘
```

---

## Configuración CORS

El módulo configura automáticamente CORS mediante propiedades.

### Propiedades disponibles:

```properties
# Orígenes permitidos (separados por comas)
security.cors.allowed-origins=http://localhost:3000,http://localhost:4200,https://app.example.com

# Métodos HTTP permitidos
security.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS,PATCH

# Headers permitidos
security.cors.allowed-headers=*

# Tiempo de caché de preflight (segundos)
security.cors.max-age=3600

# Permitir credenciales (cookies, auth headers)
security.cors.allow-credentials=true
```

### Ejemplo configuración por ambiente:

```properties
# application-dev.properties
security.cors.allowed-origins=http://localhost:3000,http://localhost:4200

# application-prod.properties
security.cors.allowed-origins=https://app.example.com,https://admin.example.com
```

---

## Configuración Rate Limiting

Protege contra ataques de fuerza bruta y DDoS.

### Propiedades disponibles:

```properties
# Habilitar/deshabilitar
security.rate-limit.enabled=true

# Solicitudes por minuto (endpoints generales)
security.rate-limit.requests-per-minute=100

# Solicitudes por minuto (endpoint de login - más restrictivo)
security.rate-limit.login-requests-per-minute=5
```

### Respuestas:

**Éxito (bajo límite):**
```
HTTP 200 OK
X-Rate-Limit-Remaining: 47
```

**Excedido límite:**
```
HTTP 429 Too Many Requests
X-Rate-Limit-Retry-After-Seconds: 45
Content: "Has excedido el límite de solicitudes. Intenta de nuevo en 45 segundos."
```

---

## Auditoría

El módulo registra automáticamente todos los accesos con información de:

- 📍 **IP del cliente** (considerando proxies)
- 👤 **Usuario autenticado** (keycloak_user_id)
- 📨 **Método HTTP y ruta**
- 🔢 **Código de respuesta HTTP**
- ⏱️ **Duración de la solicitud**
- 📅 **Timestamp del evento**

### Ejemplo de logs:

```
2024-01-13 10:30:45 INFO  AUDIT [192.168.1.100] GET /api/estudiantes - User: uuid-usuario - Status: 200 - Duration: 45ms
2024-01-13 10:30:46 WARN  AUDIT [192.168.1.101] POST /api/fichas - User: ANONYMOUS - Status: 401 - Duration: 15ms
2024-01-13 10:30:47 ERROR AUDIT [192.168.1.102] DELETE /api/usuarios/123 - User: uuid-admin - Status: 500 - Duration: 234ms
```

### Configuración de logs:

```properties
logging.level.com.arquisoft.shared.security.infrastructure.filters.AuditFilter=INFO
```

---

## Integración con Base de Datos (Usuarios Locales)

Aunque Keycloak es la fuente de verdad para autenticación, normalmente necesitarás una tabla local de usuarios para asociarlos con tus recursos.

### Esquema recomendado:

```sql
CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    keycloak_user_id VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    nombre VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT true
);

-- Ejemplo: tabla de estudiantes con FK a usuarios
CREATE TABLE estudiantes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    numero_documento VARCHAR(50) UNIQUE NOT NULL,
    primer_nombre VARCHAR(100),
    primer_apellido VARCHAR(100),
    fecha_ingreso DATE,
    activo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Ejemplo: tabla de asesores con FK a usuarios
CREATE TABLE asesores (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL REFERENCES usuarios(id),
    especialidad VARCHAR(100),
    departamento VARCHAR(100),
    activo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Sincronización en login:

```java
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final KeycloakAuthService keycloakAuthService;
    private final UsuarioRepository usuarioRepository;
    
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        // Autenticar contra Keycloak
        LoginResponseDTO response = keycloakAuthService.authenticate(request);
        
        // Decodificar token para obtener información
        AuthenticatedUserDTO userInfo = jwtTokenProvider
                .extractUserFromToken(response.getAccessToken());
        
        // Sincronizar/crear usuario local
        Usuario usuario = usuarioRepository
                .findByKeycloakUserId(userInfo.getKeycloakUserId())
                .orElseGet(() -> new Usuario(
                        userInfo.getKeycloakUserId(),
                        userInfo.getEmail(),
                        userInfo.getName()
                ));
        
        usuarioRepository.save(usuario);
        
        return response;
    }
}
```

---

## Manejo de Errores

### Excepciones personalizada:

```java
public class AuthenticationException extends RuntimeException { }
public class InvalidCredentialsException extends AuthenticationException { }
public class InvalidTokenException extends AuthenticationException { }
```

### Implementar manejador global de excepciones:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Credenciales inválidas", ex.getMessage()));
    }
    
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Token inválido", ex.getMessage()));
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Error de autenticación", ex.getMessage()));
    }
}
```

---

## Pruebas

### Test de autenticación:

```java
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private KeycloakAuthService keycloakAuthService;
    
    @Test
    void testLoginSuccess() throws Exception {
        LoginResponseDTO response = LoginResponseDTO.builder()
                .accessToken("test-token")
                .expiresIn(3600)
                .build();
        
        when(keycloakAuthService.authenticate(any()))
                .thenReturn(response);
        
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"pass123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("test-token"));
    }
}
```

---

## Variables de Entorno

Para configuración por ambiente:

```bash
# Keycloak
KEYCLOAK_URL=https://keycloak.example.com/auth
KEYCLOAK_REALM=emboliados
KEYCLOAK_CLIENT_ID=emboliados-app
KEYCLOAK_CLIENT_SECRET=tu-client-secret

# JWT
JWT_ACCESS_TOKEN_EXPIRATION=3600000  # 1 hora en ms

# CORS
CORS_ALLOWED_ORIGINS=https://app.example.com
CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,OPTIONS,PATCH

# Rate Limit
RATE_LIMIT_ENABLED=true
RATE_LIMIT_REQUESTS_PER_MINUTE=100
RATE_LIMIT_LOGIN_REQUESTS_PER_MINUTE=5
```

---

## Seguridad en Producción

### Checklist de seguridad:

- ✅ **HTTPS/SSL obligatorio** para todas las comunicaciones
- ✅ **Keycloak detrás de firewall** - no accesible públicamente
- ✅ **Client Secret** protegido en variables de entorno
- ✅ **CORS restrictivo** - solo dominios necesarios
- ✅ **Rate Limiting habilitado** - protección contra fuerza bruta
- ✅ **Auditoría activa** - revisar logs regularmente
- ✅ **Refresh Token expiration** configurado (por defecto 7 días)
- ✅ **JWT signature validation** - siempre validar firma

---

## Roadmap Futuro

- [ ] Integración con Redis para blacklist de tokens (logout real)
- [ ] MFA (Multi-Factor Authentication)
- [ ] OAuth2 Social Login (Google, GitHub)
- [ ] Auditoría persistente en BD
- [ ] Rate Limiting por usuario (no solo por IP)
- [ ] Integración con WAF (Web Application Firewall)
- [ ] SAML2 support para integración empresarial

---

## Contacto y Soporte

Para preguntas o problemas relacionados con el módulo de seguridad, consulta la documentación de [Spring Security](https://spring.io/projects/spring-security) y [Keycloak](https://www.keycloak.org/docs).

