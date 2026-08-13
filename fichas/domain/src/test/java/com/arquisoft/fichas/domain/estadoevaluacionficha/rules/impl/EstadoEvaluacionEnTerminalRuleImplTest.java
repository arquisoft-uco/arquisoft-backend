package com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EstadoEvaluacionTerminalException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.UltimoEstadoEvaluacion;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoEvaluacionEnTerminalRuleImplTest {

    private final EstadoEvaluacionEnTerminalRuleImpl regla = new EstadoEvaluacionEnTerminalRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoElUltimoEstadoEsTerminal() {
        // Arrange
        var ultimo = new UltimoEstadoEvaluacion(UUID.randomUUID(), EstadoEvaluacion.APROBADA);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(ultimo))
                .isInstanceOf(EstadoEvaluacionTerminalException.class);
    }

    @Test
    void debePasar_cuandoElUltimoEstadoNoEsTerminal() {
        // Arrange
        var ultimo = new UltimoEstadoEvaluacion(UUID.randomUUID(), EstadoEvaluacion.EN_EVALUACION);

        // Act & Assert
        assertThatCode(() -> regla.validar(ultimo)).doesNotThrowAnyException();
    }

    @Test
    void debeCallar_cuandoLaEvaluacionNoTieneEstadosPrevios() {
        // Arrange
        var ultimo = new UltimoEstadoEvaluacion(UUID.randomUUID(), EstadoEvaluacion.VACIO);

        // Act & Assert
        assertThatCode(() -> regla.validar(ultimo)).doesNotThrowAnyException();
    }
}
