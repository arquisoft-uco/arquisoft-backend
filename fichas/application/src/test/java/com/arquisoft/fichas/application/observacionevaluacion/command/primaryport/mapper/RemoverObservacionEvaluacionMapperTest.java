package com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model.RemoverObservacionEvaluacionCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RemoverObservacionEvaluacionMapperTest {

    @Test
    void debeConstruirRemocion_conLosDosCamposDelCommand() {
        // Arrange
        var observacionEvaluacion = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();
        var command = RemoverObservacionEvaluacionCommand.crear(observacionEvaluacion, representanteComite);

        // Act
        var remocion = RemoverObservacionEvaluacionMapper.toDomain(command);

        // Assert
        assertThat(remocion.getObservacionEvaluacion()).isEqualTo(observacionEvaluacion);
        assertThat(remocion.getRepresentanteComite()).isEqualTo(representanteComite);
    }
}
