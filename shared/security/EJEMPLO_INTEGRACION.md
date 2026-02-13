# Ejemplo de Integración en Contexto

Este documento muestra cómo integrar el módulo de seguridad en un contexto específico.

## Paso 1: Agregar Dependencia

En `{contexto}/build.gradle`:

```gradle
dependencies {
    implementation project(':shared:security')
    // ... otras dependencias
}
```

## Paso 2: Configurar Propiedades

En `src/main/resources/application.properties` o crear `application-security.properties`:

```properties
# Importar configuración del módulo de seguridad
spring.config.import=classpath:application-security.properties

# Keycloak
keycloak.auth-server-url=${KEYCLOAK_URL:https://localhost:8443/auth}
keycloak.realm=${KEYCLOAK_REALM:emboliados}
keycloak.resource=${KEYCLOAK_CLIENT_ID:emboliados-app}
keycloak.credentials.secret=${KEYCLOAK_CLIENT_SECRET:}

# CORS
security.cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:3000,http://localhost:4200}
security.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS,PATCH

# Rate Limit
security.rate-limit.enabled=true
security.rate-limit.requests-per-minute=100
```

## Paso 3: Usar en Servicios

### Ejemplo 1: Obtener Usuario Actual

```java
@Service
@RequiredArgsConstructor
public class EstudianteService {
    
    private final CurrentUserProvider currentUserProvider;
    private final EstudianteRepository estudianteRepository;
    
    public EstudianteDTO miPerfil() {
        // Obtener usuario autenticado
        String keycloakUserId = currentUserProvider.getCurrentUserId();
        String email = currentUserProvider.getCurrentEmail();
        
        // Buscar estudiante asociado
        Estudiante estudiante = estudianteRepository
                .findByUsuarioKeycloakId(keycloakUserId)
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado"));
        
        return mapper.toDTO(estudiante);
    }
    
    public void actualizarMiPerfil(EstudianteUpdateDTO dto) {
        String keycloakUserId = currentUserProvider.getCurrentUserId();
        
        Estudiante estudiante = estudianteRepository
                .findByUsuarioKeycloakId(keycloakUserId)
                .orElseThrow();
        
        // Actualizar estudiante
        estudiante.setNombre(dto.getNombre());
        estudianteRepository.save(estudiante);
    }
}
```

### Ejemplo 2: Verificar Roles

```java
@Service
@RequiredArgsConstructor
public class FichaService {
    
    private final CurrentUserProvider currentUserProvider;
    private final FichaRepository fichaRepository;
    
    @Transactional
    public void crearFicha(FichaCreateDTO dto) {
        // Verificar que el usuario es ASESOR o ADMINISTRADOR
        if (!currentUserProvider.hasRole("ASESOR") && 
            !currentUserProvider.hasRole("ADMINISTRADOR")) {
            throw new AccessDeniedException("No tienes permiso para crear fichas");
        }
        
        String keycloakUserId = currentUserProvider.getCurrentUserId();
        
        Ficha ficha = new Ficha();
        ficha.setTitulo(dto.getTitulo());
        ficha.setAsesorKeycloakId(keycloakUserId);
        
        fichaRepository.save(ficha);
    }
}
```

### Ejemplo 3: Usar Anotaciones

```java
@RestController
@RequestMapping("/api/evaluaciones")
@RequiredArgsConstructor
public class EvaluacionController {
    
    private final EvaluacionService evaluacionService;
    
    // Solo usuarios autenticados
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<EvaluacionDTO>> listar() {
        return ResponseEntity.ok(evaluacionService.listarMisEvaluaciones());
    }
    
    // Solo JURADO
    @PostMapping("/{evaluacionId}/calificar")
    @PreAuthorize("hasRole('JURADO')")
    public ResponseEntity<?> calificar(
            @PathVariable Long evaluacionId,
            @RequestBody CalificacionDTO calificacion) {
        evaluacionService.calificar(evaluacionId, calificacion);
        return ResponseEntity.ok().build();
    }
    
    // ADMINISTRADOR o COORDINADOR
    @DeleteMapping("/{evaluacionId}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR')")
    public ResponseEntity<?> eliminar(@PathVariable Long evaluacionId) {
        evaluacionService.eliminar(evaluacionId);
        return ResponseEntity.noContent().build();
    }
}
```

## Paso 4: Entidad de Usuario Local

Crea una tabla para mapear usuarios de Keycloak:

```java
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(unique = true, nullable = false)
    private String keycloakUserId;  // ID en Keycloak
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String nombre;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    private boolean activo;
}
```

SQL:
```sql
CREATE TABLE usuarios (
    id UUID PRIMARY KEY,
    keycloak_user_id VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    nombre VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT true
);

CREATE INDEX idx_usuarios_keycloak_id ON usuarios(keycloak_user_id);
CREATE INDEX idx_usuarios_email ON usuarios(email);
```

## Paso 5: Sincronizar Usuario en Login (Opcional)

