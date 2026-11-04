package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RemoverItemCualitativoJuradoCommandTest {

    @Test
    void debeCrearCommand_cuandoItemEsValido() {
        // Arrange
        var itemCualitativoJurado = UUID.randomUUID();

        // Act
        var command = RemoverItemCualitativoJuradoCommand.crear(itemCualitativoJurado);

        // Assert
        assertThat(command.itemCualitativoJurado()).isEqualTo(itemCualitativoJurado);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoItemEsNulo() {
        // Arrange
        UUID itemCualitativoJurado = null;

        // Act & Assert
        assertThatThrownBy(() -> RemoverItemCualitativoJuradoCommand.crear(itemCualitativoJurado))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple(
                                        EvaluacionesFields.ItemCualitativoJurado.ITEM,
                                        EvaluacionesCodes.ItemCualitativoJurado.ITEM_ID_REQUERIDO)));
    }
}
