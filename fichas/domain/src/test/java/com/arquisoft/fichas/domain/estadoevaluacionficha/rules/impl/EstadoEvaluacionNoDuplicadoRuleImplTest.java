package com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EstadoEvaluacionDuplicadoException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.DisponibilidadEstadoEvaluacion;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoEvaluacionNoDuplicadoRuleImplTest {

    private final EstadoEvaluacionNoDuplicadoRuleImpl regla = new EstadoEvaluacionNoDuplicadoRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoLaEvaluacionYaTieneEseEstado() {
        // Arrange
        var disponibilidad = new DisponibilidadEstadoEvaluacion(
                UUID.randomUUID(), EstadoEvaluacion.APROBADA, true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(disponibilidad))
                .isInstanceOf(EstadoEvaluacionDuplicadoException.class);
    }

    @Test
    void debePasar_cuandoLaEvaluacionNoTieneEseEstado() {
        // Arrange
        var disponibilidad = new DisponibilidadEstadoEvaluacion(
                UUID.randomUUID(), EstadoEvaluacion.APROBADA, false);

        // Act & Assert
        assertThatCode(() -> regla.validar(disponibilidad)).doesNotThrowAnyException();
    }
}
