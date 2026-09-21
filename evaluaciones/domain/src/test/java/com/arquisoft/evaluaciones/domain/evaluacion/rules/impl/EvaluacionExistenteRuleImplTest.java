package com.arquisoft.evaluaciones.domain.evaluacion.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacion.exception.EvaluacionNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ExistenciaEvaluacion;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionExistenteRuleImplTest {

    private final EvaluacionExistenteRuleImpl rule = new EvaluacionExistenteRuleImpl();

    @Test
    void noDebeLanzar_cuandoLaEvaluacionExiste() {
        // Arrange
        var existencia = new ExistenciaEvaluacion(UUID.randomUUID(), true);

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarEvaluacionNoEncontradaException_cuandoLaEvaluacionNoExiste() {
        // Arrange
        var evaluacion = UUID.randomUUID();
        var existencia = new ExistenciaEvaluacion(evaluacion, false);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOf(EvaluacionNoEncontradaException.class);
    }
}
