package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.validator.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoPerteneceEstudianteException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEvaluacionesCualitativasJuradoValidatorImplTest {

    private final ConsultarEvaluacionesCualitativasJuradoValidatorImpl validator =
            new ConsultarEvaluacionesCualitativasJuradoValidatorImpl();

    @Test
    void noDebeLanzar_cuandoExisteYPertenece() {
        // Act & Assert
        assertThatCode(() -> validator.validar(UUID.randomUUID(), true, true))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(UUID.randomUUID(), false, true))
                .isInstanceOf(EvaluacionJuradoNoEncontradaException.class);
    }

    @Test
    void debeLanzarNoPertenece_cuandoExisteYNoPertenece() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(UUID.randomUUID(), true, false))
                .isInstanceOf(EvaluacionJuradoNoPerteneceEstudianteException.class);
    }

    @Test
    void debeGanarLaExcepcionDeNoEncontrada_cuandoNiExisteNiPertenece() {
        // Act & Assert — RN-04: la existencia se valida antes que la propiedad
        assertThatThrownBy(() -> validator.validar(UUID.randomUUID(), false, false))
                .isInstanceOf(EvaluacionJuradoNoEncontradaException.class);
    }
}
