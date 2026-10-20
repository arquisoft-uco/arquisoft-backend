package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

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
                    new ClaimsIdentidad("uid", "email", "u", "n", "", ""))));

    private static String token(String issuer) {
        return new PlainJWT(new JWTClaimsSet.Builder().issuer(issuer).build()).serialize();
    }

    @Test
    void debeAutenticarSinAuthoritiesYConNombreDelClaimDelEmisor_cuandoElEmisorEstaConfigurado() {
        // Arrange
        var decodificadores = mock(DecodificadoresPorEmisor.class);
        var decoder = mock(JwtDecoder.class);
        var jwt = Jwt.withTokenValue("t").header("alg", "RS256").issuer(ISSUER).claim("uid", "usuario-7")
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(60)).build();
        when(decodificadores.decoderDe(propiedades.issuers().get(0))).thenReturn(decoder);
        when(decoder.decode(anyString())).thenReturn(jwt);
        var resolver = new AutenticacionPorEmisorConfig().seguridadAutenticacionPorEmisorResolver(decodificadores, propiedades);
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token(ISSUER));

        // Act
        var autenticacion = resolver.resolve(request)
                .authenticate(new BearerTokenAuthenticationToken(token(ISSUER)));

        // Assert
        assertThat(autenticacion).isInstanceOf(JwtAuthenticationToken.class);
        assertThat(autenticacion.getName()).isEqualTo("usuario-7");
        assertThat(autenticacion.getAuthorities()).isEmpty();
    }

    @Test
    void debeRechazar_cuandoElEmisorNoEstaConfigurado() {
        // Arrange
        var resolver = new AutenticacionPorEmisorConfig()
                .seguridadAutenticacionPorEmisorResolver(mock(DecodificadoresPorEmisor.class), propiedades);
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token("https://otro.test"));

        // Act / Assert
        var manager = resolver.resolve(request);
        var intento = new BearerTokenAuthenticationToken(token("https://otro.test"));
        assertThatThrownBy(() -> manager.authenticate(intento)).isInstanceOf(InvalidBearerTokenException.class);
    }
}
