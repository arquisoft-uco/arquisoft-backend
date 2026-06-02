package com.arquisoft.seguridad.infrastructure.adapter.out;

import com.arquisoft.seguridad.infrastructure.adapter.out.security.JwtTokenOutputAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenAdapterTest {

    @Mock
    private JwtDecoder jwtDecoder;

    @InjectMocks
    private JwtTokenOutputAdapter jwtTokenAdapter;

    // ── utilidad: construye un Jwt real usando el builder de Spring Security ──

    private Jwt buildJwt(Map<String, Object> claims) {
        return Jwt.withTokenValue("token-de-prueba")
                .header("alg", "RS256")
                .claims(c -> c.putAll(claims))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .subject("uuid-usuario")
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tests de extractUserInfo / extractRealmRoles
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void debeExtraerRolEstudiante_cuandoRealmAccessContieneESTUDIANTE() {
        // Arrange
        Jwt jwt = buildJwt(Map.of(
                "email", "estudiante@uco.edu.co",
                "name", "Estudiante UCO",
                "realm_access", Map.of("roles", List.of("estudiante"))
        ));
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);

        // Act
        Map<String, Object> userInfo = jwtTokenAdapter.extractUserInfo("token-de-prueba");

        // Assert
        assertThat(userInfo).isNotNull();
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) userInfo.get("roles");
        assertThat(roles).containsExactly("estudiante");
    }

    @Test
    void debeExtraerRolAsesorFicha_cuandoRealmAccessContieneASESOR_FICHA() {
        // Arrange
        Jwt jwt = buildJwt(Map.of(
                "email", "asesor@uco.edu.co",
                "name", "Asesor UCO",
                "realm_access", Map.of("roles", List.of("asesor-ficha"))
        ));
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);

        // Act
        Map<String, Object> userInfo = jwtTokenAdapter.extractUserInfo("token-de-prueba");

        // Assert
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) userInfo.get("roles");
        assertThat(roles).containsExactly("asesor-ficha");
    }

    @Test
    void debeExtraerMultiplesRoles_cuandoUsuarioTieneVariosRoles() {
        // Arrange
        Jwt jwt = buildJwt(Map.of(
                "email", "coordinador@uco.edu.co",
                "name", "Coordinador UCO",
                "realm_access", Map.of("roles", List.of("estudiante", "coordinador"))
        ));
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);

        // Act
        Map<String, Object> userInfo = jwtTokenAdapter.extractUserInfo("token-de-prueba");

        // Assert
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) userInfo.get("roles");
        assertThat(roles).hasSize(2).containsExactlyInAnyOrder("estudiante", "coordinador");
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayRealmAccess() {
        // Arrange
        Jwt jwt = buildJwt(Map.of(
                "email", "usuario@uco.edu.co",
                "name", "Usuario sin realm_access"
        ));
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);

        // Act
        Map<String, Object> userInfo = jwtTokenAdapter.extractUserInfo("token-de-prueba");

        // Assert
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) userInfo.get("roles");
        assertThat(roles).isEmpty();
    }

    @Test
    void debeIgnorarResourceAccess_cuandoExisteJuntoARealmAccess() {
        // Arrange — resource_access presente no debe contaminar la lista de roles
        Jwt jwt = buildJwt(Map.of(
                "email", "jurado@uco.edu.co",
                "name", "Jurado UCO",
                "realm_access", Map.of("roles", List.of("jurado")),
                "resource_access", Map.of(
                        "arquisoft-backend", Map.of("roles", List.of("manage-account"))
                )
        ));
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);

        // Act
        Map<String, Object> userInfo = jwtTokenAdapter.extractUserInfo("token-de-prueba");

        // Assert — solo el rol de realm_access, sin duplicados de resource_access
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) userInfo.get("roles");
        assertThat(roles).containsExactly("jurado");
        assertThat(roles).doesNotContain("manage-account");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Tests de validateToken
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void debeRetornarFalso_cuandoTokenEsMalformado() {
        // Arrange
        when(jwtDecoder.decode(anyString())).thenThrow(new JwtException("Token malformado"));

        // Act
        boolean resultado = jwtTokenAdapter.validateToken("token-basura");

        // Assert
        assertThat(resultado).isFalse();
    }
}
