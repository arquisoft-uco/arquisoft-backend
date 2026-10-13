package com.arquisoft.fichas.domain.observacionevaluacion;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class ModificacionObservacionEvaluacionDomainTest {

    @Test
    void debeCrearModificacionRecortada_cuandoDatosValidos() {
        // Arrange
        var observacionEvaluacion = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();

        // Act
        var modificacion = ModificacionObservacionEvaluacionDomain.crear(
                observacionEvaluacion, "  Nuevo texto de la observación  ", representanteComite);

        // Assert
        assertThat(modificacion.getObservacionEvaluacion()).isEqualTo(observacionEvaluacion);
        assertThat(modificacion.getObservacion()).isEqualTo("Nuevo texto de la observación");
        assertThat(modificacion.getRepresentanteComite()).isEqualTo(representanteComite);
    }

    @Test
    void debeAcumularLosTresErrores_cuandoTodosLosCamposSonInvalidos() {
        // Act
        var excepcion = catchThrowableOfType(DomainValidationException.class,
                () -> ModificacionObservacionEvaluacionDomain.crear(null, "   ", null));

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
        var excepcion = catchThrowableOfType(DomainValidationException.class,
                () -> ModificacionObservacionEvaluacionDomain.crear(
                        UtilUUID.generarNuevoUUID(), observacionDemasiadoLarga, UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(excepcion.getValidationResult().getErrores()).extracting("codigoError")
                .containsExactly(FichasCodes.ObservacionEvaluacion.OBSERVACION_DEMASIADO_LARGA);
    }

    @Test
    void debeAceptarElMaximo_cuandoLosEspaciosSobranSoloAntesDeRecortar() {
        // Arrange — mide la longitud del texto recortado, no la del crudo
        var observacionEnElLimite = "x".repeat(FichasLimits.ObservacionEvaluacion.OBSERVACION_MAX);

        // Act
        var modificacion = ModificacionObservacionEvaluacionDomain.crear(
                UtilUUID.generarNuevoUUID(), " " + observacionEnElLimite + " ", UtilUUID.generarNuevoUUID());

        // Assert
        assertThat(modificacion.getObservacion()).isEqualTo(observacionEnElLimite);
    }
}
