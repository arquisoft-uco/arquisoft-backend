# DISEÑO — Cross-Cutting Concerns `shared:redis`

---

## Decisiones de Librerías

### 1. Lettuce (sustituye Jedis)

El `application.yml` actual tiene `jedis.pool`. Lettuce es el cliente por defecto de Spring Boot 4.x: no-blocking, thread-safe, óptimo para Virtual Threads (ADR-008). Se elimina `redis.clients:jedis` del `shared/redis/build.gradle` y se cambia `jedis.pool → lettuce.pool` en `application.yml` / `application-prod.yml`.

### 2. Jackson 3.x — confirmado

`RateLimitingFilter.java` importa `tools.jackson.databind.ObjectMapper`, que es el paquete raíz de Jackson 3.x. Las anotaciones (`@JsonInclude`, `@JsonProperty`) mantienen el paquete `com.fasterxml.jackson.annotation` por compatibilidad de la herramienta. Todo código nuevo usa `tools.jackson.databind.ObjectMapper`.

Para los serializadores de Redis se usa `new GenericJackson2JsonRedisSerializer()` (constructor sin argumentos) — crea su propio `ObjectMapper` con `DefaultTyping.NON_FINAL` habilitado, correcto para cache polimórfico. No se inyecta el `ObjectMapper` de Spring para evitar interferir con la configuración global del bean.

### 3. Bucket4j Lettuce

```groovy
// En seguridad:infrastructure/build.gradle
implementation "com.bucket4j:bucket4j_jdk17-redis-common:${bucket4jVersion}"
implementation "com.bucket4j:bucket4j_jdk17-lettuce:${bucket4jVersion}"
```

Clase central: `io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager`.

### 4. InMemoryBucketResolver — eliminado

Todos los entornos usan Redis. El `RedisBucketResolver` replica exactamente los comportamientos de `InMemoryBucketResolver`:

- `enabled=false` → unlimited bucket
- Redis no disponible (fail-closed) → exhausted bucket (idéntico al `createExhaustedBucket()` existente)
- Normal → `LettuceBasedProxyManager` distribuido

El campo `maxTrackedIps` de `RateLimitProperties` se elimina junto con el resolver.

---

## Arquitectura de la Solución

```
shared:redis
├── config/
│   ├── RedisConfig.java          ← @Bean RedisTemplate<String,Object> (Jackson) + StringRedisTemplate auto-conf
│   └── RedisCacheConfig.java     ← @EnableCaching + RedisCacheManager (prefijo "arquisoft:", TTL 30min)
└── RedisClientImpl.java          ← implementa RedisClient existente

seguridad:application/auth/port/  ← (patrón existente: TokenPort, CurrentUserPort, AuthenticationPort)
└── TokenBlacklistPort.java       ← contrato: invalidarToken(jti, ttl) + estaInvalidado(jti)

seguridad:application/auth/command/
├── LogoutCommand.java            ← record inmutable: jti + ttlSegundos
├── LogoutUseCase.java            ← interface: ejecutar(LogoutCommand)
└── LogoutUseCaseImpl.java        ← @Service, delega a TokenBlacklistPort

seguridad:infrastructure
├── adapter/out/redis/
│   └── RedisTokenBlacklistAdapter.java  ← implementa TokenBlacklistPort via StringRedisTemplate
├── filter/
│   └── JwtBlacklistFilter.java          ← OncePerRequestFilter, @Order(-100), chequea JTI por request
└── config/ratelimit/
    └── RedisBucketResolver.java         ← sustituye InMemoryBucketResolver (1:1)
```

---

## Árbol de Archivos

### Nuevos (10)

```
shared/redis/src/main/java/com/arquisoft/shared/redis/
├── config/
│   ├── RedisConfig.java
│   └── RedisCacheConfig.java
└── RedisClientImpl.java

seguridad/application/src/main/java/com/arquisoft/seguridad/application/auth/
├── port/
│   └── TokenBlacklistPort.java
└── command/
    ├── LogoutCommand.java
    ├── LogoutUseCase.java
    └── LogoutUseCaseImpl.java

seguridad/infrastructure/src/main/java/com/arquisoft/seguridad/infrastructure/
├── adapter/out/redis/
│   └── RedisTokenBlacklistAdapter.java
├── filter/
│   └── JwtBlacklistFilter.java
└── config/ratelimit/
    └── RedisBucketResolver.java
```

### Eliminados (1)

