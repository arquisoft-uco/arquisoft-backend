package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarItemCuantitativoJuradoCommandTest {

    @Test
    void debeCrearYNormalizarCommandValido() {
        // Arrange
        UUID categoria = UUID.randomUUID();

        // Act
        RegistrarItemCuantitativoJuradoCommand command =
                RegistrarItemCuantitativoJuradoCommand.crear(
                        "  Calidad  ", "  Descripción  ", categoria.toString(), 100);

        // Assert
        assertThat(command.nombre()).isEqualTo("Calidad");
        assertThat(command.descripcion()).isEqualTo("Descripción");
        assertThat(command.categoria()).isEqualTo(categoria);
        assertThat(command.valor()).isEqualTo(100);
    }

    @Test
    void debeAcumularErroresDeEntrada() {
        // Act & Assert
        assertThatThrownBy(() -> RegistrarItemCuantitativoJuradoCommand.crear(
                " ", null, null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.ItemCuantitativoJurado.NOMBRE_REQUERIDO,
                                        EvaluacionesCodes.ItemCuantitativoJurado.DESCRIPCION_REQUERIDA,
                                        EvaluacionesCodes.ItemCuantitativoJurado.CATEGORIA_REQUERIDA,
                                        EvaluacionesCodes.ItemCuantitativoJurado.VALOR_REQUERIDO));
    }

    @Test
    void debeRechazarLongitudesYValorFueraDeRango() {
        // Act & Assert
        assertThatThrownBy(() -> RegistrarItemCuantitativoJuradoCommand.crear(
                "n".repeat(101), "d".repeat(301), UUID.randomUUID().toString(), -1))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.ItemCuantitativoJurado.NOMBRE_DEMASIADO_LARGO,
                                        EvaluacionesCodes.ItemCuantitativoJurado.DESCRIPCION_DEMASIADO_LARGA,
                                        EvaluacionesCodes.ItemCuantitativoJurado.VALOR_FUERA_DE_RANGO));
    }

    @Test
    void debeRechazarCategoriaConFormatoInvalido() {
        // Act & Assert
        assertThatThrownBy(() -> RegistrarItemCuantitativoJuradoCommand.crear(
                "Calidad", "Descripción", "no-es-un-uuid", 100))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(
                                        EvaluacionesCodes.ItemCuantitativoJurado.CATEGORIA_REQUERIDA));
    }
}
