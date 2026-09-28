package com.arquisoft.evaluaciones.domain.evaluacion.rules.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.exception.EvaluacionFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacion.model.EstadoRegistroEvaluacion;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionAdmiteRegistroRuleImplTest {

    private final EvaluacionAdmiteRegistroRuleImpl regla = new EvaluacionAdmiteRegistroRuleImpl();

    @Test
    void debePermitirFlujo_cuandoEstadoEsPendienteOEnProgreso() {
        // Act & Assert
        assertThatCode(() -> regla.validar(new EstadoRegistroEvaluacion(EstadoEvaluacion.PENDIENTE)))
                .doesNotThrowAnyException();
        assertThatCode(() -> regla.validar(new EstadoRegistroEvaluacion(EstadoEvaluacion.EN_PROGRESO)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoEstadoEsFinalizada() {
        // Act & Assert
        assertThatThrownBy(() -> regla.validar(new EstadoRegistroEvaluacion(EstadoEvaluacion.FINALIZADA)))
                .isInstanceOfSatisfying(EvaluacionFinalizadaException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.Evaluacion.ESTADO_FINALIZADA));
    }
}
