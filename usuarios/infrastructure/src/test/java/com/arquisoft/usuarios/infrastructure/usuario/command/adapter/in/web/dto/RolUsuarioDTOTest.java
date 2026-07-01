package com.arquisoft.usuarios.infrastructure.usuario.command.adapter.in.web.dto;

import com.arquisoft.shared.model.UsuarioRole;
import com.arquisoft.usuarios.infrastructure.usuario.command.adapter.in.web.dto.CrearUsuarioRequestDTO.RolUsuarioDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RolUsuarioDTOTest {

    @Test
    void debeMapeareARol_cuandoToDomainEsInvocado() {
        // Arrange / Act / Assert
        assertThat(RolUsuarioDTO.ESTUDIANTE.toDomain()).isEqualTo(UsuarioRole.ESTUDIANTE);
        assertThat(RolUsuarioDTO.ASESOR.toDomain()).isEqualTo(UsuarioRole.ASESOR);
        assertThat(RolUsuarioDTO.ASESOR_FICHA.toDomain()).isEqualTo(UsuarioRole.ASESOR_FICHA);
        assertThat(RolUsuarioDTO.COORDINADOR.toDomain()).isEqualTo(UsuarioRole.COORDINADOR);
        assertThat(RolUsuarioDTO.JURADO.toDomain()).isEqualTo(UsuarioRole.JURADO);
        assertThat(RolUsuarioDTO.BIBLIOTECARIO.toDomain()).isEqualTo(UsuarioRole.BIBLIOTECARIO);
        assertThat(RolUsuarioDTO.REPRESENTANTE_COMITE_CURRICULUM.toDomain())
                .isEqualTo(UsuarioRole.REPRESENTANTE_COMITE_CURRICULUM);
        assertThat(RolUsuarioDTO.ADMINISTRADOR.toDomain()).isEqualTo(UsuarioRole.ADMINISTRADOR);
    }
}
