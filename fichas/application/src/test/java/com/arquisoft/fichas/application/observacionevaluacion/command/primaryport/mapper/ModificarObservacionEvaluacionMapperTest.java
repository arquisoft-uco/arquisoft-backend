package com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model.ModificarObservacionEvaluacionCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarObservacionEvaluacionMapperTest {

    @Test
    void debeConstruirModificacion_conLosTresCamposDelCommand() {
        // Arrange
        var observacionEvaluacion = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();
        var command = ModificarObservacionEvaluacionCommand.crear(
                observacionEvaluacion, "Nuevo texto de la observación", representanteComite);

        // Act
        var modificacion = ModificarObservacionEvaluacionMapper.toDomain(command);

        // Assert
        assertThat(modificacion.getObservacionEvaluacion()).isEqualTo(observacionEvaluacion);
        assertThat(modificacion.getObservacion()).isEqualTo("Nuevo texto de la observación");
        assertThat(modificacion.getRepresentanteComite()).isEqualTo(representanteComite);
    }
}
