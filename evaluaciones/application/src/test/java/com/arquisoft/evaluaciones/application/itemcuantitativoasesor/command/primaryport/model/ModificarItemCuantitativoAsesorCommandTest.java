package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ModificarItemCuantitativoAsesorCommandTest {

    @Test
    void debeCrearComandoRecortado_cuandoDatosValidos() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();

        // Act
        var command = ModificarItemCuantitativoAsesorCommand.crear(item, "  Descripción ampliada  ");

        // Assert
        assertThat(command.itemCuantitativoAsesor()).isEqualTo(item);
        assertThat(command.descripcion()).isEqualTo("Descripción ampliada");
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoItemEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> ModificarItemCuantitativoAsesorCommand.crear(null, "Descripción"))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple(
                                        EvaluacionesFields.ItemCuantitativoAsesor.ITEM,
                                        EvaluacionesCodes.ItemCuantitativoAsesor.ITEM_ID_REQUERIDO)));
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoDescripcionEstaEnBlanco() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> ModificarItemCuantitativoAsesorCommand.crear(item, "   "))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple(
                                        EvaluacionesFields.ItemCuantitativoAsesor.DESCRIPCION,
                                        EvaluacionesCodes.ItemCuantitativoAsesor.DESCRIPCION_REQUERIDA)));
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoDescripcionSuperaLongitudMaxima() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> ModificarItemCuantitativoAsesorCommand.crear(item, "d".repeat(301)))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple(
                                        EvaluacionesFields.ItemCuantitativoAsesor.DESCRIPCION,
                                        EvaluacionesCodes.ItemCuantitativoAsesor.DESCRIPCION_DEMASIADO_LARGA)));
    }
}