```java
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final KeycloakAuthService keycloakAuthService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioRepository usuarioRepository;
    
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        // Autenticar contra Keycloak
        LoginResponseDTO response = keycloakAuthService.authenticate(request);
        
        // Extraer información del token
        AuthenticatedUserDTO userInfo = jwtTokenProvider
                .extractUserFromToken(response.getAccessToken());
        
        // Sincronizar usuario local
        Usuario usuario = usuarioRepository
                .findByKeycloakUserId(userInfo.getKeycloakUserId())
                .orElseGet(() -> Usuario.builder()
                        .keycloakUserId(userInfo.getKeycloakUserId())
                        .email(userInfo.getEmail())
                        .nombre(userInfo.getName())
                        .activo(true)
                        .build());
        
        // Actualizar información si cambió
        usuario.setNombre(userInfo.getName());
        usuario.setEmail(userInfo.getEmail());
        usuario.setActivo(true);
        
        usuarioRepository.save(usuario);
        
        return response;
    }
}
```

## Paso 6: Test Unitario

```java
@SpringBootTest
@AutoConfigureMockMvc
class EstudianteServiceTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private CurrentUserProvider currentUserProvider;
    
    @MockBean
    private EstudianteRepository estudianteRepository;
    
    @Autowired
    private EstudianteService estudianteService;
    
    @Test
    void testMiPerfilSuccess() {
        // Setup
        String keycloakUserId = "user-123";
        AuthenticatedUserDTO user = AuthenticatedUserDTO.builder()
                .keycloakUserId(keycloakUserId)
                .email("test@example.com")
                .roles(List.of("ESTUDIANTE"))
                .build();
        
        Estudiante estudiante = Estudiante.builder()
                .id(1L)
                .usuarioKeycloakId(keycloakUserId)
                .nombre("Juan")
                .build();
        
        when(currentUserProvider.getCurrentUserId()).thenReturn(keycloakUserId);
        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(estudianteRepository.findByUsuarioKeycloakId(keycloakUserId))
                .thenReturn(Optional.of(estudiante));
        
        // Execute
        EstudianteDTO result = estudianteService.miPerfil();
        
        // Assert
        assertEquals("Juan", result.getNombre());
        verify(estudianteRepository).findByUsuarioKeycloakId(keycloakUserId);
    }
    
    @Test
    void testCrearFichaSinPermiso() {
        // Setup
        when(currentUserProvider.hasRole("ASESOR")).thenReturn(false);
        when(currentUserProvider.hasRole("ADMINISTRADOR")).thenReturn(false);
        
        FichaCreateDTO dto = FichaCreateDTO.builder().build();
        
        // Execute & Assert
        assertThrows(AccessDeniedException.class, () -> {
            fichaService.crearFicha(dto);
        });
    }
}
```

## Paso 7: Test de Integración

```java
@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private KeycloakAuthService keycloakAuthService;
    
    @Test
    void testLoginFlow() throws Exception {
        // Setup
        LoginResponseDTO loginResponse = LoginResponseDTO.builder()
                .accessToken("test-token")
                .expiresIn(3600)
                .build();
        
        when(keycloakAuthService.authenticate(any()))
                .thenReturn(loginResponse);
        
        // Login
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"pass123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("test-token"));
    }
    
    @Test
    @WithMockUser(roles = "ESTUDIANTE")
    void testProtectedEndpoint() throws Exception {
        mockMvc.perform(get("/api/estudiantes"))
                .andExpect(status().isOk());
    }
    
    @Test
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/estudiantes"))
                .andExpect(status().isUnauthorized());
    }
}
```

## Paso 8: Documentación del Contexto

En tu `README.md` del contexto:

```markdown
## Seguridad

Este contexto utiliza el módulo compartido de seguridad (`shared:security`).

### Autenticación
- Usar endpoint `/api/auth/login` con email y password
- Incluir token en header: `Authorization: Bearer {token}`
- Refrescar token con `/api/auth/refresh`

### Autorización
- Usar anotación `@PreAuthorize` para proteger métodos
- Roles disponibles: ESTUDIANTE, ASESOR, JURADO, ADMINISTRADOR, etc.

### Usuarios Locales
- Los usuarios se sincronizan en tabla `usuarios`
- Campo `keycloak_user_id` mapea al ID en Keycloak

Ver [MODULO_SEGURIDAD.md](../MODULO_SEGURIDAD.md) para más información.
```

---

## Resumen

Para integrar el módulo de seguridad en un nuevo contexto:

1. ✅ Agregar dependencia en `build.gradle`
2. ✅ Configurar propiedades (Keycloak, CORS, Rate Limit)
3. ✅ Inyectar `CurrentUserProvider` donde necesites usuario actual
4. ✅ Usar `@PreAuthorize` en controladores
5. ✅ Crear tabla de sincronización de usuarios (opcional)
6. ✅ Hacer tests unitarios e integración
7. ✅ Documentar en README del contexto

¡Listo! El módulo está completamente integrado y funcional.