```
seguridad/infrastructure/src/main/java/.../config/ratelimit/InMemoryBucketResolver.java  ← ELIMINAR
```

### Modificados (7)

```
shared/redis/build.gradle
seguridad/infrastructure/build.gradle
seguridad/infrastructure/.../config/ratelimit/RateLimitProperties.java  (quitar maxTrackedIps)
seguridad/infrastructure/.../config/security/SecurityConfig.java         (registrar JwtBlacklistFilter)
seguridad/infrastructure/.../adapter/in/web/AuthController.java          (logout completo)
src/main/resources/application.yml                                       (jedis → lettuce)
src/main/resources/application-prod.yml                                  (jedis → lettuce)
```

---

## Detalle de Cada Archivo

### `shared/redis/build.gradle`

```groovy
dependencies {
    implementation project(':shared:domain')

    // Spring Data Redis — Lettuce es el cliente por defecto cuando Jedis no está en classpath
    implementation "org.springframework.boot:spring-boot-starter-data-redis"

    // Pool de conexiones para Lettuce
    implementation "org.apache.commons:commons-pool2"

    compileOnly "org.projectlombok:lombok:${lombokVersion}"
    annotationProcessor "org.projectlombok:lombok:${lombokVersion}"
    // ELIMINADO: redis.clients:jedis
}
```

---

### `RedisConfig.java`

```java
package com.arquisoft.shared.redis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * Sobreescribe el RedisTemplate<Object,Object> por defecto de Spring Boot,
     * que usa serialización JDK (no legible, no portable entre JVMs).
     * Usa String como clave y JSON Jackson 3.x como valor.
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Constructor sin argumentos: crea su propio ObjectMapper con DefaultTyping.NON_FINAL
        // para soportar deserialización polimórfica (tipo guardado como @class en el JSON).
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
    // StringRedisTemplate es auto-configurado por Spring Boot — no se redeclara.
}
```

---

### `RedisCacheConfig.java`

```java
package com.arquisoft.shared.redis.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    /**
     * CacheManager global para todos los bounded contexts.
     *
     * Prefijo de clave: "arquisoft:"
     * Convención de uso: @Cacheable(value = "{contexto}:{nombre}", key = "#param")
     * Ejemplo key Redis: arquisoft:proyectos:activos::{uuid}
     *
     * TTL por defecto: 30 minutos.
     * Para un TTL diferente, crear un RedisCacheManagerBuilderCustomizer
     * en el bounded context o declarar configuraciones de cache especificas.
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .prefixCacheNameWith("arquisoft:")
                .serializeValuesWith(SerializationPair.fromSerializer(jsonSerializer))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .build();
    }
}
```

**Convención de nombres de caché:**

| Bounded context | `value` en `@Cacheable` | Clave Redis resultante |
|---|---|---|
| `proyectos` | `"proyectos:activos"` | `arquisoft:proyectos:activos::{key}` |
| `fichas` | `"fichas:perfil-resumen"` | `arquisoft:fichas:perfil-resumen::{key}` |
| `evaluaciones` | `"evaluaciones:por-proyecto"` | `arquisoft:evaluaciones:por-proyecto::{key}` |

---

### `RedisClientImpl.java`

```java
package com.arquisoft.shared.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisClientImpl implements RedisClient {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        return type.isInstance(value) ? (T) value : null;
    }

    @Override
    public void set(String key, Object value, long ttlSeconds) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds));
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public long increment(String key) {
        Long result = stringRedisTemplate.opsForValue().increment(key);
        return result != null ? result : 0L;
    }
}
```

---

### `TokenBlacklistPort.java` (en `seguridad:application/auth/port/`)

```java
package com.arquisoft.seguridad.application.auth.port;

/**
 * Puerto de salida para la blacklist de tokens JWT revocados explicitamente.
 * Sigue el patron del contexto seguridad: TokenPort, CurrentUserPort, AuthenticationPort.
 *
 * Estrategia anti-saturacion: el TTL de cada entrada = vida restante del token.
 * Redis expira automaticamente la entrada cuando el token habria caducado de todas formas.
 * El tamano maximo del blacklist ≈ sesiones activas con logout pendiente, no el total historico.
 */
public interface TokenBlacklistPort {

    /**
     * Registra el JTI del token como revocado.
     * @param jti         claim "jti" del JWT (identificador unico)
     * @param ttlSegundos segundos restantes de vida del token
     */
    void invalidarToken(String jti, long ttlSegundos);

    /**
     * Verifica si el token fue revocado explicitamente.
     * @param jti claim "jti" del JWT
     * @return true si el token esta en la blacklist
     */
    boolean estaInvalidado(String jti);
}
```

