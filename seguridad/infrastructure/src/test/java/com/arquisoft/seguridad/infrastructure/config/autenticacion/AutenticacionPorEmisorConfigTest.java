package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AutenticacionPorEmisorConfigTest {

    private static final String ISSUER = "https://idp.test/realms/x";

    private final AutenticacionProperties propiedades = new AutenticacionProperties(List.of(
            new AutenticacionProperties.Emisor("principal", true, ISSUER, "", List.of(), ModoAutorizacion.PEP, false,
                    new ClaimsIdentidad("uid", "email", "u", "n", "roles"))));

    private static String token(String issuer) {
        return new PlainJWT(new JWTClaimsSet.Builder().issuer(issuer).build()).serialize();
    }

    @Test
    void debeConstruirLaIdentidadConLosClaimsDelEmisor_cuandoElEmisorEstaConfigurado() {
        // Arrange
        var decodificadores = mock(DecodificadoresPorEmisor.class);
        var decoder = mock(JwtDecoder.class);
        var jwt = Jwt.withTokenValue("t").header("alg", "RS256").issuer(ISSUER).jti("jti-1")
                .claim("uid", "usuario-7").claim("email", "a@uco.edu.co").claim("roles", List.of("R1", "R2"))
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(60)).build();
        when(decodificadores.decoderDe(propiedades.issuers().get(0))).thenReturn(decoder);
        when(decoder.decode(anyString())).thenReturn(jwt);
        var manager = new AutenticacionPorEmisorConfig().seguridadAutenticacionBearerManager(decodificadores, propiedades);

        // Act
        var autenticacion = manager.authenticate(new BearerTokenAuthenticationToken(token(ISSUER)));

        // Assert
        assertThat(autenticacion).isInstanceOfSatisfying(BearerAuthenticationToken.class, identidad -> {
            assertThat(identidad.proveedor()).isEqualTo("bearer");
            assertThat(identidad.emisor()).isEqualTo("principal");
            assertThat(identidad.sujeto()).isEqualTo("usuario-7");
            assertThat(identidad.correo()).isEqualTo("a@uco.edu.co");
            assertThat(identidad.nombreUsuario()).isEqualTo("a@uco.edu.co");
            assertThat(identidad.roles()).containsExactly("R1", "R2");
            assertThat(identidad.idToken()).isEqualTo("jti-1");
            assertThat(identidad.expiraEn()).isEqualTo(jwt.getExpiresAt());
            assertThat(identidad.getAuthorities()).isEmpty();
        });
    }

    @Test
    void debeRechazar_cuandoElEmisorNoEstaConfigurado() {
        // Arrange
        var manager = new AutenticacionPorEmisorConfig()
                .seguridadAutenticacionBearerManager(mock(DecodificadoresPorEmisor.class), propiedades);
        var intento = new BearerTokenAuthenticationToken(token("https://otro.test"));

        // Act / Assert
        assertThatThrownBy(() -> manager.authenticate(intento)).isInstanceOf(InvalidBearerTokenException.class);
    }

    @Test
    void debeRechazar_cuandoElTokenEsIlegible() {
        // Arrange
        var manager = new AutenticacionPorEmisorConfig()
                .seguridadAutenticacionBearerManager(mock(DecodificadoresPorEmisor.class), propiedades);
        var intento = new BearerTokenAuthenticationToken("no-es-un-jwt");

        // Act / Assert
        assertThatThrownBy(() -> manager.authenticate(intento)).isInstanceOf(InvalidBearerTokenException.class);
    }
}
