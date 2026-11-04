package com.arquisoft.evaluaciones.domain.itemcualitativojurado.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcualitativojurado.exception.ItemCualitativoJuradoEnUsoException;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.model.UsoItemCualitativoJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemCualitativoJuradoSinUsoRuleImplTest {

    private final ItemCualitativoJuradoSinUsoRuleImpl regla = new ItemCualitativoJuradoSinUsoRuleImpl();

    @Test
    void debeNoLanzar_cuandoItemNoEstaEnUso() {
        // Arrange
        var uso = new UsoItemCualitativoJurado(UUID.randomUUID(), false);

        // Act & Assert
        assertThatCode(() -> regla.validar(uso)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarItemCualitativoJuradoEnUsoException_cuandoItemEstaEnUso() {
        // Arrange
        var uso = new UsoItemCualitativoJurado(UUID.randomUUID(), true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(uso))
                .isInstanceOfSatisfying(
                        ItemCualitativoJuradoEnUsoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCualitativoJurado.ITEM_EN_USO));
    }
}