---

### `LogoutCommand.java`

```java
package com.arquisoft.seguridad.application.auth.command;

import java.util.Objects;

/**
 * Comando inmutable que representa la intencion de revocar un token JWT.
 */
public record LogoutCommand(String jti, long ttlSegundos) {

    public LogoutCommand {
        Objects.requireNonNull(jti, "jti no puede ser null");
        if (ttlSegundos <= 0) {
            throw new IllegalArgumentException("ttlSegundos debe ser > 0");
        }
    }
}
```

### `LogoutUseCase.java`

```java
package com.arquisoft.seguridad.application.auth.command;

public interface LogoutUseCase {
    void ejecutar(LogoutCommand command);
}
```

### `LogoutUseCaseImpl.java`

```java
package com.arquisoft.seguridad.application.auth.command;

import com.arquisoft.seguridad.application.auth.port.TokenBlacklistPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutUseCaseImpl implements LogoutUseCase {

    private final TokenBlacklistPort tokenBlacklistPort;

    @Override
    public void ejecutar(LogoutCommand command) {
        tokenBlacklistPort.invalidarToken(command.jti(), command.ttlSegundos());
    }
}
```

---

### `RedisTokenBlacklistAdapter.java`

```java
package com.arquisoft.seguridad.infrastructure.adapter.out.redis;

import com.arquisoft.seguridad.application.auth.port.TokenBlacklistPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisTokenBlacklistAdapter implements TokenBlacklistPort {

    private static final String PREFIX = "arquisoft:blacklist:jti:";
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void invalidarToken(String jti, long ttlSegundos) {
        // SET arquisoft:blacklist:jti:{jti} 1 EX {ttl}
        // TTL = vida restante del token → Redis expira la entrada cuando el token caducaria
        stringRedisTemplate.opsForValue()
                .set(PREFIX + jti, "1", Duration.ofSeconds(ttlSegundos));
    }

    @Override
    public boolean estaInvalidado(String jti) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(PREFIX + jti));
    }
}
```

---

### `JwtBlacklistFilter.java`

```java
package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.seguridad.application.auth.port.TokenBlacklistPort;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Verifica la blacklist de tokens en cada request autenticado.
 * Registrado en SecurityConfig con addFilterAfter(BearerTokenAuthenticationFilter),
 * de modo que SecurityContextHolder ya tiene la autenticacion verificada.
 *
 * Patron de respuesta: identico a RateLimitingFilter — ObjectMapper + ErrorResponseDTO.
 * Los handlers (@RestControllerAdvice) no aplican a filtros.
 *
 * Fail-closed:
 *   - Token en blacklist → 401 + log.warn
 *   - Redis no disponible → 503 + log.error (servicio externo caido)
 *     SecurityContext se limpia en ambos casos.
 */
@Slf4j
@Component
@Order(-100)
@RequiredArgsConstructor
public class JwtBlacklistFilter extends OncePerRequestFilter {

    private final TokenBlacklistPort tokenBlacklistPort;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.startsWith("/api/swagger-ui")
                || uri.startsWith("/api/v3/api-docs")
                || uri.startsWith("/api/swagger-resources")
                || uri.startsWith("/api/actuator")
                || uri.equals("/api/auth/login")
                || uri.equals("/api/auth/refresh")
                || uri.equals("/api/auth/validate");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = (Jwt) jwtAuth.getCredentials();
            String jti = jwt.getId();

            if (jti != null) {
                try {
                    if (tokenBlacklistPort.estaInvalidado(jti)) {
                        log.warn("Token revocado bloqueado: JTI='{}' path='{}'",
                                jti, request.getRequestURI());
                        writeErrorResponse(response, request,
                                HttpStatus.UNAUTHORIZED,
                                "Unauthorized",
                                "TOKEN_REVOCADO",
                                "El token ha sido revocado por logout explicito");
                        return;
                    }
                } catch (Exception e) {
                    log.error("Blacklist Redis no disponible, denegando request (fail-closed): {}",
                            e.getMessage(), e);
                    writeErrorResponse(response, request,
                            HttpStatus.SERVICE_UNAVAILABLE,
                            "Service Unavailable",
                            "BLACKLIST_SERVICE_UNAVAILABLE",
                            "Servicio de autenticacion temporalmente no disponible");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private void writeErrorResponse(HttpServletResponse response,
                                    HttpServletRequest request,
                                    HttpStatus status,
                                    String error,
                                    String errorCode,
                                    String message) throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(),
                ErrorResponseDTO.builder()
                        .error(error)
                        .errorCode(errorCode)
                        .message(message)
                        .status(status.value())
                        .path(request.getRequestURI())
                        .build());
    }
}
```

