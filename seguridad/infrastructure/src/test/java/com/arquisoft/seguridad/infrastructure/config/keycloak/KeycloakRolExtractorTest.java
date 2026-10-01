package com.arquisoft.seguridad.infrastructure.config.keycloak;

import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.assertj.core.api.Assertions.assertThat;

class KeycloakRolExtractorTest {

    private static final String CLIENT_ID = "arquisoft-api";

    private KeycloakRolExtractor rolExtractor;
    private JwtAuthenticationConverter converter;

    @BeforeEach
    void setUp() {
        rolExtractor = new KeycloakRolExtractor();
        // El campo clientId se inyecta normalmente via @Value("${KEYCLOAK_CLIENT_ID}").
        ReflectionTestUtils.setField(rolExtractor, "clientId", CLIENT_ID);
        converter = new KeycloakJwtConverterConfig(mock(AppLogger.class), rolExtractor)
                .jwtAuthenticationConverter();
    }

    private Jwt jwtConRolesDeCliente(List<String> roles) {
        return Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("uuid-estudiante")
                .issuedAt(Instant.now().minusSeconds(60))
                .expiresAt(Instant.now().plusSeconds(3600))
                .claim("resource_access", Map.of(
                        CLIENT_ID, Map.of("roles", roles)))
                .build();
    }

    @Test
    void debeExtraerRolesDelResourceAccessDelClienteEsperado() {
        // Arrange
        Jwt jwt = jwtConRolesDeCliente(List.of("fichas:ficha-perfil:view", "usuarios:usuario:create"));

        // Act
        List<String> roles = rolExtractor.extraerRolesRecurso(jwt);

        // Assert
        assertThat(roles).containsExactlyInAnyOrder(
                "fichas:ficha-perfil:view", "usuarios:usuario:create");
    }

    @Test
    void debeMapearRolesAAuthoritiesSinPrefijoRole() {
        // Arrange
        Jwt jwt = jwtConRolesDeCliente(List.of("fichas:ficha-perfil:view"));

        // Act
        AbstractAuthenticationToken authentication = converter.convert(jwt);

        // Assert — el permiso fino se mapea a authority sin prefijo ROLE_.
        // (Spring Security 7 añade además factores como FACTOR_BEARER; no son roles de negocio.)
        assertThat(authentication).isNotNull();
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        assertThat(authorities).contains("fichas:ficha-perfil:view");
        assertThat(authorities).noneMatch(authority -> authority.startsWith("ROLE_"));
    }

    @Test
    void debeRetornarSinAuthorities_cuandoNoHayResourceAccessDelCliente() {
        // Arrange — token sin el claim resource_access esperado
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("uuid-estudiante")
                .issuedAt(Instant.now().minusSeconds(60))
                .expiresAt(Instant.now().plusSeconds(3600))
                .claim("resource_access", Map.of("otro-client",
                        Map.of("roles", List.of("rol-de-otro-client"))))
                .build();

        // Act
        List<String> rolesExtraidos = rolExtractor.extraerRolesRecurso(jwt);
        AbstractAuthenticationToken authentication = converter.convert(jwt);

        // Assert — no se extrae ningún permiso del cliente esperado (los roles de otro
        // client no cuentan); por tanto ningún @PreAuthorize("hasAuthority(...)") pasaría.
        assertThat(rolesExtraidos).isEmpty();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .doesNotContain("rol-de-otro-client");
    }
}
