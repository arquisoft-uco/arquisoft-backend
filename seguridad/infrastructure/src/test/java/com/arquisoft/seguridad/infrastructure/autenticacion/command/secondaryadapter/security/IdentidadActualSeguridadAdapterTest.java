package com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.security;

import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ClaimsIdentidad;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IdentidadActualSeguridadAdapterTest {

    private static final String ISSUER = "https://idp.test/realms/x";

    private final IdentidadActualSeguridadAdapter adaptador = new IdentidadActualSeguridadAdapter(
            new AutenticacionProperties(List.of(new AutenticacionProperties.Emisor("legado", true, ISSUER, "",
                    List.of(), ModoAutorizacion.ROLE_TABLE, true,
                    new ClaimsIdentidad("sub", "email", "preferred_username", "name", "realm_access.roles", "")))));

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    private void autenticar(String issuer, String sub, Map<String, Object> extra) {
        var jwt = Jwt.withTokenValue("t").header("alg", "RS256").issuer(issuer).subject(sub).jti("jti-1")
                .claims(c -> c.putAll(extra)).issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(60)).build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt, List.of(), sub));
    }

    @Test
    void debeConstruirIdentidad_conClaimsDelEmisor() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        autenticar(ISSUER, id.toString(), Map.of("email", "a@uco.edu.co",
                "realm_access", Map.of("roles", List.of("ASESOR"))));

        // Act
        var identidad = adaptador.obtener();

        // Assert
        assertThat(identidad.id()).isEqualTo(id);
        assertThat(identidad.correo()).isEqualTo("a@uco.edu.co");
        assertThat(identidad.nombreUsuario()).isEqualTo("a@uco.edu.co");
        assertThat(identidad.roles()).containsExactly("ASESOR");
        assertThat(identidad.idToken()).isEqualTo("jti-1");
        assertThat(identidad.expiraEn()).isNotNull();
    }

    @Test
    void debeFallar_cuandoSubNoEsUuidOEmisorDesconocido() {
        // Arrange
        autenticar(ISSUER, "no-es-uuid", Map.of());

        // Act / Assert
        assertThatThrownBy(adaptador::obtener).isInstanceOf(AuthenticationCredentialsNotFoundException.class);
        autenticar("https://otro", UtilUUID.generarNuevoUUID().toString(), Map.of());
        assertThatThrownBy(adaptador::obtener).isInstanceOf(AuthenticationCredentialsNotFoundException.class);
    }

    @Test
    void debeFallar_cuandoNoHayAutenticacion() {
        // Act / Assert
        assertThatThrownBy(adaptador::obtener).isInstanceOf(AuthenticationCredentialsNotFoundException.class);
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("u", "p"));
        assertThatThrownBy(adaptador::obtener).isInstanceOf(AuthenticationCredentialsNotFoundException.class);
    }
}
