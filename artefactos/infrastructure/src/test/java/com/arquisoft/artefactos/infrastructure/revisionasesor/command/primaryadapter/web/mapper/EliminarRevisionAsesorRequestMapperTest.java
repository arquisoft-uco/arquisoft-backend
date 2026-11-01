package com.arquisoft.artefactos.infrastructure.revisionasesor.command.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EliminarRevisionAsesorRequestMapperTest {

    @Test
    void debeConstruirElCommand_conRevisionYAsesor() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var command = EliminarRevisionAsesorRequestMapper.toCommand(revision, asesor);

        // Assert
        assertThat(command.revisionAsesor()).isEqualTo(revision);
        assertThat(command.asesor()).isEqualTo(asesor);
    }
}
