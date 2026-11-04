package com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.mapper;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.model.AgregarEstudiantesProyectoGradoCommand;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AgregarEstudiantesProyectoGradoMapperTest {

    @Test
    void debeArmarUnVinculoPorEstudiante_cuandoSeMapeaElCommand() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var estudianteA = UtilUUID.generarNuevoUUID();
        var estudianteB = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-10-02T10:00:00Z");
        var command = new AgregarEstudiantesProyectoGradoCommand(
                proyecto, List.of(estudianteA, estudianteB), ocurridoEn);

        // Act
        var agregacion = AgregarEstudiantesProyectoGradoMapper.toDomain(command);

        // Assert
        assertThat(agregacion.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(agregacion.getVinculos()).extracting(EstudianteProyectoGradoDomain::getEstudiante)
                .containsExactly(estudianteA, estudianteB);
        assertThat(agregacion.getVinculos()).extracting(EstudianteProyectoGradoDomain::getOcurridoEn)
                .containsOnly(ocurridoEn);
    }
}
