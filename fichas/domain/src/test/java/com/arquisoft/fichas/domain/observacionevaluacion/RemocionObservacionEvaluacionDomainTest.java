package com.arquisoft.fichas.domain.observacionevaluacion;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class RemocionObservacionEvaluacionDomainTest {

    @Test
    void debeCrearRemocion_cuandoDatosValidos() {
        // Arrange
        var observacionEvaluacion = UtilUUID.generarNuevoUUID();
        var representanteComite = UtilUUID.generarNuevoUUID();

        // Act
        var remocion = RemocionObservacionEvaluacionDomain.crear(observacionEvaluacion, representanteComite);

        // Assert
        assertThat(remocion.getObservacionEvaluacion()).isEqualTo(observacionEvaluacion);
        assertThat(remocion.getRepresentanteComite()).isEqualTo(representanteComite);
    }

    @Test
    void debeAcumularErroresDeAmbosCampos_cuandoTodoNulo() {
        // Act
        var excepcion = catchThrowableOfType(DomainValidationException.class,
                () -> RemocionObservacionEvaluacionDomain.crear(null, null));

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
