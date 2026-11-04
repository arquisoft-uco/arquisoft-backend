package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.model.ModificarRepositorioArtefactoCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarRepositorioArtefactoMapperTest {

    @Test
    void debeMapearADomain_cuandoCommandValido() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();
        var command = ModificarRepositorioArtefactoCommand.crear(
                repositorio, "Repo", "Descripcion", administrador);

        // Act
        var domain = ModificarRepositorioArtefactoMapper.toDomain(command);

        // Assert
        assertThat(domain.getRepositorio()).isEqualTo(repositorio);
        assertThat(domain.getNombreRepositorio()).isEqualTo("Repo");
        assertThat(domain.getDescripcion()).isEqualTo("Descripcion");
        assertThat(domain.getAdministrador()).isEqualTo(administrador);
    }
}
