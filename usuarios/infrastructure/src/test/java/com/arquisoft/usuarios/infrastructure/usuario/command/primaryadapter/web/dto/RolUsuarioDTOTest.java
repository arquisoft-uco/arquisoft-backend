package com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web.dto;

import com.arquisoft.usuarios.domain.usuario.model.UsuarioRole;
import com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web.dto.CrearUsuarioRequestDTO.RolUsuarioDTO;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class RolUsuarioDTOTest {

    @Test
    void debeResolverAlRolDelMismoNombre_paraTodoCodigoDelDTO() {
        // Arrange
        RolUsuarioDTO[] rolesDelContrato = RolUsuarioDTO.values();

        // Act & Assert
        assertThat(rolesDelContrato).allSatisfy(rol ->
                assertThat(UsuarioRole.desdeCodigo(rol.getCodigo()).name()).isEqualTo(rol.name()));
    }

    @Test
    void debeExponerUnRolPorCadaConstanteDelDominio() {
        // Arrange
        var nombresDelDominio = Arrays.stream(UsuarioRole.values()).map(Enum::name).toList();

        // Act
        var nombresDelContrato = Arrays.stream(RolUsuarioDTO.values()).map(Enum::name).toList();

        // Assert
        assertThat(nombresDelContrato).containsExactlyInAnyOrderElementsOf(nombresDelDominio);
    }
}
