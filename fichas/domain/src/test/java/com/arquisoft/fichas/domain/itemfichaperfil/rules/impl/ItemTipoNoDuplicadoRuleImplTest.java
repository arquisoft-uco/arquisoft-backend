package com.arquisoft.fichas.domain.itemfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemTipoDuplicadoException;
import com.arquisoft.fichas.domain.itemfichaperfil.model.DisponibilidadTipoItem;
import com.arquisoft.fichas.domain.tipoitem.TipoItem;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemTipoNoDuplicadoRuleImplTest {

    private final ItemTipoNoDuplicadoRuleImpl regla = new ItemTipoNoDuplicadoRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoLaFichaYaTieneUnItemDeEseTipo() {
        // Arrange
        var disponibilidad = new DisponibilidadTipoItem(TipoItem.OBJETIVO_GENERAL, true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(disponibilidad))
                .isInstanceOf(ItemTipoDuplicadoException.class);
    }

    @Test
    void debePasar_cuandoLaFichaNoTieneItemDeEseTipo() {
        // Arrange
        var disponibilidad = new DisponibilidadTipoItem(TipoItem.OBJETIVO_GENERAL, false);

        // Act & Assert
        assertThatCode(() -> regla.validar(disponibilidad)).doesNotThrowAnyException();
    }
}
