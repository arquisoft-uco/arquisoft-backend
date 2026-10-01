package com.arquisoft.fichas.domain.itemfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropiedadFicha;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaNoPropiaException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemFichaPropiaRuleImplTest {

    private final ItemFichaPropiaRuleImpl regla = new ItemFichaPropiaRuleImpl();

    @Test
    void debeLanzarExcepcion_cuandoLaFichaNoEsDelEstudiante() {
        // Arrange
        var propiedad = new PropiedadFicha(UUID.randomUUID(), UUID.randomUUID(), false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(propiedad))
                .isInstanceOf(ItemFichaNoPropiaException.class);
    }

    @Test
    void debePasar_cuandoLaFichaEsDelEstudiante() {
        // Arrange
        var propiedad = new PropiedadFicha(UUID.randomUUID(), UUID.randomUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(propiedad)).doesNotThrowAnyException();
    }
}
