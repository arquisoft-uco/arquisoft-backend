package com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.mapper;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.model.DestituirEstudianteProyectoGradoCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class DestituirEstudianteProyectoGradoMapperTest {

    @Test
    void debeArmarVinculoYDesvinculacion_cuandoSeMapeaElCommand() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-10-02T10:00:00Z");
        var command = new DestituirEstudianteProyectoGradoCommand(estudiante, proyecto, ocurridoEn);

        // Act
        var destitucion = DestituirEstudianteProyectoGradoMapper.toDomain(command);

        // Assert
        assertThat(destitucion.getVinculo().getEstudiante()).isEqualTo(estudiante);
        assertThat(destitucion.getVinculo().getProyectoGrado()).isEqualTo(proyecto);
        assertThat(destitucion.getVinculo().getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(destitucion.getDesvinculacion().getEstudiante()).isEqualTo(estudiante);
        assertThat(destitucion.getDesvinculacion().getProyectoGrado()).isEqualTo(proyecto);
    }
}
