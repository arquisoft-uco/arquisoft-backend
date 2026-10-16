package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ModificarItemCuantitativoJuradoCommandTest {

    @Test
    void debeCrearComandoNormalizado_cuandoDatosValidos() {
        // Arrange
        var itemCuantitativoJurado = UUID.randomUUID();
        var descripcion = "  Evalúa la calidad técnica de la exposición  ";

        // Act
        var command = ModificarItemCuantitativoJuradoCommand.crear(
                itemCuantitativoJurado, descripcion);

        // Assert
        assertThat(command.itemCuantitativoJurado()).isEqualTo(itemCuantitativoJurado);
        assertThat(command.descripcion()).isEqualTo("Evalúa la calidad técnica de la exposición");
    }

    @Test
    void debeAcumularErrorDeEntrada_cuandoDescripcionEstaEnBlanco() {
        // Arrange
        var itemCuantitativoJurado = UUID.randomUUID();
        var descripcion = "   ";

        // Act & Assert
        assertThatThrownBy(() -> ModificarItemCuantitativoJuradoCommand.crear(
                itemCuantitativoJurado, descripcion))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple(
                                        EvaluacionesFields.ItemCuantitativoJurado.DESCRIPCION,
                                        EvaluacionesCodes.ItemCuantitativoJurado
                                                .DESCRIPCION_REQUERIDA)));
    }

    @Test
    void debeAcumularErrorDeEntrada_cuandoDescripcionSuperaLongitudMaxima() {
        // Arrange
        var itemCuantitativoJurado = UUID.randomUUID();
        var descripcion = "d".repeat(301);

        // Act & Assert
        assertThatThrownBy(() -> ModificarItemCuantitativoJuradoCommand.crear(
                itemCuantitativoJurado, descripcion))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple(
                                        EvaluacionesFields.ItemCuantitativoJurado.DESCRIPCION,
                                        EvaluacionesCodes.ItemCuantitativoJurado
                                                .DESCRIPCION_DEMASIADO_LARGA)));
    }
}
