package com.arquisoft.fichas.domain.observacionitem.rules.impl;

import com.arquisoft.fichas.domain.observacionitem.exception.ObservacionItemNoEncontradaException;
import com.arquisoft.fichas.domain.observacionitem.model.ExistenciaObservacionItem;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObservacionItemExisteRuleImplTest {

    private final ObservacionItemExisteRuleImpl regla = new ObservacionItemExisteRuleImpl();

    @Test
    void debePasar_cuandoLaObservacionExiste() {
        // Arrange
        var existencia = new ExistenciaObservacionItem(UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrada_cuandoLaObservacionNoExiste() {
        // Arrange
        var existencia = new ExistenciaObservacionItem(UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOf(ObservacionItemNoEncontradaException.class)
                .hasFieldOrPropertyWithValue("codigoError", FichasCodes.ObservacionItem.NO_ENCONTRADA);
    }
}
