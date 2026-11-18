package com.arquisoft.evaluaciones.domain.evaluacion.rules.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.exception.EvaluacionFinalizadaAsignacionException;
import com.arquisoft.evaluaciones.domain.evaluacion.model.EstadoAsignacionEvaluacion;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionAdmiteCambioAsignacionRuleImplTest {

    private final EvaluacionAdmiteCambioAsignacionRuleImpl regla = new EvaluacionAdmiteCambioAsignacionRuleImpl();

    @Test
    void debePermitirFlujo_cuandoEstadoEsPendienteOEnProgreso() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new EstadoAsignacionEvaluacion(EstadoEvaluacion.PENDIENTE)))
                .doesNotThrowAnyException();
        assertThatCode(() -> regla.validar(new EstadoAsignacionEvaluacion(EstadoEvaluacion.EN_PROGRESO)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoEstadoEsFinalizada() {
        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new EstadoAsignacionEvaluacion(EstadoEvaluacion.FINALIZADA)))
                .isInstanceOfSatisfying(EvaluacionFinalizadaAsignacionException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.Evaluacion.ASIGNACION_FINALIZADA));
    }
}
