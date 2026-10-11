package com.arquisoft.fichas.domain.observacionevaluacion;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class AgregacionObservacionEvaluacionDomainTest {

    @Test
    void debeConstruirAgregacion_cuandoDatosValidos() {
        // Arrange
        var evaluacionFichaPerfil = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();
        var observacionEvaluacion = ObservacionEvaluacionDomain.crear(evaluacionFichaPerfil, "Observación válida");

        // Act
        var agregacion = AgregacionObservacionEvaluacionDomain.crear(observacionEvaluacion, representanteComite);

        // Assert
        assertThat(agregacion.getObservacionEvaluacion()).isSameAs(observacionEvaluacion);
        assertThat(agregacion.getEvaluacionFichaPerfil()).isEqualTo(evaluacionFichaPerfil);
        assertThat(agregacion.getObservacion()).isEqualTo("Observación válida");
        assertThat(agregacion.getRepresentanteComite()).isEqualTo(representanteComite);
    }

    @Test
    void debeAcumularErrores_cuandoObservacionYRepresentanteNulos() {
        // Act
        var excepcion = catchThrowableOfType(DomainValidationException.class,
                () -> AgregacionObservacionEvaluacionDomain.crear(null, null));

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
