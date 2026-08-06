package com.arquisoft.fichas.domain.itemfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemConRevisionesException;
import com.arquisoft.fichas.domain.itemfichaperfil.model.RevisionesItemCriteria;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemSinRevisionesRuleImplTest {

    private final ItemSinRevisionesRuleImpl regla = new ItemSinRevisionesRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoTieneRevisiones() {
        // Arrange
        UUID item = UUID.randomUUID();
        var criteria = new RevisionesItemCriteria(item, 1);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(criteria))
                .isInstanceOf(ItemConRevisionesException.class)
                .hasMessageContaining(item.toString());
    }

    @Test
    void debePasar_cuandoSinRevisiones() {
        // Arrange
        var criteria = new RevisionesItemCriteria(UUID.randomUUID(), 0);

        // Act & Assert
        assertThatCode(() -> regla.validar(criteria)).doesNotThrowAnyException();
    }
}
