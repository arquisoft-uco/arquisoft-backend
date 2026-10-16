package com.arquisoft.evaluaciones.application.observacionitemjurado.query.validator.impl;

import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.EvaluacionCuantitativaJuradoNoEncontradaException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarObservacionesItemJuradoValidatorImplTest {

    private final ConsultarObservacionesItemJuradoValidatorImpl validator =
            new ConsultarObservacionesItemJuradoValidatorImpl();

    @Test
    void noDebeLanzar_cuandoLaEvaluacionCuantitativaExiste() {
        // Act & Assert
        assertThatCode(() -> validator.validar(UUID.randomUUID(), true))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_conElIdEnElMensaje_cuandoNoExiste() {
        // Arrange
        var evaluacionCuantitativaJurado = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(evaluacionCuantitativaJurado, false))
                .isInstanceOf(EvaluacionCuantitativaJuradoNoEncontradaException.class)
                .satisfies(exception -> assertThat(exception.getMessage())
                        .contains(evaluacionCuantitativaJurado.toString()));
    }
}
