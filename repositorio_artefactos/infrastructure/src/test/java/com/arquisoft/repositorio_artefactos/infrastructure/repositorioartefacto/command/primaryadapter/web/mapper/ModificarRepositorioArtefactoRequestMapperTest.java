package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.primaryadapter.web.dto.ModificarRepositorioArtefactoRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarRepositorioArtefactoRequestMapperTest {

    @Test
    void debeConstruirCommand_cuandoDtoYPathValidos() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();
        var dto = new ModificarRepositorioArtefactoRequestDTO("Repo", "Descripcion");

        // Act
        var command = ModificarRepositorioArtefactoRequestMapper.toCommand(dto, repositorio, administrador);

        // Assert
        assertThat(command.repositorio()).isEqualTo(repositorio);
        assertThat(command.nombreRepositorio()).isEqualTo("Repo");
        assertThat(command.descripcion()).isEqualTo("Descripcion");
        assertThat(command.administrador()).isEqualTo(administrador);
    }
}
