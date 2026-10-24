package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.primaryadapter.web.dto.GenerarRepositorioArtefactoRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GenerarRepositorioArtefactoRequestMapperTest {

    @Test
    void debeConstruirElCommand_cuandoDtoEIdentidadValidos() {
        // Arrange
        var administrador = UtilUUID.generarNuevoUUID();
        var dto = new GenerarRepositorioArtefactoRequestDTO("Repo", "Descripcion");

        // Act
        var command = GenerarRepositorioArtefactoRequestMapper.toCommand(dto, administrador);

        // Assert
        assertThat(command.nombreRepositorio()).isEqualTo("Repo");
        assertThat(command.descripcion()).isEqualTo("Descripcion");
        assertThat(command.administrador()).isEqualTo(administrador);
    }
}
