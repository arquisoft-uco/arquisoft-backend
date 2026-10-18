package com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception.ItemCuantitativoJuradoEnUsoException;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.model.UsoItemCuantitativoJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemCuantitativoJuradoSinUsoRuleImplTest {

    private final ItemCuantitativoJuradoSinUsoRuleImpl regla = new ItemCuantitativoJuradoSinUsoRuleImpl();

    @Test
    void debeNoLanzar_cuandoItemNoEstaEnUso() {
        // Arrange
        var uso = new UsoItemCuantitativoJurado(UUID.randomUUID(), false);

        // Act & Assert
        assertThatCode(() -> regla.validar(uso)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarEnUso_cuandoItemEstaEnUso() {
        // Arrange
        var uso = new UsoItemCuantitativoJurado(UUID.randomUUID(), true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(uso))
                .isInstanceOfSatisfying(
                        ItemCuantitativoJuradoEnUsoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCuantitativoJurado.ITEM_EN_USO));
    }
}
