package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.model.EliminarVersionRepositorioArtefactoCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EliminarVersionRepositorioArtefactoMapperTest {

    @Test
    void debeMapearLaVersion() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();

        // Act
        var eliminacion = EliminarVersionRepositorioArtefactoMapper.toDomain(
                new EliminarVersionRepositorioArtefactoCommand(version));

        // Assert
        assertThat(eliminacion.getVersion()).isEqualTo(version);
    }
}
