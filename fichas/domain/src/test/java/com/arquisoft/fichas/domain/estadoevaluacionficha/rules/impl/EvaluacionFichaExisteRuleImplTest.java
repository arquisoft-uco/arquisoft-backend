package com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.ExistenciaEvaluacionFicha;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionFichaExisteRuleImplTest {

    private final EvaluacionFichaExisteRuleImpl regla = new EvaluacionFichaExisteRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoLaEvaluacionNoExiste() {
        // Arrange
        var existencia = new ExistenciaEvaluacionFicha(UUID.randomUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOf(EvaluacionFichaPerfilNoEncontradaException.class);
    }

    @Test
    void debePasar_cuandoLaEvaluacionExiste() {
        // Arrange
        var existencia = new ExistenciaEvaluacionFicha(UUID.randomUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }
}
