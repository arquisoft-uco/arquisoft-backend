package com.arquisoft.artefactos.infrastructure.versionartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EliminarVersionArtefactoRequestMapperTest {

    @Test
    void debeConstruirElCommand() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act
        var command = EliminarVersionArtefactoRequestMapper.toCommand(version, solicitante);

        // Assert
        assertThat(command.versionArtefacto()).isEqualTo(version);
        assertThat(command.solicitante()).isEqualTo(solicitante);
    }
}
