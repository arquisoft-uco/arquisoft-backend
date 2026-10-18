package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RemoverItemCuantitativoJuradoCommandTest {

    @Test
    void debeCrearCommand_cuandoItemEsValido() {
        // Arrange
        var itemCuantitativoJurado = UUID.randomUUID();

        // Act
        var command = RemoverItemCuantitativoJuradoCommand.crear(itemCuantitativoJurado);

        // Assert
        assertThat(command.itemCuantitativoJurado()).isEqualTo(itemCuantitativoJurado);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoItemEsNulo() {
        // Arrange
        UUID itemCuantitativoJurado = null;

        // Act & Assert
        assertThatThrownBy(() -> RemoverItemCuantitativoJuradoCommand.crear(itemCuantitativoJurado))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple(
                                        EvaluacionesFields.ItemCuantitativoJurado.ITEM,
                                        EvaluacionesCodes.ItemCuantitativoJurado.ITEM_ID_REQUERIDO)));
    }
}
