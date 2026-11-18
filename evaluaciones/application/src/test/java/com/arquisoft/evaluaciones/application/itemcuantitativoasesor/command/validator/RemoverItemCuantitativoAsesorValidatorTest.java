package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.validator.impl.RemoverItemCuantitativoAsesorValidatorImpl;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception.ItemCuantitativoAsesorNoEncontradoException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverItemCuantitativoAsesorValidatorTest {

    private final RemoverItemCuantitativoAsesorValidatorImpl validator =
            new RemoverItemCuantitativoAsesorValidatorImpl();

    @Test
    void debeNoLanzar_cuandoItemExiste() {
        // Arrange
        var itemCuantitativoAsesor = UUID.randomUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(itemCuantitativoAsesor, true))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoItemNoExiste() {
        // Arrange
        var itemCuantitativoAsesor = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(itemCuantitativoAsesor, false))
                .isInstanceOfSatisfying(
                        ItemCuantitativoAsesorNoEncontradoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCuantitativoAsesor.ITEM_NO_ENCONTRADO));
    }
}
