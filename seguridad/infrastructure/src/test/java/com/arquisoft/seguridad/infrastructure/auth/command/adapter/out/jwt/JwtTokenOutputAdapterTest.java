package com.arquisoft.seguridad.infrastructure.auth.command.adapter.out.jwt;

import com.arquisoft.seguridad.domain.auth.model.IdentidadToken;
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
class JwtTokenOutputAdapterTest {

    @Mock
    private JwtDecoder jwtDecoder;

    @InjectMocks
    private JwtTokenOutputAdapter jwtTokenAdapter;

    private Jwt buildJwt(Map<String, Object> claims) {
        return Jwt.withTokenValue("token-de-prueba")
                .header("alg", "RS256")
                .claims(c -> c.putAll(claims))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .subject("uuid-usuario")
                .build();
    }

    @Test
    void debeExtraerRolEstudiante_cuandoRealmAccessContieneESTUDIANTE() {
        Jwt jwt = buildJwt(Map.of(
                "email", "estudiante@uco.edu.co",
                "name", "Estudiante UCO",
                "realm_access", Map.of("roles", List.of("estudiante"))
        ));
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);

        IdentidadToken info = jwtTokenAdapter.extraerInfo("token-de-prueba");

        assertThat(info).isNotNull();
        assertThat(info.roles()).containsExactly("estudiante");
    }

    @Test
    void debeExtraerRolAsesorFicha_cuandoRealmAccessContieneASESOR_FICHA() {
        Jwt jwt = buildJwt(Map.of(
                "email", "asesor@uco.edu.co",
                "name", "Asesor UCO",
                "realm_access", Map.of("roles", List.of("asesor-ficha"))
        ));
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);

        IdentidadToken info = jwtTokenAdapter.extraerInfo("token-de-prueba");

        assertThat(info.roles()).containsExactly("asesor-ficha");
    }

    @Test
    void debeExtraerMultiplesRoles_cuandoUsuarioTieneVariosRoles() {
        Jwt jwt = buildJwt(Map.of(
                "email", "coordinador@uco.edu.co",
                "name", "Coordinador UCO",
                "realm_access", Map.of("roles", List.of("estudiante", "coordinador"))
        ));
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);

        IdentidadToken info = jwtTokenAdapter.extraerInfo("token-de-prueba");

        assertThat(info.roles()).hasSize(2).containsExactlyInAnyOrder("estudiante", "coordinador");
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayRealmAccess() {
        Jwt jwt = buildJwt(Map.of(
                "email", "usuario@uco.edu.co",
                "name", "Usuario sin realm_access"
        ));
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);

        IdentidadToken info = jwtTokenAdapter.extraerInfo("token-de-prueba");

        assertThat(info.roles()).isEmpty();
    }

    @Test
    void debeIgnorarResourceAccess_cuandoExisteJuntoARealmAccess() {
        Jwt jwt = buildJwt(Map.of(
                "email", "jurado@uco.edu.co",
                "name", "Jurado UCO",
                "realm_access", Map.of("roles", List.of("jurado")),
                "resource_access", Map.of(
                        "arquisoft-backend", Map.of("roles", List.of("manage-account"))
                )
        ));
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);

        IdentidadToken info = jwtTokenAdapter.extraerInfo("token-de-prueba");

        assertThat(info.roles()).containsExactly("jurado");
        assertThat(info.roles()).doesNotContain("manage-account");
    }

    @Test
    void debeExtraerIdentidadId_cuandoTokenValido() {
        Jwt jwt = buildJwt(Map.of(
                "email", "usuario@uco.edu.co",
                "name", "Usuario UCO",
                "realm_access", Map.of("roles", List.of("estudiante"))
        ));
        when(jwtDecoder.decode(anyString())).thenReturn(jwt);

        IdentidadToken info = jwtTokenAdapter.extraerInfo("token-de-prueba");

        assertThat(info.identidadId()).isEqualTo("uuid-usuario");
        assertThat(info.correo()).isEqualTo("usuario@uco.edu.co");
    }

    @Test
    void debeRetornarFalso_cuandoTokenEsMalformado() {
        when(jwtDecoder.decode(anyString())).thenThrow(new JwtException("Token malformado"));

        boolean resultado = jwtTokenAdapter.validarToken("token-basura");

        assertThat(resultado).isFalse();
    }
}
