package com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception.ItemCuantitativoJuradoNoEncontradoException;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.model.ExistenciaItemCuantitativoJurado;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemCuantitativoJuradoExistenteRuleImplTest {

    private final ItemCuantitativoJuradoExistenteRuleImpl regla =
            new ItemCuantitativoJuradoExistenteRuleImpl();

    @Test
    void debePermitirFlujo_cuandoItemExiste() {
        // Arrange
        var existencia = new ExistenciaItemCuantitativoJurado(UUID.randomUUID(), true);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoItemNoExiste() {
        // Arrange
        var itemCuantitativoJurado = UUID.randomUUID();
        var existencia = new ExistenciaItemCuantitativoJurado(itemCuantitativoJurado, false);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOfSatisfying(
                        ItemCuantitativoJuradoNoEncontradoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCuantitativoJurado.ITEM_NO_ENCONTRADO));
    }
}
