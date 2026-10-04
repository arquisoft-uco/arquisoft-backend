package com.arquisoft.fichas.domain.estadofichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilRepetidoException;
import com.arquisoft.fichas.domain.estadofichaperfil.model.TransicionEstadoFicha;
import com.arquisoft.shared.message.constant.FichasCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoFichaPerfilNoRepetidoRuleImplTest {

    private final EstadoFichaPerfilNoRepetidoRuleImpl regla = new EstadoFichaPerfilNoRepetidoRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoElEstadoNuevoEsElActual() {
        // Arrange
        var transicion = new TransicionEstadoFicha(UUID.randomUUID(),
                EstadoFicha.EN_CONSTRUCCION, EstadoFicha.EN_CONSTRUCCION);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(transicion))
                .isInstanceOf(EstadoFichaPerfilRepetidoException.class)
                .extracting("codigoError")
                .isEqualTo(FichasCodes.EstadoFichaPerfil.ESTADO_REPETIDO);
    }

    @Test
    void debePasar_cuandoElEstadoNuevoEsDistintoDelActual() {
        // Arrange
        var transicion = new TransicionEstadoFicha(UUID.randomUUID(),
                EstadoFicha.EN_CONSTRUCCION, EstadoFicha.DESCARTADA);

        // Act & Assert
        assertThatCode(() -> regla.validar(transicion)).doesNotThrowAnyException();
    }
}
