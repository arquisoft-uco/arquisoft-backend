package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RemoverItemCuantitativoAsesorCommandTest {

    @Test
    void debeCrearCommand_cuandoItemEsValido() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();

        // Act
        var command = RemoverItemCuantitativoAsesorCommand.crear(item);

        // Assert
        assertThat(command.itemCuantitativoAsesor()).isEqualTo(item);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoItemEsNulo() {
        // Arrange
        UUID item = null;

        // Act & Assert
        assertThatThrownBy(() -> RemoverItemCuantitativoAsesorCommand.crear(item))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple(
                                        EvaluacionesFields.ItemCuantitativoAsesor.ITEM,
                                        EvaluacionesCodes.ItemCuantitativoAsesor.ITEM_ID_REQUERIDO)));
    }
}
