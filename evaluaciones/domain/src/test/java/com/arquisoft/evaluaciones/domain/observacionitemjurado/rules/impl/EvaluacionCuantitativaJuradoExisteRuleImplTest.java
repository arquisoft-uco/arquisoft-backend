package com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.impl;

import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.EvaluacionCuantitativaJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.model.ExistenciaEvaluacionCuantitativaJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionCuantitativaJuradoExisteRuleImplTest {

    private final EvaluacionCuantitativaJuradoExisteRuleImpl rule = new EvaluacionCuantitativaJuradoExisteRuleImpl();

    @Test
    void noDebeLanzar_cuandoLaEvaluacionExiste() {
        // Arrange
        var existencia = new ExistenciaEvaluacionCuantitativaJurado(UUID.randomUUID(), true);

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoLaEvaluacionNoExiste() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();
        var existencia = new ExistenciaEvaluacionCuantitativaJurado(evaluacionCuantitativaJurado, false);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOfSatisfying(EvaluacionCuantitativaJuradoNoEncontradaException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.ObservacionItemJurado.EVALUACION_NO_ENCONTRADA);
                    assertThat(exception.getMessage()).contains(evaluacionCuantitativaJurado.toString());
                });
    }
}
