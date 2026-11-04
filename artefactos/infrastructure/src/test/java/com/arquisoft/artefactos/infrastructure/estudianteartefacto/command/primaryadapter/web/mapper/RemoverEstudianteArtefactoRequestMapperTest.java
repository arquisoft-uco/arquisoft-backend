package com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RemoverEstudianteArtefactoRequestMapperTest {

    @Test
    void debeArmarCommand_conLosTresIdsEnSuCampo() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var command = RemoverEstudianteArtefactoRequestMapper.toCommand(artefacto, estudiante, asesor);

        // Assert
        assertThat(command.artefacto()).isEqualTo(artefacto);
        assertThat(command.estudiante()).isEqualTo(estudiante);
        assertThat(command.asesor()).isEqualTo(asesor);
    }
}
