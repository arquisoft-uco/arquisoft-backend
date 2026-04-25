package com.arquisoft.seguridad.domain.service;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class RoleAuthorityMapperTest {

    @Test
    void debeRetornarAuthority_cuandoCodigoEsValido() {
        // Arrange
        String codigo = "ESTUDIANTE";

        // Act
        Optional<String> resultado = RoleAuthorityMapper.toAuthorityName(codigo);

        // Assert
        assertThat(resultado).isPresent().hasValue("ROLE_ESTUDIANTE");
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
        List<String> codigos = List.of("ESTUDIANTE", "COORDINADOR", "ADMINISTRADOR");

        // Act
        List<String> authorities = RoleAuthorityMapper.toAuthorityNames(codigos);

        // Assert
        assertThat(authorities)
                .hasSize(3)
                .containsExactly("ROLE_ESTUDIANTE", "ROLE_COORDINADOR", "ROLE_ADMINISTRADOR");
    }

    @Test
    void debeFiltrarRolesInternos_cuandoListaMixta() {
        // Arrange
        List<String> codigos = List.of("ESTUDIANTE", "offline_access", "uma_authorization", "JURADO");

        // Act
        List<String> authorities = RoleAuthorityMapper.toAuthorityNames(codigos);

        // Assert
        assertThat(authorities)
                .hasSize(2)
                .containsExactly("ROLE_ESTUDIANTE", "ROLE_JURADO");
    }
}
