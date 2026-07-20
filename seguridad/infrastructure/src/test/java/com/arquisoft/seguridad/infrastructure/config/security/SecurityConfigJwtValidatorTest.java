package com.arquisoft.seguridad.infrastructure.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigJwtValidatorTest {

    private static final String ISSUER = "https://keycloak.example.com/realms/arquisoft";
    private static final String EXPECTED_AUDIENCE = "arquisoft-api";

    private final OAuth2TokenValidator<Jwt> validator =
            SecurityConfig.jwtValidator(ISSUER, EXPECTED_AUDIENCE);

    private Jwt jwt(String issuer, List<String> audiencias) {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .header("typ", "JWT")
                .subject("uuid-estudiante")
                .issuer(issuer)
                .audience(audiencias)
                .issuedAt(Instant.now().minusSeconds(60))
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    @Test
    void debeSerExitoso_cuandoIssuerYAudienciaSonCorrectos() {
        // Act
        OAuth2TokenValidatorResult resultado =
                validator.validate(jwt(ISSUER, List.of(EXPECTED_AUDIENCE)));

        // Assert
        assertThat(resultado.hasErrors()).isFalse();
    }

    @Test
    void debeFallarConInvalidToken_cuandoAudienciaEsIncorrecta() {
        // Act
        OAuth2TokenValidatorResult resultado =
                validator.validate(jwt(ISSUER, List.of("arquisoft-otro-client")));

        // Assert
        assertThat(resultado.hasErrors()).isTrue();
        assertThat(resultado.getErrors())
                .anyMatch(error -> "invalid_token".equals(error.getErrorCode()));
    }

    @Test
    void debeFallar_cuandoIssuerEsIncorrecto() {
        // Act
        OAuth2TokenValidatorResult resultado = validator.validate(
                jwt("https://atacante.example.com/realms/otro", List.of(EXPECTED_AUDIENCE)));

        // Assert
        assertThat(resultado.hasErrors()).isTrue();
    }
}
