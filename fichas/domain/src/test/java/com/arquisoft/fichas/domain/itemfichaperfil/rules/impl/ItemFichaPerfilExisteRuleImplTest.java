package com.arquisoft.fichas.domain.itemfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.itemfichaperfil.model.ExistenciaItemFichaPerfil;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemFichaPerfilExisteRuleImplTest {

    private final ItemFichaPerfilExisteRuleImpl regla = new ItemFichaPerfilExisteRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoElItemNoExiste() {
        // Arrange
        var fichaDelItem = new ExistenciaItemFichaPerfil(UUID.randomUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(fichaDelItem))
                .isInstanceOf(ItemFichaPerfilNoEncontradoException.class);
    }

    @Test
    void debePasar_cuandoElItemExiste() {
        // Arrange
        var fichaDelItem = new ExistenciaItemFichaPerfil(UUID.randomUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(fichaDelItem)).doesNotThrowAnyException();
    }
}
