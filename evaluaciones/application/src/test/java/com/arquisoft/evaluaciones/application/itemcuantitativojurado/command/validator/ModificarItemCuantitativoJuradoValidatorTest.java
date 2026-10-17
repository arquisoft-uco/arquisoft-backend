package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.validator;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.validator.impl.ModificarItemCuantitativoJuradoValidatorImpl;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception.ItemCuantitativoJuradoNoEncontradoException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarItemCuantitativoJuradoValidatorTest {

    private final ModificarItemCuantitativoJuradoValidatorImpl validator =
            new ModificarItemCuantitativoJuradoValidatorImpl();

    @Test
    void debeAceptar_cuandoItemExiste() {
        // Arrange
        var itemCuantitativoJurado = UUID.randomUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(itemCuantitativoJurado, true))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoItemNoExiste() {
        // Arrange
        var itemCuantitativoJurado = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(itemCuantitativoJurado, false))
                .isInstanceOfSatisfying(
                        ItemCuantitativoJuradoNoEncontradoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCuantitativoJurado.ITEM_NO_ENCONTRADO));
    }
}
