package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.validator.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoEncontradaException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEvaluacionesCuantitativasJuradoValidatorImplTest {

    private final ConsultarEvaluacionesCuantitativasJuradoValidatorImpl validator =
            new ConsultarEvaluacionesCuantitativasJuradoValidatorImpl();

    @Test
    void noDebeLanzar_cuandoLaEvaluacionExiste() {
        // Act & Assert
        assertThatCode(() -> validator.validar(UUID.randomUUID(), true))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(UUID.randomUUID(), false))
                .isInstanceOf(EvaluacionJuradoNoEncontradaException.class);
    }
}
