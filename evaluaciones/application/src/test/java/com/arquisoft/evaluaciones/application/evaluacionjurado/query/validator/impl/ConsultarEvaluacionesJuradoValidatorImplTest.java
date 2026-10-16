package com.arquisoft.evaluaciones.application.evaluacionjurado.query.validator.impl;

import com.arquisoft.evaluaciones.domain.evaluacion.exception.EvaluacionNoEncontradaException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEvaluacionesJuradoValidatorImplTest {

    private final ConsultarEvaluacionesJuradoValidatorImpl validator = new ConsultarEvaluacionesJuradoValidatorImpl();

    @Test
    void noDebeLanzar_cuandoLaEvaluacionExiste() {
        // Act & Assert
        assertThatCode(() -> validator.validar(UUID.randomUUID(), true)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarEvaluacionNoEncontradaException_cuandoLaEvaluacionNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(UUID.randomUUID(), false))
                .isInstanceOf(EvaluacionNoEncontradaException.class);
    }
}
