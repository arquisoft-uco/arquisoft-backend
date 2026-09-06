package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class ModificarItemCualitativoJuradoCommandTest {

    @Test
    void debeCrearComandoNormalizado_cuandoDatosValidos() {
        // Arrange
        UUID itemCualitativoJurado = UUID.randomUUID();
        String descripcion = "  Evalúa la claridad de la exposición  ";

        // Act
        ModificarItemCualitativoJuradoCommand command =
                ModificarItemCualitativoJuradoCommand.crear(itemCualitativoJurado, descripcion);

        // Assert
        assertThat(command.itemCualitativoJurado()).isEqualTo(itemCualitativoJurado);
        assertThat(command.descripcion()).isEqualTo("Evalúa la claridad de la exposición");
    }

    @Test
    void debeAcumularErrorDeEntrada_cuandoDescripcionEstaEnBlanco() {
        // Arrange
        UUID itemCualitativoJurado = UUID.randomUUID();
        String descripcion = "   ";

        // Act & Assert
        assertThatThrownBy(() -> ModificarItemCualitativoJuradoCommand.crear(
                itemCualitativoJurado, descripcion))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple(
                                        EvaluacionesFields.ItemCualitativoJurado.DESCRIPCION,
                                        EvaluacionesCodes.ItemCualitativoJurado.DESCRIPCION_REQUERIDA)));
    }

    @Test
    void debeAcumularErrorDeEntrada_cuandoDescripcionSuperaLongitudMaxima() {
        // Arrange
        UUID itemCualitativoJurado = UUID.randomUUID();
        String descripcion = "d".repeat(301);

        // Act & Assert
        assertThatThrownBy(() -> ModificarItemCualitativoJuradoCommand.crear(
                itemCualitativoJurado, descripcion))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple(
                                        EvaluacionesFields.ItemCualitativoJurado.DESCRIPCION,
                                        EvaluacionesCodes.ItemCualitativoJurado
                                                .DESCRIPCION_DEMASIADO_LARGA)));
    }
}
