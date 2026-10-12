package com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class AgregarObservacionEvaluacionCommandTest {

    @Test
    void debeCrearCommandRecortado_cuandoDatosValidos() {
        // Arrange
        var evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();

        // Act
        var command = AgregarObservacionEvaluacionCommand.crear(
                evaluacionFichaPerfil, "  Observación válida  ", representanteComite);

        // Assert
        assertThat(command.evaluacionFichaPerfil()).isEqualTo(evaluacionFichaPerfil);
        assertThat(command.observacion()).isEqualTo("Observación válida");
        assertThat(command.representanteComite()).isEqualTo(representanteComite);
    }

    @Test
    void debeAcumularLosTresErrores_cuandoTodosLosCamposSonInvalidos() {
        // Act
        var excepcion = catchThrowableOfType(ApplicationValidationException.class,
                () -> AgregarObservacionEvaluacionCommand.crear(null, "   ", null));

        // Assert
        var errores = excepcion.getValidationResult().getErrores();
        assertThat(errores).extracting("codigoError").containsExactlyInAnyOrder(
                FichasCodes.ObservacionEvaluacion.EVALUACION_FICHA_PERFIL_REQUERIDA,
                FichasCodes.ObservacionEvaluacion.OBSERVACION_REQUERIDA,
                FichasCodes.ObservacionEvaluacion.REPRESENTANTE_COMITE_REQUERIDO);
        assertThat(errores).extracting("campo").containsExactlyInAnyOrder(
                FichasFields.ObservacionEvaluacion.EVALUACION_FICHA_PERFIL,
                FichasFields.ObservacionEvaluacion.OBSERVACION,
                FichasFields.ObservacionEvaluacion.REPRESENTANTE_COMITE);
    }

    @Test
    void debeRechazar_cuandoObservacionSuperaMaximo() {
        // Arrange
        var observacionDemasiadoLarga = "x".repeat(FichasLimits.ObservacionEvaluacion.OBSERVACION_MAX + 1);

        // Act
        var excepcion = catchThrowableOfType(ApplicationValidationException.class,
                () -> AgregarObservacionEvaluacionCommand.crear(
                        UtilUUID.generarNuevoUUID(), observacionDemasiadoLarga, UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(excepcion.getValidationResult().getErrores()).extracting("codigoError")
                .containsExactly(FichasCodes.ObservacionEvaluacion.OBSERVACION_DEMASIADO_LARGA);
    }
}
