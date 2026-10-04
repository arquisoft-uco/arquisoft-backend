package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilNoDisponibleParaEvaluacionException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.shared.message.constant.FichasCodes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoFichaPerfilDisponibleParaEvaluacionRuleImplTest {

    private final EstadoFichaPerfilDisponibleParaEvaluacionRuleImpl regla =
            new EstadoFichaPerfilDisponibleParaEvaluacionRuleImpl();

    @Test
    void debePasar_cuandoLaFichaEstaDisponibleParaEvaluacion() {
        // Arrange
        var estado = new EstadoActualFicha(UUID.randomUUID(), EstadoFicha.DISPONIBLE_PARA_EVALUACION);

        // Act & Assert
        assertThatCode(() -> regla.validar(estado)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoFicha.class, names = "DISPONIBLE_PARA_EVALUACION", mode = EnumSource.Mode.EXCLUDE)
    void debeLanzarExcepcion_cuandoLaFichaEstaEnCualquierOtroEstado(EstadoFicha estadoActual) {
        // Arrange
        var estado = new EstadoActualFicha(UUID.randomUUID(), estadoActual);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(estado))
                .isInstanceOf(FichaPerfilNoDisponibleParaEvaluacionException.class)
                .extracting("codigoError")
                .isEqualTo(FichasCodes.EstadoFichaPerfil.NO_DISPONIBLE_PARA_EVALUACION);
    }
}
