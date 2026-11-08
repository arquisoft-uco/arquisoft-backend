package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator.impl.ModificarItemCualitativoAsesorValidatorImpl;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.exception.ItemCualitativoAsesorNoEncontradoException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarItemCualitativoAsesorValidatorTest {

    private final ModificarItemCualitativoAsesorValidatorImpl validator =
            new ModificarItemCualitativoAsesorValidatorImpl();

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
                .isInstanceOfSatisfying(ItemCualitativoAsesorNoEncontradoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCualitativoAsesor.ITEM_NO_ENCONTRADO));
    }
}
