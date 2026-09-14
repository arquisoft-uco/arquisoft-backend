package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionJuradoExistenteRuleImplTest {

    private final EvaluacionJuradoExistenteRuleImpl regla = new EvaluacionJuradoExistenteRuleImpl();

    @Test
    void debePermitirFlujo_cuandoEvaluacionJuradoExiste() {
        // Arrange
        var existencia = new ExistenciaEvaluacionJurado(UUID.randomUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoEvaluacionJuradoNoExiste() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        var existencia = new ExistenciaEvaluacionJurado(evaluacionJurado, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOfSatisfying(EvaluacionJuradoNoEncontradaException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_NO_ENCONTRADA));
    }
}
