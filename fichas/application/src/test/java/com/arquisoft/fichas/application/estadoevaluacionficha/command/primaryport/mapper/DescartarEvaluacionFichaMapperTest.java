package com.arquisoft.fichas.application.estadoevaluacionficha.command.primaryport.mapper;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.primaryport.model.DescartarEvaluacionFichaCommand;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DescartarEvaluacionFichaMapperTest {

    @Test
    void debeConstruirAgregacionConEstadoDescartada() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var representante = UtilUUID.generarNuevoUUID();
        var command = DescartarEvaluacionFichaCommand.crear(evaluacion, representante);

        // Act
        var agregacion = DescartarEvaluacionFichaMapper.toDomain(command);

        // Assert
        assertThat(agregacion.getEvaluacionFichaPerfil()).isEqualTo(evaluacion);
        assertThat(agregacion.getRepresentanteComite()).isEqualTo(representante);
        assertThat(agregacion.getEstadoEvaluacion()).isEqualTo(EstadoEvaluacion.DESCARTADA);
    }
}
