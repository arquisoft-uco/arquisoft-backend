package com.arquisoft.seguridad.infrastructure.auth.command.secondaryadapter.jwt;

import com.arquisoft.seguridad.infrastructure.auth.command.secondaryadapter.jwt.mapper.JwtIdentidadMapper;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ClaimsIdentidad;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtIdentidadMapperTest {

    private static final String ISSUER = "https://idp.test/realms/x";

    private final JwtIdentidadMapper mapper = new JwtIdentidadMapper(new AutenticacionProperties(List.of(
            new AutenticacionProperties.Emisor("principal", true, ISSUER, "", List.of(), ModoAutorizacion.PEP, false,
                    new ClaimsIdentidad("sub", "email", "preferred_username", "name", "", "")))));

    private static Jwt jwt(String issuer) {
        return Jwt.withTokenValue("t").header("alg", "RS256").issuer(issuer).subject("u-1")
                .claim("email", "a@uco.edu.co").claim("name", "Ana").claim("realm_access", Map.of("roles", List.of("X")))
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(60)).build();
    }

    @Test
    void debeMapearConClaimsDelEmisorYRolesVacios_cuandoElEmisorNoDeclaraRoles() {
        // Act
        var identidad = mapper.toModel(jwt(ISSUER));

        // Assert
        assertThat(identidad.identidadId()).isEqualTo("u-1");
        assertThat(identidad.correo()).isEqualTo("a@uco.edu.co");
        assertThat(identidad.nombre()).isEqualTo("Ana");
        assertThat(identidad.roles()).isEmpty();
    }

    @Test
    void debeRechazar_cuandoElEmisorNoEstaConfigurado() {
        // Arrange
        var token = jwt("https://otro.test");

        // Act / Assert
        assertThatThrownBy(() -> mapper.toModel(token)).isInstanceOf(BadJwtException.class);
    }
}
