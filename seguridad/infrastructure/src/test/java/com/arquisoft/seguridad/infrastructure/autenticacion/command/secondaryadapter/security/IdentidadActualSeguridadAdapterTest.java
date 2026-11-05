package com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.security;

import com.arquisoft.seguridad.infrastructure.config.autenticacion.BearerAuthenticationToken;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.SesionAuthenticationToken;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IdentidadActualSeguridadAdapterTest {

    private final IdentidadActualSeguridadAdapter adaptador = new IdentidadActualSeguridadAdapter();

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void debeArmarIdentidadActual_cuandoBearer() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var expira = Instant.now().plusSeconds(60);
        SecurityContextHolder.getContext().setAuthentication(new BearerAuthenticationToken("legado",
                id.toString(), "a@uco.edu.co", "a@uco.edu.co", List.of("ASESOR"), "jti-1", expira));

        // Act
        var identidad = adaptador.obtener();

        // Assert
        assertThat(identidad.id()).isEqualTo(id);
        assertThat(identidad.correo()).isEqualTo("a@uco.edu.co");
        assertThat(identidad.nombreUsuario()).isEqualTo("a@uco.edu.co");
        assertThat(identidad.roles()).containsExactly("ASESOR");
        assertThat(identidad.idToken()).isEqualTo("jti-1");
        assertThat(identidad.expiraEn()).isEqualTo(expira);
    }

    @Test
    void debeArmarIdentidadActual_cuandoSesion() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        SecurityContextHolder.getContext().setAuthentication(
                new SesionAuthenticationToken("sesion-pdp", id.toString(), "Ana", "ana@uco.edu.co", "t-1"));

        // Act
        var identidad = adaptador.obtener();

        // Assert
        assertThat(identidad.id()).isEqualTo(id);
        assertThat(identidad.correo()).isEqualTo("ana@uco.edu.co");
        assertThat(identidad.nombreUsuario()).isEqualTo("Ana");
        assertThat(identidad.roles()).isEmpty();
        assertThat(identidad.idToken()).isNull();
        assertThat(identidad.expiraEn()).isNull();
    }

    @Test
    void debeLanzarCredencialesNoEncontradas_cuandoNoHayIdentidadOElSujetoNoEsUuid() {
        // Arrange
        var sinIdentidad = new TestingAuthenticationToken("u", "p");

        // Act / Assert
        assertThatThrownBy(adaptador::obtener).isInstanceOf(AuthenticationCredentialsNotFoundException.class);
        SecurityContextHolder.getContext().setAuthentication(sinIdentidad);
        assertThatThrownBy(adaptador::obtener).isInstanceOf(AuthenticationCredentialsNotFoundException.class);
        SecurityContextHolder.getContext().setAuthentication(
                new SesionAuthenticationToken("sesion-pdp", "no-es-uuid", "Ana", "ana@uco.edu.co", "t-1"));
        assertThatThrownBy(adaptador::obtener).isInstanceOf(AuthenticationCredentialsNotFoundException.class);
        SecurityContextHolder.getContext().setAuthentication(new BearerAuthenticationToken("legado",
                "no-es-uuid", null, null, null, null, null));
        assertThatThrownBy(adaptador::obtener).isInstanceOf(AuthenticationCredentialsNotFoundException.class);
    }
}
