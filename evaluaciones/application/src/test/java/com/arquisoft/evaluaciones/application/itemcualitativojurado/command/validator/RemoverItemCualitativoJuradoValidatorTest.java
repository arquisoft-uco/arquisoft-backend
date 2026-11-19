package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator.impl.RemoverItemCualitativoJuradoValidatorImpl;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.exception.ItemCualitativoJuradoNoEncontradoException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverItemCualitativoJuradoValidatorTest {

    private final RemoverItemCualitativoJuradoValidatorImpl validator =
            new RemoverItemCualitativoJuradoValidatorImpl();

    @Test
    void debeNoLanzar_cuandoItemExiste() {
        // Arrange
        var itemCualitativoJurado = UUID.randomUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(itemCualitativoJurado, true))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoItemNoExiste() {
        // Arrange
        var itemCualitativoJurado = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(itemCualitativoJurado, false))
                .isInstanceOfSatisfying(
                        ItemCualitativoJuradoNoEncontradoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCualitativoJurado.ITEM_NO_ENCONTRADO));
    }
}
