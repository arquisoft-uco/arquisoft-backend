package com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverObservacionItemAsesorCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var command = RemoverObservacionItemAsesorCommand.crear(observacion, asesor);

        // Assert
        assertThat(command.observacion()).isEqualTo(observacion);
        assertThat(command.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeFallar_cuandoObservacionYAsesorSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> RemoverObservacionItemAsesorCommand.crear(null, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.ObservacionItemAsesor.ID_REQUERIDO,
                                        EvaluacionesCodes.ObservacionItemAsesor.ASESOR_REQUERIDO));
    }
}
