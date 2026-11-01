package com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.application.estudianteartefacto.command.primaryport.model.RemoverEstudianteArtefactoCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RemoverEstudianteArtefactoMapperTest {

    @Test
    void debeMapearAObjetoDeAccion_cuandoCommandValido() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var command = new RemoverEstudianteArtefactoCommand(artefacto, asesor, estudiante);

        // Act
        var remocion = RemoverEstudianteArtefactoMapper.toDomain(command);

        // Assert
        assertThat(remocion.getArtefacto()).isEqualTo(artefacto);
        assertThat(remocion.getAsesor()).isEqualTo(asesor);
        assertThat(remocion.getEstudiante()).isEqualTo(estudiante);
    }
}
