package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.BadJwtException;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DecodificadoresPorEmisorTest {

    private static final String ISSUER = "https://idp-principal.test/realms/x";
    private static HttpServer servidor;
    private static RSAKey clave;
    private static String jwksUri;

    @BeforeAll
    static void iniciar() throws Exception {
        clave = new RSAKeyGenerator(2048).keyID("k1").generate();
        var cuerpo = new JWKSet(clave.toPublicJWK()).toString().getBytes(StandardCharsets.UTF_8);
        servidor = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        servidor.createContext("/jwks", intercambio -> {
            intercambio.getResponseHeaders().add("Content-Type", "application/json");
            intercambio.sendResponseHeaders(200, cuerpo.length);
            intercambio.getResponseBody().write(cuerpo);
            intercambio.close();
        });
        servidor.start();
        jwksUri = "http://127.0.0.1:" + servidor.getAddress().getPort() + "/jwks";
    }

    @AfterAll
    static void detener() {
        servidor.stop(0);
    }

    private static AutenticacionProperties.Emisor emisor(String id, boolean habilitado, String issuer, String audiencia) {
        return new AutenticacionProperties.Emisor(id, habilitado, issuer, jwksUri, List.of(audiencia),
                ModoAutorizacion.PEP, false, new ClaimsIdentidad("sub", "email", "u", "n", "", ""));
    }

    private static String firmado(String issuer, String audiencia) throws Exception {
        var claims = new JWTClaimsSet.Builder().issuer(issuer).subject("u-1").audience(audiencia)
                .expirationTime(new Date(System.currentTimeMillis() + 600_000)).build();
        var jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("k1").build(), claims);
        jwt.sign(new RSASSASigner(clave));
        return jwt.serialize();
    }

    private static DecodificadoresPorEmisor decodificadores() {
        return new DecodificadoresPorEmisor(new AutenticacionProperties(List.of(
                emisor("principal", true, ISSUER, "arquisoft"),
                emisor("apagado", false, "https://apagado.test", "arquisoft"))));
    }

    @Test
    void debeEnrutarPorIssYDecodificar_cuandoElEmisorEstaHabilitado() throws Exception {
        // Act
        var jwt = decodificadores().decoderMultiEmisor().decode(firmado(ISSUER, "arquisoft"));

        // Assert
        assertThat(jwt.getSubject()).isEqualTo("u-1");
    }

    @Test
    void debeRechazar_cuandoEmisorDesconocidoODeshabilitadoOTokenIlegible() throws Exception {
        // Arrange
        var decoder = decodificadores().decoderMultiEmisor();
        var desconocido = new PlainJWT(new JWTClaimsSet.Builder().issuer("https://otro.test").build()).serialize();
        var deshabilitado = new PlainJWT(new JWTClaimsSet.Builder().issuer("https://apagado.test").build()).serialize();

        // Act / Assert
        assertThatThrownBy(() -> decoder.decode(desconocido)).isInstanceOf(BadJwtException.class);
        assertThatThrownBy(() -> decoder.decode(deshabilitado)).isInstanceOf(BadJwtException.class);
        assertThatThrownBy(() -> decoder.decode("basura")).isInstanceOf(BadJwtException.class);
    }

    @Test
    void debeValidarAudienciaPorEmisor_cuandoElTokenTraeOtraAudiencia() throws Exception {
        // Arrange
        var decoder = decodificadores().decoderMultiEmisor();
        var token = firmado(ISSUER, "otra-audiencia");

        // Act / Assert
        assertThatThrownBy(() -> decoder.decode(token)).isInstanceOf(BadJwtException.class);
    }
}
