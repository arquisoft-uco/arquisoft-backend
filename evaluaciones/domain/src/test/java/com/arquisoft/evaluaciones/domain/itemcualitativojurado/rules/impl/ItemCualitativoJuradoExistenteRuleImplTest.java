package com.arquisoft.evaluaciones.domain.itemcualitativojurado.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcualitativojurado.exception.ItemCualitativoJuradoNoEncontradoException;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.model.ExistenciaItemCualitativoJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemCualitativoJuradoExistenteRuleImplTest {

    private final ItemCualitativoJuradoExistenteRuleImpl regla =
            new ItemCualitativoJuradoExistenteRuleImpl();

    @Test
    void debePermitirFlujo_cuandoItemExiste() {
        // Arrange
        var existencia = new ExistenciaItemCualitativoJurado(UUID.randomUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoItemNoExiste() {
        // Arrange
        UUID itemCualitativoJurado = UUID.randomUUID();
        var existencia = new ExistenciaItemCualitativoJurado(itemCualitativoJurado, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOfSatisfying(
                        ItemCualitativoJuradoNoEncontradoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCualitativoJurado.ITEM_NO_ENCONTRADO));
    }
}
