package com.arquisoft.evaluaciones.application.evaluacionjurado.query.validator.impl;

import com.arquisoft.evaluaciones.domain.jurado.exception.JuradoNoEncontradoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEvaluacionesAsignadasJuradoValidatorImplTest {

    private final ConsultarEvaluacionesAsignadasJuradoValidatorImpl validator =
            new ConsultarEvaluacionesAsignadasJuradoValidatorImpl();

    @Test
    void noDebeLanzar_cuandoElJuradoExiste() {
        // Act & Assert
        assertThatCode(() -> validator.validar(UtilUUID.generarNuevoUUID(), true))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarJuradoNoEncontrado_cuandoNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(UtilUUID.generarNuevoUUID(), false))
                .isInstanceOf(JuradoNoEncontradoException.class);
    }
}
