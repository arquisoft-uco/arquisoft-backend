package com.arquisoft.seguridad.infrastructure.config.keycloak;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RoleAuthorityMapperTest {

    @Test
    void debeRetornarAuthority_cuandoCodigoEsValido() {
        // Arrange
        String codigo = "estudiante";

        // Act
        Optional<String> resultado = RoleAuthorityMapper.toAuthorityName(codigo);

        // Assert
        assertThat(resultado).isPresent().hasValue("estudiante");
    }

    @Test
    void debeRetornarVacio_cuandoCodigoEsRolInternoKeycloak() {
        // Arrange
        String rolInterno = "offline_access";

        // Act
        Optional<String> resultado = RoleAuthorityMapper.toAuthorityName(rolInterno);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeMapearTodosLosRoles_cuandoListaContieneRolesValidos() {
        // Arrange
        List<String> codigos = List.of("estudiante", "coordinador", "administrador");

        // Act
        List<String> authorities = RoleAuthorityMapper.toAuthorityNames(codigos);

        // Assert
        assertThat(authorities)
                .hasSize(3)
                .containsExactly("estudiante", "coordinador", "administrador");
    }

    @Test
    void debeFiltrarRolesInternos_cuandoListaMixta() {
        // Arrange
        List<String> codigos = List.of("estudiante", "offline_access", "uma_authorization", "jurado");

        // Act
        List<String> authorities = RoleAuthorityMapper.toAuthorityNames(codigos);

        // Assert
        assertThat(authorities)
                .hasSize(2)
                .containsExactly("estudiante", "jurado");
    }
}
