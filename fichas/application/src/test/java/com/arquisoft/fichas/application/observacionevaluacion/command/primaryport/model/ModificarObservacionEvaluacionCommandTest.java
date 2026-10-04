package com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class ModificarObservacionEvaluacionCommandTest {

    @Test
    void debeCrearCommandRecortado_cuandoDatosValidos() {
        // Arrange
        var observacionEvaluacion = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();

        // Act
        var command = ModificarObservacionEvaluacionCommand.crear(
                observacionEvaluacion, "  Nuevo texto de la observación  ", representanteComite);

        // Assert
        assertThat(command.observacionEvaluacion()).isEqualTo(observacionEvaluacion);
        assertThat(command.observacion()).isEqualTo("Nuevo texto de la observación");
        assertThat(command.representanteComite()).isEqualTo(representanteComite);
    }

    @Test
    void debeAcumularLosTresErrores_cuandoTodosLosCamposSonInvalidos() {
        // Act
        var excepcion = catchThrowableOfType(ApplicationValidationException.class,
                () -> ModificarObservacionEvaluacionCommand.crear(null, "   ", null));

        // Assert
        var errores = excepcion.getValidationResult().getErrores();
        assertThat(errores).extracting("codigoError").containsExactlyInAnyOrder(
                FichasCodes.ObservacionEvaluacion.OBSERVACION_EVALUACION_REQUERIDA,
                FichasCodes.ObservacionEvaluacion.OBSERVACION_REQUERIDA,
                FichasCodes.ObservacionEvaluacion.REPRESENTANTE_COMITE_REQUERIDO);
        assertThat(errores).extracting("campo").containsExactlyInAnyOrder(
                FichasFields.ObservacionEvaluacion.OBSERVACION_EVALUACION,
                FichasFields.ObservacionEvaluacion.OBSERVACION,
                FichasFields.ObservacionEvaluacion.REPRESENTANTE_COMITE);
    }

    @Test
    void debeRechazar_cuandoObservacionSuperaMaximo() {
        // Arrange
        var observacionDemasiadoLarga = "x".repeat(FichasLimits.ObservacionEvaluacion.OBSERVACION_MAX + 1);

        // Act
        var excepcion = catchThrowableOfType(ApplicationValidationException.class,
                () -> ModificarObservacionEvaluacionCommand.crear(
                        UtilUUID.generarNuevoUUID(), observacionDemasiadoLarga, UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(excepcion.getValidationResult().getErrores()).extracting("codigoError")
                .containsExactly(FichasCodes.ObservacionEvaluacion.OBSERVACION_DEMASIADO_LARGA);
    }
}
