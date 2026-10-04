package com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class RemoverObservacionEvaluacionCommandTest {

    @Test
    void debeCrearCommand_cuandoDatosValidos() {
        // Arrange
        var observacionEvaluacion = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();

        // Act
        var command = RemoverObservacionEvaluacionCommand.crear(observacionEvaluacion, representanteComite);

        // Assert
        assertThat(command.observacionEvaluacion()).isEqualTo(observacionEvaluacion);
        assertThat(command.representanteComite()).isEqualTo(representanteComite);
    }

    @Test
    void debeAcumularErroresDeEntrada_cuandoCamposNulos() {
        // Act
        var excepcion = catchThrowableOfType(ApplicationValidationException.class,
                () -> RemoverObservacionEvaluacionCommand.crear(null, null));

        // Assert
        var errores = excepcion.getValidationResult().getErrores();
        assertThat(errores).extracting("codigoError").containsExactlyInAnyOrder(
                FichasCodes.ObservacionEvaluacion.OBSERVACION_EVALUACION_REQUERIDA,
                FichasCodes.ObservacionEvaluacion.REPRESENTANTE_COMITE_REQUERIDO);
        assertThat(errores).extracting("campo").containsExactlyInAnyOrder(
                FichasFields.ObservacionEvaluacion.OBSERVACION_EVALUACION,
                FichasFields.ObservacionEvaluacion.REPRESENTANTE_COMITE);
    }
}
