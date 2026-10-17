package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.primaryadapter.web.dto.GenerarRepositorioArtefactoRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenerarRepositorioArtefactoRequestMapperTest {

    @Test
    void debeConstruirElCommand_cuandoElSubjectEsUuid() {
        // Arrange
        var administrador = UtilUUID.generarNuevoUUID();
        var dto = new GenerarRepositorioArtefactoRequestDTO("Repo", "Descripcion");

        // Act
        var command = GenerarRepositorioArtefactoRequestMapper.toCommand(dto, administrador.toString());

        // Assert
        assertThat(command.nombreRepositorio()).isEqualTo("Repo");
        assertThat(command.descripcion()).isEqualTo("Descripcion");
        assertThat(command.administrador()).isEqualTo(administrador);
    }

    @Test
    void debeLanzarApplicationValidation_cuandoElSubjectNoEsUuid() {
        // Arrange
        var dto = new GenerarRepositorioArtefactoRequestDTO("Repo", "Descripcion");

        // Act & Assert
        assertThatThrownBy(() -> GenerarRepositorioArtefactoRequestMapper.toCommand(dto, "no-uuid"))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
