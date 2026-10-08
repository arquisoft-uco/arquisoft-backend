package com.arquisoft.fichas.domain.observacionevaluacion;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class ObservacionEvaluacionDomainTest {

    @Test
    void debeCrearObservacionRecortada_cuandoDatosValidos() {
        // Arrange
        var evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();

        // Act
        var observacionEvaluacion = ObservacionEvaluacionDomain.crear(
                evaluacionFichaPerfil, "  El marco teórico es insuficiente  ");

        // Assert
        assertThat(observacionEvaluacion.getId()).isNotNull();
        assertThat(observacionEvaluacion.getEvaluacionFichaPerfil()).isEqualTo(evaluacionFichaPerfil);
        assertThat(observacionEvaluacion.getObservacion()).isEqualTo("El marco teórico es insuficiente");
    }

    @Test
    void debeAcumularErrores_cuandoEvaluacionNulaYObservacionEnBlanco() {
        // Act
        var excepcion = catchThrowableOfType(DomainValidationException.class,
                () -> ObservacionEvaluacionDomain.crear(null, "   "));

        // Assert
        var errores = excepcion.getValidationResult().getErrores();
        assertThat(errores).extracting("codigoError").containsExactlyInAnyOrder(
                FichasCodes.ObservacionEvaluacion.EVALUACION_FICHA_PERFIL_REQUERIDA,
                FichasCodes.ObservacionEvaluacion.OBSERVACION_REQUERIDA);
        assertThat(errores).extracting("campo").containsExactlyInAnyOrder(
                FichasFields.ObservacionEvaluacion.EVALUACION_FICHA_PERFIL,
                FichasFields.ObservacionEvaluacion.OBSERVACION);
    }

    @Test
    void debeRechazar_cuandoObservacionSuperaMaximo() {
        // Arrange
        var observacionDemasiadoLarga = "x".repeat(FichasLimits.ObservacionEvaluacion.OBSERVACION_MAX + 1);

        // Act
        var excepcion = catchThrowableOfType(DomainValidationException.class,
                () -> ObservacionEvaluacionDomain.crear(UtilUUID.generarNuevoUUID(), observacionDemasiadoLarga));

        // Assert
        assertThat(excepcion.getValidationResult().getErrores()).extracting("codigoError")
                .containsExactly(FichasCodes.ObservacionEvaluacion.OBSERVACION_DEMASIADO_LARGA);
    }

    @Test
    void debeAceptarElMaximo_cuandoLosEspaciosLoSuperanAntesDelTrim() {
        // Arrange — la longitud se mide sobre el texto recortado, no sobre el crudo
        var observacionEnElBorde = "x".repeat(FichasLimits.ObservacionEvaluacion.OBSERVACION_MAX);

        // Act
        var observacionEvaluacion = ObservacionEvaluacionDomain.crear(
                UtilUUID.generarNuevoUUID(), "   " + observacionEnElBorde + "   ");

        // Assert
        assertThat(observacionEvaluacion.getObservacion()).isEqualTo(observacionEnElBorde);
    }
}
