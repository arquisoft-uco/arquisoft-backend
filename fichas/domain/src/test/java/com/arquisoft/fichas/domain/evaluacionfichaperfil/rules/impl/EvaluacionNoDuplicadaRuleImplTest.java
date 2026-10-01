package com.arquisoft.fichas.domain.evaluacionfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.exception.EvaluacionFichaPerfilDuplicadaException;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.DisponibilidadEvaluacionFicha;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionNoDuplicadaRuleImplTest {

    private final EvaluacionNoDuplicadaRuleImpl regla = new EvaluacionNoDuplicadaRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoElRepresentanteYaEvaluoLaFicha() {
        // Arrange
        var disponibilidad = new DisponibilidadEvaluacionFicha(UUID.randomUUID(), UUID.randomUUID(), true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(disponibilidad))
                .isInstanceOf(EvaluacionFichaPerfilDuplicadaException.class);
    }

    @Test
    void debePasar_cuandoElRepresentanteNoHaEvaluadoLaFicha() {
        // Arrange
        var disponibilidad = new DisponibilidadEvaluacionFicha(UUID.randomUUID(), UUID.randomUUID(), false);

        // Act & Assert
        assertThatCode(() -> regla.validar(disponibilidad)).doesNotThrowAnyException();
    }
}
