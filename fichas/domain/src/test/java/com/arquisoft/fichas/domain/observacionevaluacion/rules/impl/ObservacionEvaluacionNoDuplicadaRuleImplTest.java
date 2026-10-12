package com.arquisoft.fichas.domain.observacionevaluacion.rules.impl;

import com.arquisoft.fichas.domain.observacionevaluacion.exception.ObservacionEvaluacionDuplicadaException;
import com.arquisoft.fichas.domain.observacionevaluacion.model.DisponibilidadObservacionEvaluacion;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObservacionEvaluacionNoDuplicadaRuleImplTest {

    private final ObservacionEvaluacionNoDuplicadaRuleImpl regla = new ObservacionEvaluacionNoDuplicadaRuleImpl();

    @Test
    void debePasar_cuandoLaObservacionNoExiste() {
        // Arrange
        var disponibilidad = new DisponibilidadObservacionEvaluacion(
                UtilUUID.generarNuevoUUID(), "Observación válida", false);

        // Act & Assert
        assertThatCode(() -> regla.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarDuplicada_cuandoLaObservacionYaExiste() {
        // Arrange
        var disponibilidad = new DisponibilidadObservacionEvaluacion(
                UtilUUID.generarNuevoUUID(), "Observación válida", true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(disponibilidad))
                .isInstanceOf(ObservacionEvaluacionDuplicadaException.class)
                .extracting("codigoError")
                .isEqualTo(FichasCodes.ObservacionEvaluacion.OBSERVACION_EVALUACION_DUPLICADA);
    }
}
