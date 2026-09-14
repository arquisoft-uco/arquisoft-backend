package com.arquisoft.evaluaciones.domain.evaluacion;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class InicioEvaluacionDomainTest {

    @Test
    void debeCrearAccion_cuandoDatosValidos() {
        // Arrange
        UUID evaluacion = UUID.randomUUID();

        // Act
        InicioEvaluacionDomain accion = InicioEvaluacionDomain.crear(evaluacion, EstadoEvaluacion.PENDIENTE);

        // Assert
        assertThat(accion.getEvaluacion()).isEqualTo(evaluacion);
        assertThat(accion.getEstadoActual()).isEqualTo(EstadoEvaluacion.PENDIENTE);
    }

    @Test
    void debeAcumularErrores_cuandoEvaluacionYEstadoSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> InicioEvaluacionDomain.crear(null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.Evaluacion.EVALUACION,
                                                EvaluacionesCodes.Evaluacion.EVALUACION_REQUERIDO),
                                        tuple(EvaluacionesFields.Evaluacion.ESTADO,
                                                EvaluacionesCodes.Evaluacion.ESTADO_REQUERIDO)));
    }

    @Test
    void debeDecidirEnProgreso_cuandoElEstadoActualEsPendienteOEnProgreso() {
        // Arrange
        InicioEvaluacionDomain pendiente = InicioEvaluacionDomain.crear(UUID.randomUUID(), EstadoEvaluacion.PENDIENTE);
        InicioEvaluacionDomain enProgreso = InicioEvaluacionDomain.crear(UUID.randomUUID(), EstadoEvaluacion.EN_PROGRESO);

        // Act & Assert
        assertThat(pendiente.estadoDestino()).isEqualTo(EstadoEvaluacion.EN_PROGRESO);
        assertThat(enProgreso.estadoDestino()).isEqualTo(EstadoEvaluacion.EN_PROGRESO);
    }
}
