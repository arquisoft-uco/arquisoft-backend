package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RegistrarItemCualitativoJuradoCommandTest {

    @Test
    void debeCrearCommandNormalizado_cuandoDatosValidos() {
        // Arrange
        String nombre = "  Claridad  ";
        String descripcion = "  Evalúa la claridad conceptual  ";

        // Act
        RegistrarItemCualitativoJuradoCommand command =
                RegistrarItemCualitativoJuradoCommand.crear(nombre, descripcion);

        // Assert
        assertThat(command.nombre()).isEqualTo("Claridad");
        assertThat(command.descripcion()).isEqualTo("Evalúa la claridad conceptual");
    }

    @Test
    void debeAcumularErroresDeEntrada_cuandoDatosInvalidos() {
        // Arrange
        String nombre = " ";
        String descripcion = "d".repeat(301);

        // Act & Assert
        assertThatThrownBy(() -> RegistrarItemCualitativoJuradoCommand.crear(
                nombre, descripcion))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception -> {
                    assertThat(exception.getValidationResult().getErrores())
                            .extracting(
                                    error -> error.campo(),
                                    error -> error.codigoError())
                            .containsExactlyInAnyOrder(
                                    tuple(
                                            EvaluacionesFields.ItemCualitativoJurado.NOMBRE,
                                            EvaluacionesCodes.ItemCualitativoJurado.NOMBRE_REQUERIDO),
                                    tuple(
                                            EvaluacionesFields.ItemCualitativoJurado.DESCRIPCION,
                                            EvaluacionesCodes.ItemCualitativoJurado
                                                    .DESCRIPCION_DEMASIADO_LARGA));
                });
    }
}
