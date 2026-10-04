package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.TransicionEstadoFichaNoPermitidaException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.TransicionEstadoFicha;
import com.arquisoft.shared.message.constant.FichasCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransicionEstadoFichaPermitidaRuleImplTest {

    private final TransicionEstadoFichaPermitidaRuleImpl regla = new TransicionEstadoFichaPermitidaRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoSePasaDeDescartadaADisponibleParaEvaluacion() {
        // Arrange
        var transicion = new TransicionEstadoFicha(UUID.randomUUID(),
                EstadoFicha.DESCARTADA, EstadoFicha.DISPONIBLE_PARA_EVALUACION);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(transicion))
                .isInstanceOf(TransicionEstadoFichaNoPermitidaException.class)
                .extracting("codigoError")
                .isEqualTo(FichasCodes.EstadoFichaPerfil.TRANSICION_NO_PERMITIDA);
    }

    @Test
    void debePasar_cuandoLaTransicionEstaEnLaMatriz() {
        // Arrange
        var transicion = new TransicionEstadoFicha(UUID.randomUUID(),
                EstadoFicha.DESCARTADA, EstadoFicha.EN_CONSTRUCCION);

        // Act & Assert
        assertThatCode(() -> regla.validar(transicion)).doesNotThrowAnyException();
    }
}
