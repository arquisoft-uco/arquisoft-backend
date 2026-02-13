# Módulo de Seguridad - Emboliados

Módulo centralizado de autenticación y autorización para toda la plataforma usando Spring Security, JWT y Keycloak.

> **Nota:** El despliegue de Keycloak se gestiona en un repositorio de infraestructura separado. Este módulo solo consume los tokens JWT emitidos por Keycloak.

## Estructura de Archivos

```
shared/security/
├── README.md                              # Este archivo
├── MODULO_SEGURIDAD.md                   # Documentación del módulo completo
├── EJEMPLO_INTEGRACION.md                # Ejemplos de integración
│
├── docs/                                 # Documentación adicional
│   ├── RESUMEN_MODULO_SEGURIDAD.md      # Resumen ejecutivo
│   └── CHECKLIST_MODULO_SEGURIDAD.md    # Checklist de implementación
│
├── application/                          # Capa de aplicación
│   ├── build.gradle
│   └── src/
│       └── main/java/com/arquisoft/shared/security/application/
│           ├── CurrentUserProvider.java  # Interfaz para usuario actual
│           ├── services/
│           │   ├── JwtTokenProvider.java # Interfaz JWT
│           │   └── KeycloakAuthService.java  # Interfaz Keycloak
│           └── impl/
│               └── CurrentUserProviderImpl.java
│
├── domain/                               # Capa de dominio
│   ├── build.gradle
│   └── src/
│       └── main/java/com/arquisoft/shared/security/domain/
│           ├── dto/
│           │   ├── AuthenticatedUserDTO.java
│           │   ├── LoginRequestDTO.java
│           │   ├── LoginResponseDTO.java
│           │   └── RefreshTokenRequestDTO.java
│           ├── exceptions/
│           │   ├── AuthenticationException.java
│           │   ├── InvalidCredentialsException.java
│           │   └── InvalidTokenException.java
│           └── enums/
│               └── UserRole.java          # 8 roles predefinidos
│
├── infrastructure/                       # Capa de infraestructura
│   ├── build.gradle
│   └── src/
│       └── main/java/com/arquisoft/shared/security/infrastructure/
│           ├── keycloak/
│           │   ├── JwtTokenProviderImpl.java
│           │   └── KeycloakAuthServiceImpl.java
│           ├── config/
│           │   ├── SecurityConfig.java
│           │   ├── CorsConfig.java
│           │   └── RateLimitConfig.java
│           ├── filters/
│           │   ├── RateLimitingFilter.java
│           │   └── AuditFilter.java
│           └── rest/
│               └── AuthController.java    # Endpoints auth
│
└── build.gradle                          # Configuración Gradle del módulo
```

## Configuración de Spring Boot

En `src/main/resources/application.properties`:

```properties
# Importar configuración de seguridad
spring.config.import=classpath:application-security.properties

# O copiar contenido de shared/security/application-security.properties
```

## Roles Disponibles

El módulo define 8 roles predefinidos:

1. **ADMINISTRADOR** - Acceso total al sistema
2. **COORDINADOR** - Coordinación de procesos
3. **ASESOR** - Asesoría general
4. **ASESOR_FICHA** - Asesor especializado en fichas
5. **JURADO** - Funciones de evaluación
6. **BIBLIOTECARIO** - Gestión de biblioteca
7. **ESTUDIANTE** - Acceso como estudiante
8. **REPRESENTANTE_COMITE_CURRICULUM** - Representación en comités

## Endpoints de Autenticación

Todos disponibles en `/api/auth`:

- `POST /login` - Autenticación con usuario/contraseña
- `POST /refresh` - Renovación de token JWT
- `POST /logout` - Cierre de sesión
- `GET /validate` - Validar token actual

## Características

✅ **Autenticación JWT** con Keycloak  
✅ **8 Roles predefinidos** mapeados a Spring Security  
✅ **Rate Limiting** (100 req/min general, 5 req/min login)  
✅ **Audit Logging** de todas las requests  
✅ **CORS configurable** por entorno  
✅ **Refresh Tokens** (1 hora default)  

## Documentación Completa

Ver carpeta `shared/security/`:

- **[MODULO_SEGURIDAD.md](MODULO_SEGURIDAD.md)** - Documentación técnica completa
- **[EJEMPLO_INTEGRACION.md](EJEMPLO_INTEGRACION.md)** - Ejemplos de integración
- **[docs/RESUMEN_MODULO_SEGURIDAD.md](docs/RESUMEN_MODULO_SEGURIDAD.md)** - Resumen ejecutivo
- **[docs/CHECKLIST_MODULO_SEGURIDAD.md](docs/CHECKLIST_MODULO_SEGURIDAD.md)** - Checklist de validación

## Integración en Otros Contextos

Para usar este módulo en otros contextos (ejemplo: `contextos/fichas/`):

1. Añadir dependencia en `build.gradle`:
```gradle
implementation project(':shared:security')
```

2. Importar configuración:
```properties
spring.config.import=classpath:application-security.properties
```

3. Inyectar servicios:
```java
@Autowired
private CurrentUserProvider currentUserProvider;

@Autowired
private JwtTokenProvider tokenProvider;

// Usar para obtener usuario actual
AuthenticatedUserDTO user = currentUserProvider.getCurrentUser();
```

4. Asegurar endpoints:
```java
@PostMapping("/endpoint")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ASESOR')")
public ResponseEntity<?> endpoint() {
    // Endpoint protegido
}
```

## Propiedades Configurables

Consultar `shared/security/application-security.properties`:

- `security.jwt.secret` - Secreto para validar JWT
- `security.jwt.expiration` - Expiración de token (ms)
- `security.keycloak.url` - URL de Keycloak
- `security.keycloak.realm` - Realm de Keycloak
- `security.cors.origins` - Orígenes CORS permitidos
- `security.ratelimit.enabled` - Activar rate limiting
- `security.ratelimit.general.requests` - Requests por minuto (general)
- `security.ratelimit.login.requests` - Requests por minuto (login)

## Solución de Problemas

**Error: "Invalid token"**
- Verificar que token no haya expirado
- Usar endpoint `/api/auth/refresh` para renovar
- Validar configuración de Keycloak en `application-security.properties`

**Error: "Access Denied"**
- Verificar que el usuario tenga los roles necesarios en Keycloak
- Revisar las anotaciones `@PreAuthorize` en los endpoints

**Error: "Rate limit exceeded"**
- El sistema limita requests por IP (100/min general, 5/min login)
- Esperar un minuto o ajustar configuración si es necesario

## Soporte

Para problemas o preguntas sobre el módulo de seguridad, consultar la documentación completa.

---

**Versión:** 1.0.0  
**Última actualización:** Febrero 2026  
**Autores:** Equipo Emboliados
