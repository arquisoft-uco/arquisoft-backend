package com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.mapper;

import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model.AgregarObservacionEvaluacionCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AgregarObservacionEvaluacionMapperTest {

    @Test
    void debeConstruirAgregacion_conObservacionYRepresentante() {
        // Arrange
        var evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();
        var command = AgregarObservacionEvaluacionCommand.crear(
                evaluacionFichaPerfil, "Observación válida", representanteComite);

        // Act
        var agregacion = AgregarObservacionEvaluacionMapper.toDomain(command);

        // Assert
        assertThat(agregacion.getObservacionEvaluacion().getId()).isNotNull();
        assertThat(agregacion.getEvaluacionFichaPerfil()).isEqualTo(evaluacionFichaPerfil);
        assertThat(agregacion.getObservacion()).isEqualTo("Observación válida");
        assertThat(agregacion.getRepresentanteComite()).isEqualTo(representanteComite);
    }
}
