package com.arquisoft.fichas.application.estadoevaluacionficha.command.validator;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.impl.AsignarEstadoInicialEvaluacionValidatorImpl;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaPerfilNoEncontradaException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// El validator solo orquesta reglas puras: no necesita Mockito.
class AsignarEstadoInicialEvaluacionValidatorTest {

    private final AsignarEstadoInicialEvaluacionValidator validator =
            new AsignarEstadoInicialEvaluacionValidatorImpl();

    @Test
    void debePasar_cuandoLaEvaluacionExiste() {
        // Act & Assert
        assertThatCode(() -> validator.validar(UUID.randomUUID(), true)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoLaEvaluacionNoExiste() {
        // Arrange
        UUID evaluacion = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(evaluacion, false))
                .isInstanceOf(EvaluacionFichaPerfilNoEncontradaException.class)
                .hasMessageContaining(evaluacion.toString());
    }
}