---

### `RedisBucketResolver.java` — sustituye `InMemoryBucketResolver` 1:1

```java
package com.arquisoft.seguridad.infrastructure.config.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.Duration;

/**
 * Implementacion distribuida de {@link BucketResolver} usando Bucket4j + Lettuce (Redis).
 *
 * Sustituye InMemoryBucketResolver. Comportamientos preservados:
 *   - rate-limit.enabled=false  → unlimited bucket (mismo que antes)
 *   - Redis no disponible       → exhausted bucket, fail-closed (igual que maxTrackedIps excedido)
 *   - bucket login              → refillGreedy (anti-ventana-fija, igual que antes)
 *   - bucket general            → refillIntervally (igual que antes)
 *
 * Ventajas sobre la implementacion en memoria:
 *   - Estado compartido entre instancias (horizontal scaling)
 *   - Sin limite de IPs rastreadas (Redis gestiona la eviction por TTL)
 *   - Sin @Scheduled de eviction manual
 *   - Estado sobrevive reinicios de la app
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisBucketResolver implements BucketResolver, DisposableBean {

    private final RateLimitProperties properties;
    private final LettuceConnectionFactory lettuceConnectionFactory;

    private LettuceBasedProxyManager<String> proxyManager;
    private StatefulRedisConnection<String, byte[]> bucketConnection;

    @PostConstruct
    public void init() {
        Object nativeClient = lettuceConnectionFactory.getRequiredNativeClient();
        if (nativeClient instanceof RedisClient redisClient) {
            this.bucketConnection = redisClient.connect(
                    RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
            this.proxyManager = LettuceBasedProxyManager.builderFor(bucketConnection)
                    .expirationAfterWrite(ExpirationAfterWriteStrategy
                            .basedOnTimeForRefillingBucketUpToMax(Duration.ofMinutes(2)))
                    .build();
            log.debug("RedisBucketResolver inicializado (Lettuce-backed distributed rate limiting)");
        } else {
            throw new IllegalStateException(
                    "RedisBucketResolver requiere RedisClient standalone. " +
                    "Configuracion de Lettuce incorrecta o cliente es Cluster/Sentinel.");
        }
    }

    @Override
    public Bucket resolveBucket(String ip) {
        if (!properties.enabled()) return createUnlimitedBucket();
        try {
            return proxyManager.getProxy(
                    "arquisoft:ratelimit:global:" + ip,
                    () -> BucketConfiguration.builder()
                            .addLimit(limit -> limit
                                    .capacity(properties.requestsPerMinute())
                                    .refillIntervally(properties.requestsPerMinute(), Duration.ofMinutes(1)))
                            .build());
        } catch (Exception e) {
            log.error("Error al resolver bucket Redis para IP={}, aplicando fail-closed: {}",
                    ip, e.getMessage());
            return createExhaustedBucket();
        }
    }

    @Override
    public Bucket resolveLoginBucket(String ip) {
        if (!properties.enabled()) return createUnlimitedBucket();
        try {
            return proxyManager.getProxy(
                    "arquisoft:ratelimit:login:" + ip,
                    () -> BucketConfiguration.builder()
                            .addLimit(limit -> limit
                                    .capacity(properties.loginRequestsPerMinute())
                                    .refillGreedy(properties.loginRequestsPerMinute(), Duration.ofMinutes(1)))
                            .build());
        } catch (Exception e) {
            log.error("Error al resolver login bucket Redis para IP={}, aplicando fail-closed: {}",
                    ip, e.getMessage());
            return createExhaustedBucket();
        }
    }

    @Override
    public boolean isRateLimitEnabled() {
        return properties.enabled();
    }

    @Override
    public void destroy() {
        if (bucketConnection != null) {
            bucketConnection.close();
        }
    }

    /** Bucket exhausto: cualquier peticion recibe HTTP 429 de inmediato. Fail-closed. */
    private Bucket createExhaustedBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(1)
                .refillIntervally(1, Duration.ofDays(1))
                .build();
        Bucket bucket = Bucket.builder().addLimit(limit).build();
        bucket.tryConsume(1);
        return bucket;
    }

    /** Bucket ilimitado: usado cuando rate-limit.enabled=false. */
    private Bucket createUnlimitedBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(Long.MAX_VALUE)
                        .refillIntervally(Long.MAX_VALUE, Duration.ofDays(1))
                        .build())
                .build();
    }
}
```

