package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.primaryadapter.web.dto.ModificarRepositorioArtefactoRequestDTO;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarRepositorioArtefactoRequestMapperTest {

    @Test
    void debeConstruirCommand_cuandoDtoYPathValidos() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();
        var dto = new ModificarRepositorioArtefactoRequestDTO("Repo", "Descripcion");

        // Act
        var command = ModificarRepositorioArtefactoRequestMapper.toCommand(dto, repositorio, administrador.toString());

        // Assert
        assertThat(command.repositorio()).isEqualTo(repositorio);
        assertThat(command.nombreRepositorio()).isEqualTo("Repo");
        assertThat(command.descripcion()).isEqualTo("Descripcion");
        assertThat(command.administrador()).isEqualTo(administrador);
    }

    @Test
    void debeLanzarApplicationValidation_cuandoElSubjectNoEsUuid() {
        // Arrange
        var dto = new ModificarRepositorioArtefactoRequestDTO("Repo", "Descripcion");
        var repositorio = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> ModificarRepositorioArtefactoRequestMapper.toCommand(dto, repositorio, "no-uuid"))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
