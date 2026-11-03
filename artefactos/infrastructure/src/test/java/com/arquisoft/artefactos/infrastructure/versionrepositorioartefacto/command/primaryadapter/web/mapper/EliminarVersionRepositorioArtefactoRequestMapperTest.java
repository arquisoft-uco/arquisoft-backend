package com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EliminarVersionRepositorioArtefactoRequestMapperTest {

    @Test
    void debeConstruirElCommand() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();

        // Act
        var command = EliminarVersionRepositorioArtefactoRequestMapper.toCommand(version);

        // Assert
        assertThat(command.version()).isEqualTo(version);
    }
}
