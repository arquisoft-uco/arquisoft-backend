package com.arquisoft.fichas.domain.observacionevaluacion.rules.impl;

import com.arquisoft.fichas.domain.observacionevaluacion.exception.ObservacionEvaluacionNoEncontradaException;
import com.arquisoft.fichas.domain.observacionevaluacion.model.ExistenciaObservacionEvaluacion;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObservacionEvaluacionExisteRuleImplTest {

    private final ObservacionEvaluacionExisteRuleImpl regla = new ObservacionEvaluacionExisteRuleImpl();

    @Test
    void debePasar_cuandoLaObservacionExiste() {
        // Arrange
        var existencia = new ExistenciaObservacionEvaluacion(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoLaObservacionNoExiste() {
        // Arrange
        var observacionEvaluacion = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaObservacionEvaluacion(observacionEvaluacion, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOf(ObservacionEvaluacionNoEncontradaException.class)
                .hasMessageContaining(observacionEvaluacion.toString())
                .extracting("codigoError")
                .isEqualTo(FichasCodes.ObservacionEvaluacion.OBSERVACION_EVALUACION_NO_ENCONTRADA);
    }
}
