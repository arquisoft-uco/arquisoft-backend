package com.arquisoft.seguridad.infrastructure.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AudienciaValidatorTest {

    private static final String EXPECTED_AUDIENCE = "arquisoft-api";

    private final AudienciaValidator validator = new AudienciaValidator(List.of(EXPECTED_AUDIENCE));

    private Jwt jwtConAudiencia(List<String> audiencias) {
        Jwt.Builder builder = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("uuid-estudiante")
                .issuedAt(Instant.now().minusSeconds(60))
                .expiresAt(Instant.now().plusSeconds(3600));
        if (audiencias != null) {
            builder.audience(audiencias);
        }
        return builder.build();
    }

    @Test
    void debeSerExitoso_cuandoAudContieneLaAudienciaEsperada() {
        // Arrange
        Jwt jwt = jwtConAudiencia(List.of("otra-audiencia", EXPECTED_AUDIENCE));

        // Act
        OAuth2TokenValidatorResult resultado = validator.validate(jwt);

        // Assert
        assertThat(resultado.hasErrors()).isFalse();
    }

    @Test
    void debeFallarConInvalidToken_cuandoAudNoContieneLaAudienciaEsperada() {
        // Arrange
        Jwt jwt = jwtConAudiencia(List.of("arquisoft-otro-client"));

        // Act
        OAuth2TokenValidatorResult resultado = validator.validate(jwt);

        // Assert
        assertThat(resultado.hasErrors()).isTrue();
        assertThat(resultado.getErrors())
                .anyMatch(error -> "invalid_token".equals(error.getErrorCode()));
    }

    @Test
    void debeFallarConInvalidToken_cuandoNoHayClaimAud() {
        // Arrange
        Jwt jwt = jwtConAudiencia(null);

        // Act
        OAuth2TokenValidatorResult resultado = validator.validate(jwt);

        // Assert
        assertThat(resultado.hasErrors()).isTrue();
        assertThat(resultado.getErrors())
                .anyMatch(error -> "invalid_token".equals(error.getErrorCode()));
    }

    @Test
    void debeSerExitoso_cuandoAlgunaDeVariasAudienciasCoincide() {
        // Arrange
        var validadorVarias = new AudienciaValidator(List.of("a", EXPECTED_AUDIENCE));
        Jwt jwt = jwtConAudiencia(List.of(EXPECTED_AUDIENCE));

        // Act
        OAuth2TokenValidatorResult resultado = validadorVarias.validate(jwt);

        // Assert
        assertThat(resultado.hasErrors()).isFalse();
    }

    @Test
    void debeSerExitoso_cuandoNoSeConfiguraNingunaAudiencia() {
        // Arrange
        var validadorSinAudiencias = new AudienciaValidator(List.of());
        Jwt jwt = jwtConAudiencia(null);

        // Act
        OAuth2TokenValidatorResult resultado = validadorSinAudiencias.validate(jwt);

        // Assert
        assertThat(resultado.hasErrors()).isFalse();
    }
}
