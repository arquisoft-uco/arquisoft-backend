package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model.CambiarCriterioEvaluacionCualitativaJuradoCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CambiarCriterioEvaluacionCualitativaJuradoMapperTest {

    @Test
    void debeMapearAlObjetoDeAccion_cuandoCommandValido() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var command = new CambiarCriterioEvaluacionCualitativaJuradoCommand(evaluacion, criterio, jurado);

        // Act
        var cambio = CambiarCriterioEvaluacionCualitativaJuradoMapper.toDomain(command);

        // Assert
        assertThat(cambio.getEvaluacionCualitativaJurado()).isEqualTo(evaluacion);
        assertThat(cambio.getCriterio()).isEqualTo(criterio);
        assertThat(cambio.getJurado()).isEqualTo(jurado);
    }
}
