package com.arquisoft.evaluaciones.application.evaluacion.command.validator;

import com.arquisoft.evaluaciones.application.evaluacion.command.validator.impl.IniciarEvaluacionValidatorImpl;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.exception.EvaluacionFinalizadaException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IniciarEvaluacionValidatorTest {

    private final IniciarEvaluacionValidatorImpl validator = new IniciarEvaluacionValidatorImpl();

    @Test
    void debePermitirFlujo_cuandoEstadoEsPendienteOEnProgreso() {
        // Act & Assert
        assertThatCode(() -> validator.validar(EstadoEvaluacion.PENDIENTE)).doesNotThrowAnyException();
        assertThatCode(() -> validator.validar(EstadoEvaluacion.EN_PROGRESO)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoEstadoEsFinalizada() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(EstadoEvaluacion.FINALIZADA))
                .isInstanceOfSatisfying(EvaluacionFinalizadaException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.Evaluacion.ESTADO_FINALIZADA));
    }
}
