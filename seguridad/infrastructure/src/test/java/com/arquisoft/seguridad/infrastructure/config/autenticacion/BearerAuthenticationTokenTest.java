package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BearerAuthenticationTokenTest {

    @Test
    void debeExponerElContrato_cuandoSeConstruye() {
        // Arrange
        var expira = Instant.now().plusSeconds(60);

        // Act
        var token = new BearerAuthenticationToken("principal", "sub-1", "a@uco.edu.co", "ana",
                List.of("R1"), "jti-1", expira);

        // Assert
        assertThat(token.proveedor()).isEqualTo("bearer");
        assertThat(token.emisor()).isEqualTo("principal");
        assertThat(token.sujeto()).isEqualTo("sub-1");
        assertThat(token.correo()).isEqualTo("a@uco.edu.co");
        assertThat(token.nombreUsuario()).isEqualTo("ana");
        assertThat(token.roles()).containsExactly("R1");
        assertThat(token.idToken()).isEqualTo("jti-1");
        assertThat(token.expiraEn()).isEqualTo(expira);
        assertThat(token.getName()).isEqualTo("sub-1");
        assertThat(token.getPrincipal()).isEqualTo("sub-1");
        assertThat(token.getCredentials()).isNull();
    }

    @Test
    void debeEstarAutenticado_cuandoSeConstruye() {
        // Arrange / Act
        var token = new BearerAuthenticationToken("principal", "sub-1", null, null, null, null, null);

        // Assert
        assertThat(token.isAuthenticated()).isTrue();
        assertThat(token.roles()).isEmpty();
    }
}
