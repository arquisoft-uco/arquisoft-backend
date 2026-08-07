package com.arquisoft.seguridad.infrastructure.auth.command.adapter.out.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioActualOutputAdapterTest {

    @InjectMocks
    private UsuarioActualOutputAdapter adapter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Jwt construirJwt(String subject, String email, String nombrePreferido) {
        return Jwt.withTokenValue("token-prueba")
                .header("alg", "RS256")
                .subject(subject)
                .claim("email", email)
                .claim("preferred_username", nombrePreferido)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    private void configurarContextoSeguridad(Jwt jwt, List<GrantedAuthority> authorities) {
        SecurityContext context = mock(SecurityContext.class);
        JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt, authorities);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    void debeObtenerIdUsuario_cuandoJwtEnSecurityContext() {
        // Arrange
        Jwt jwt = construirJwt("uuid-estudiante-123", "estudiante@uco.edu.co", "estudiante");
        configurarContextoSeguridad(jwt, List.of());

        // Act
        String userId = adapter.obtenerIdUsuario();

        // Assert
        assertThat(userId).isEqualTo("uuid-estudiante-123");
    }

    @Test
    void debeRetornarNull_cuandoNoHayAutenticacion() {
        // Arrange
        SecurityContextHolder.clearContext();

        // Act
        String userId = adapter.obtenerIdUsuario();

        // Assert
        assertThat(userId).isNull();
    }

    @Test
    void debeObtenerCorreo_cuandoJwtContieneEmail() {
        // Arrange
        Jwt jwt = construirJwt("uuid-asesor-456", "asesor@uco.edu.co", "asesor");
        configurarContextoSeguridad(jwt, List.of());

        // Act
        String correo = adapter.obtenerCorreo();

        // Assert
        assertThat(correo).isEqualTo("asesor@uco.edu.co");
    }

    @Test
    void debeObtenerNombreUsuarioDesdeNombrePreferido_cuandoExiste() {
        // Arrange
        Jwt jwt = construirJwt("uuid-coordinador", "coordinador@uco.edu.co", "coord.sistemas");
        configurarContextoSeguridad(jwt, List.of());

        // Act
        String nombreUsuario = adapter.obtenerNombreUsuario();

        // Assert
        assertThat(nombreUsuario).isEqualTo("coord.sistemas");
    }

    @Test
    void debeObtenerNombreUsuarioDesdeEmail_cuandoNombrePreferidoNoExiste() {
        // Arrange
        Jwt jwt = Jwt.withTokenValue("token-prueba")
                .header("alg", "RS256")
                .subject("uuid-jurado")
                .claim("email", "jurado@uco.edu.co")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        configurarContextoSeguridad(jwt, List.of());

        // Act
        String nombreUsuario = adapter.obtenerNombreUsuario();

        // Assert
        assertThat(nombreUsuario).isEqualTo("jurado@uco.edu.co");
    }

    @Test
    void debeRetornarTrue_cuandoTieneRol() {
        // Arrange
        Jwt jwt = construirJwt("uuid-estudiante", "estudiante@uco.edu.co", "estudiante");
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("estudiante"),
                new SimpleGrantedAuthority("coordinador")
        );
        configurarContextoSeguridad(jwt, authorities);

        // Act
        boolean tieneRolEstudiante = adapter.tieneRol("estudiante");
        boolean tieneRolCoordinador = adapter.tieneRol("coordinador");
        boolean tieneRolAsesor = adapter.tieneRol("asesor-ficha");

        // Assert
        assertThat(tieneRolEstudiante).isTrue();
        assertThat(tieneRolCoordinador).isTrue();
        assertThat(tieneRolAsesor).isFalse();
    }

    @Test
    void debeRetornarFalso_cuandoNoHayAutenticacionParaTieneRol() {
        // Arrange
        SecurityContextHolder.clearContext();

        // Act
        boolean tieneRol = adapter.tieneRol("estudiante");

        // Assert
        assertThat(tieneRol).isFalse();
    }

    @Test
    void debeRetornarListaRoles_cuandoAutenticado() {
        // Arrange
        Jwt jwt = construirJwt("uuid-admin", "admin@uco.edu.co", "admin");
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("estudiante"),
                new SimpleGrantedAuthority("coordinador"),
                new SimpleGrantedAuthority("jurado")
        );
        configurarContextoSeguridad(jwt, authorities);

        // Act
        List<String> roles = adapter.obtenerRoles();

        // Assert
        assertThat(roles).hasSize(3)
                .containsExactlyInAnyOrder("estudiante", "coordinador", "jurado");
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayAutenticacionParaObtenerRoles() {
        // Arrange
        SecurityContextHolder.clearContext();

        // Act
        List<String> roles = adapter.obtenerRoles();

        // Assert
        assertThat(roles).isEmpty();
    }

    @Test
    void debeRetornarNull_cuandoPrincipalNoEsJwt() {
        // Arrange
        SecurityContext context = mock(SecurityContext.class);
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn("not-a-jwt");
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        // Act
        String userId = adapter.obtenerIdUsuario();
        String correo = adapter.obtenerCorreo();

        // Assert
        assertThat(userId).isNull();
        assertThat(correo).isNull();
    }
}
