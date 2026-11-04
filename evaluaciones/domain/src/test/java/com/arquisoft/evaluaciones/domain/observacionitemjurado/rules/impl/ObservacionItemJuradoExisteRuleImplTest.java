package com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.impl;

import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.ObservacionItemJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.model.ExistenciaObservacionItemJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObservacionItemJuradoExisteRuleImplTest {

    private final ObservacionItemJuradoExisteRuleImpl rule = new ObservacionItemJuradoExisteRuleImpl();

    @Test
    void noDebeLanzar_cuandoLaObservacionExiste() {
        // Arrange
        var existencia = new ExistenciaObservacionItemJurado(UUID.randomUUID(), true);

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoLaObservacionNoExiste() {
        // Arrange
        var observacionItemJurado = UUID.randomUUID();
        var existencia = new ExistenciaObservacionItemJurado(observacionItemJurado, false);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOfSatisfying(ObservacionItemJuradoNoEncontradaException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.ObservacionItemJurado.NO_ENCONTRADA);
                    assertThat(exception.getMessage()).contains(observacionItemJurado.toString());
                });
    }
}
