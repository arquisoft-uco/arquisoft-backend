package com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.model.RegistrarObservacionItemAsesorCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrarObservacionItemAsesorMapperTest {

    @Test
    void debeMapearAlObjetoDeAccion() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var command = RegistrarObservacionItemAsesorCommand.crear(evaluacion, asesor, "Sustento");

        // Act
        var registro = RegistrarObservacionItemAsesorMapper.toDomain(command);

        // Assert
        assertThat(registro.getAsesor()).isEqualTo(asesor);
        assertThat(registro.getObservacion().getEvaluacionCuantitativaAsesor()).isEqualTo(evaluacion);
        assertThat(registro.getObservacion().getDescripcion()).isEqualTo("Sustento");
        assertThat(registro.getObservacion().getId()).isNotNull();
    }
}
