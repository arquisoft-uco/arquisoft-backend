package com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarObservacionItemAsesorCommandTest {

    @Test
    void debeCrearYRecortarDescripcion_cuandoDatosValidos() {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var command = RegistrarObservacionItemAsesorCommand.crear(evaluacion, asesor, "  Buen sustento  ");

        // Assert
        assertThat(command.evaluacionCuantitativaAsesor()).isEqualTo(evaluacion);
        assertThat(command.asesor()).isEqualTo(asesor);
        assertThat(command.descripcion()).isEqualTo("Buen sustento");
    }

    @Test
    void debeFallar_cuandoCamposInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> RegistrarObservacionItemAsesorCommand.crear(null, null, " "))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.ObservacionItemAsesor.EVALUACION_CUANTITATIVA_ASESOR_REQUERIDA,
                                        EvaluacionesCodes.ObservacionItemAsesor.ASESOR_REQUERIDO,
                                        EvaluacionesCodes.ObservacionItemAsesor.DESCRIPCION_REQUERIDA));
    }

    @Test
    void debeFallar_cuandoDescripcionExcede500() {
        // Arrange
        var descripcionLarga = "a".repeat(501);
        var evaluacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> RegistrarObservacionItemAsesorCommand.crear(evaluacion, asesor, descripcionLarga))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.ObservacionItemAsesor.DESCRIPCION_DEMASIADO_LARGA));
    }
}
