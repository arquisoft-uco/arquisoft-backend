package com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarObservacionItemAsesorCommandTest {

    @Test
    void debeCrearYRecortarDescripcion_cuandoDatosValidos() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var command = ModificarObservacionItemAsesorCommand.crear(observacion, asesor, "  Nuevo sustento  ");

        // Assert
        assertThat(command.observacion()).isEqualTo(observacion);
        assertThat(command.asesor()).isEqualTo(asesor);
        assertThat(command.descripcion()).isEqualTo("Nuevo sustento");
    }

    @Test
    void debeFallar_cuandoCamposInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> ModificarObservacionItemAsesorCommand.crear(null, null, " "))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.ObservacionItemAsesor.ID_REQUERIDO,
                                        EvaluacionesCodes.ObservacionItemAsesor.ASESOR_REQUERIDO,
                                        EvaluacionesCodes.ObservacionItemAsesor.DESCRIPCION_REQUERIDA));
    }

    @Test
    void debeFallar_cuandoDescripcionExcede500() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var descripcionLarga = "a".repeat(501);

        // Act & Assert
        assertThatThrownBy(() -> ModificarObservacionItemAsesorCommand.crear(observacion, asesor, descripcionLarga))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.ObservacionItemAsesor.DESCRIPCION_DEMASIADO_LARGA));
    }
}
