package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.validator.impl.ModificarItemCualitativoJuradoValidatorImpl;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.exception.ItemCualitativoJuradoNoEncontradoException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarItemCualitativoJuradoValidatorTest {

    private final ModificarItemCualitativoJuradoValidatorImpl validator =
            new ModificarItemCualitativoJuradoValidatorImpl();

    @Test
    void debeAceptar_cuandoItemExiste() {
        // Arrange
        UUID itemCualitativoJurado = UUID.randomUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(itemCualitativoJurado, true))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoItemNoExiste() {
        // Arrange
        UUID itemCualitativoJurado = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(itemCualitativoJurado, false))
                .isInstanceOfSatisfying(
                        ItemCualitativoJuradoNoEncontradoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCualitativoJurado.ITEM_NO_ENCONTRADO));
    }
}
