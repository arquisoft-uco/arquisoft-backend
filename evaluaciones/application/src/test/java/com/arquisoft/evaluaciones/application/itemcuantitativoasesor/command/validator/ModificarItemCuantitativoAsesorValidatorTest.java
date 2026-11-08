package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator.impl.ModificarItemCuantitativoAsesorValidatorImpl;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception.ItemCuantitativoAsesorNoEncontradoException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarItemCuantitativoAsesorValidatorTest {

    private final ModificarItemCuantitativoAsesorValidatorImpl validator =
            new ModificarItemCuantitativoAsesorValidatorImpl();

    @Test
    void noDebeLanzar_cuandoItemExiste() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(item, true)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoItemNoExiste() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(item, false))
                .isInstanceOfSatisfying(ItemCuantitativoAsesorNoEncontradoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCuantitativoAsesor.ITEM_NO_ENCONTRADO));
    }
}