---

### `RateLimitProperties.java` — eliminar `maxTrackedIps`

```java
package com.arquisoft.seguridad.infrastructure.config.ratelimit;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "security.rate-limit")
public record RateLimitProperties(
        boolean enabled,
        @Min(value = 1, message = "requests-per-minute debe ser >= 1") int requestsPerMinute,
        @Min(value = 1, message = "login-requests-per-minute debe ser >= 1") int loginRequestsPerMinute
) {
}
```

---

### `AuthController.java` — metodo `logout` mejorado

```java
// Campo a agregar (constructor injection via @RequiredArgsConstructor):
private final LogoutUseCase logoutUseCase;

@PostMapping("/logout")
@Operation(
    summary = "Cerrar sesion",
    description = "Invalida el token JWT actual en la blacklist de Redis. El token queda " +
                  "rechazado hasta su expiracion natural aunque se presente con firma valida.",
    security = @SecurityRequirement(name = "bearerAuth")
)
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Sesion cerrada"),
    @ApiResponse(responseCode = "401", description = "No autenticado")
})
public ResponseEntity<LogoutResponseDTO> logout(@AuthenticationPrincipal Jwt jwt) {
    // jwt != null garantizado: /auth/logout requiere autenticacion (anyRequest().authenticated())
    String jti = jwt.getId();

    if (jti == null) {
        log.warn("Logout con token sin claim 'jti' — invalidacion omitida");
        return ResponseEntity.ok(LogoutResponseDTO.builder().build());
    }

    Instant expiresAt = jwt.getExpiresAt();
    long remainingSeconds = expiresAt != null
            ? Math.max(0, Duration.between(Instant.now(), expiresAt).toSeconds())
            : 0;

    if (remainingSeconds <= 0) {
        log.warn("Logout con token ya expirado o sin expiracion: JTI='{}'", jti);
        return ResponseEntity.ok(LogoutResponseDTO.builder().build());
    }

    logoutUseCase.ejecutar(new LogoutCommand(jti, remainingSeconds));
    log.warn("Logout exitoso — token invalidado: JTI='{}', TTL={}s", jti, remainingSeconds);
    return ResponseEntity.ok(LogoutResponseDTO.builder().build());
}
```

---

### `SecurityConfig.java` — registrar `JwtBlacklistFilter`

```java
// Campo a agregar:
private final JwtBlacklistFilter jwtBlacklistFilter;

// En filterChain(), despues del bloque .oauth2ResourceServer(...):
http.addFilterAfter(jwtBlacklistFilter, BearerTokenAuthenticationFilter.class);
```

---

### `application.yml` — cambios

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      timeout: 2000ms
      lettuce:                    # ← era "jedis:"
        pool:
          max-active: 10
          max-idle: 5
          min-idle: 0

security:
  rate-limit:
    enabled: true
    requests-per-minute: 100
    login-requests-per-minute: 5
    # maxTrackedIps eliminado
```

### `application-prod.yml` — cambios

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}
      timeout: 3000ms
      lettuce:                    # ← era "jedis:"
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5

security:
  rate-limit:
    enabled: true
    requests-per-minute: 60
    login-requests-per-minute: 3
    # maxTrackedIps eliminado
```

---

## Resumen de Cambios

| Modulo | Nuevos | Eliminados | Modificados |
|---|---|---|---|
| `shared:redis` | 3 | 0 | 1 (build.gradle) |
| `seguridad:application` | 4 | 0 | 0 |
| `seguridad:infrastructure` | 3 | 1 (InMemoryBucketResolver) | 4 |
| Config global | 0 | 0 | 2 (yml) |
| **Total** | **10** | **1** | **7** |

---

## Sin Tests

`shared:redis` no tiene tests unitarios segun instruccion.
Los tests de `seguridad` existentes deberan actualizarse si inyectan `InMemoryBucketResolver` o el `AuthController`.
