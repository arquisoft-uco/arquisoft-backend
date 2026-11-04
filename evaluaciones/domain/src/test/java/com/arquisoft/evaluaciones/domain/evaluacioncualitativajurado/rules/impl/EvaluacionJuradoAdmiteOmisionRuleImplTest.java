package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.EstadoOmisionEvaluacionesCualitativasJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionJuradoAdmiteOmisionRuleImplTest {

    private final EvaluacionJuradoAdmiteOmisionRuleImpl regla = new EvaluacionJuradoAdmiteOmisionRuleImpl();

    @ParameterizedTest
    @EnumSource(value = EstadoEvaluacion.class, names = {"PENDIENTE", "EN_PROGRESO"})
    void debePermitirFlujo_cuandoLaEvaluacionNoEstaFinalizada(EstadoEvaluacion estado) {
        // Arrange
        var entrada = new EstadoOmisionEvaluacionesCualitativasJurado(UUID.randomUUID(), estado);

        // Act & Assert
        assertThatCode(() -> regla.validar(entrada)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoLaEvaluacionEstaFinalizada() {
        // Arrange
        var entrada = new EstadoOmisionEvaluacionesCualitativasJurado(UUID.randomUUID(), EstadoEvaluacion.FINALIZADA);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(entrada))
                .isInstanceOfSatisfying(EvaluacionJuradoFinalizadaException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_FINALIZADA));
    }
}
