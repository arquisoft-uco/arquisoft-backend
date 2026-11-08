package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RegistrarItemCualitativoAsesorCommandTest {

    @Test
    void debeCrearRecortado_cuandoDatosValidos() {
        // Arrange
        var nombre = "  Claridad  ";
        var descripcion = "  Evalúa la claridad  ";

        // Act
        var command = RegistrarItemCualitativoAsesorCommand.crear(nombre, descripcion);

        // Assert
        assertThat(command.nombre()).isEqualTo("Claridad");
        assertThat(command.descripcion()).isEqualTo("Evalúa la claridad");
    }

    @Test
    void debeAcumularErrores_cuandoNombreVacioYDescripcionSuperaLimite() {
        // Arrange
        var nombre = " ";
        var descripcion = "d".repeat(301);

        // Act & Assert
        assertThatThrownBy(() -> RegistrarItemCualitativoAsesorCommand.crear(nombre, descripcion))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.ItemCualitativoAsesor.NOMBRE,
                                                EvaluacionesCodes.ItemCualitativoAsesor.NOMBRE_REQUERIDO),
                                        tuple(EvaluacionesFields.ItemCualitativoAsesor.DESCRIPCION,
                                                EvaluacionesCodes.ItemCualitativoAsesor.DESCRIPCION_DEMASIADO_LARGA)));
    }

    @Test
    void debeRechazar_cuandoNombreSuperaLimiteYDescripcionNula() {
        // Arrange
        var nombre = "n".repeat(101);
        String descripcion = null;

        // Act & Assert
        assertThatThrownBy(() -> RegistrarItemCualitativoAsesorCommand.crear(nombre, descripcion))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.ItemCualitativoAsesor.NOMBRE_DEMASIADO_LARGO,
                                        EvaluacionesCodes.ItemCualitativoAsesor.DESCRIPCION_REQUERIDA));
    }
}
