package com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EstadoEnEvaluacionNoManualException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.SolicitudEstadoEvaluacion;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoEnEvaluacionNoManualRuleImplTest {

    private final EstadoEnEvaluacionNoManualRuleImpl regla = new EstadoEnEvaluacionNoManualRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoSeSolicitaEnEvaluacion() {
        // Arrange
        var solicitud = new SolicitudEstadoEvaluacion(UUID.randomUUID(), EstadoEvaluacion.EN_EVALUACION);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(solicitud))
                .isInstanceOf(EstadoEnEvaluacionNoManualException.class);
    }

    @Test
    void debePasar_cuandoSeSolicitaCualquierOtroEstado() {
        // Arrange
        var solicitud = new SolicitudEstadoEvaluacion(UUID.randomUUID(), EstadoEvaluacion.APROBADA);

        // Act & Assert
        assertThatCode(() -> regla.validar(solicitud)).doesNotThrowAnyException();
    }
}
