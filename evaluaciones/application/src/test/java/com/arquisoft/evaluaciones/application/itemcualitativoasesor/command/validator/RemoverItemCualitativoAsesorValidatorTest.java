package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.validator.impl.RemoverItemCualitativoAsesorValidatorImpl;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.exception.ItemCualitativoAsesorNoEncontradoException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverItemCualitativoAsesorValidatorTest {

    private final RemoverItemCualitativoAsesorValidatorImpl validator =
            new RemoverItemCualitativoAsesorValidatorImpl();

    @Test
    void debeNoLanzar_cuandoItemExiste() {
        // Arrange
        var itemCualitativoAsesor = UUID.randomUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(itemCualitativoAsesor, true))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoItemNoExiste() {
        // Arrange
        var itemCualitativoAsesor = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(itemCualitativoAsesor, false))
                .isInstanceOfSatisfying(
                        ItemCualitativoAsesorNoEncontradoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCualitativoAsesor.ITEM_NO_ENCONTRADO));
    }
}
