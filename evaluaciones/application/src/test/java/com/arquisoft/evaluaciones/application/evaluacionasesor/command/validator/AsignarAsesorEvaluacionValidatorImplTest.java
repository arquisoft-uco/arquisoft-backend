package com.arquisoft.evaluaciones.application.evaluacionasesor.command.validator;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.validator.impl.AsignarAsesorEvaluacionValidatorImpl;
import com.arquisoft.evaluaciones.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.model.EstadoAsignacionEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ExistenciaEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.model.CambioAsesorEvaluacion;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignarAsesorEvaluacionValidatorImplTest {

    private final AsignarAsesorEvaluacionValidatorImpl validator = new AsignarAsesorEvaluacionValidatorImpl();

    private final UUID evaluacion = UtilUUID.generarNuevoUUID();
    private final UUID actual = UtilUUID.generarNuevoUUID();
    private final UUID nuevo = UtilUUID.generarNuevoUUID();

    @Test
    void debePasar_cuandoTodoValido() {
        // Act & Assert
        assertThatCode(() -> validator.validar(
                new ExistenciaEvaluacion(evaluacion, true),
                new ExistenciaAsesor(nuevo, true),
                new EstadoAsignacionEvaluacion(EstadoEvaluacion.PENDIENTE),
                new CambioAsesorEvaluacion(evaluacion, actual, nuevo, false))).doesNotThrowAnyException();
    }

    @Test
    void debeGanarEvaluacionNoEncontrada_cuandoTodoFalla() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                new ExistenciaEvaluacion(evaluacion, false),
                new ExistenciaAsesor(nuevo, false),
                new EstadoAsignacionEvaluacion(EstadoEvaluacion.FINALIZADA),
                new CambioAsesorEvaluacion(evaluacion, actual, nuevo, true)))
                .isInstanceOfSatisfying(DomainException.class, ex ->
                        assertThat(ex.getCodigoError()).isEqualTo(EvaluacionesCodes.Evaluacion.NO_ENCONTRADA));
    }

    @Test
    void debeGanarAsesorNoEncontrado_cuandoFallanAsesorFinalizadaYRegistros() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                new ExistenciaEvaluacion(evaluacion, true),
                new ExistenciaAsesor(nuevo, false),
                new EstadoAsignacionEvaluacion(EstadoEvaluacion.FINALIZADA),
                new CambioAsesorEvaluacion(evaluacion, actual, nuevo, true)))
                .isInstanceOfSatisfying(DomainException.class, ex ->
                        assertThat(ex.getCodigoError()).isEqualTo(EvaluacionesCodes.Asesor.NO_ENCONTRADO));
    }

    @Test
    void debeGanarFinalizada_cuandoFallanFinalizadaYRegistros() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                new ExistenciaEvaluacion(evaluacion, true),
                new ExistenciaAsesor(nuevo, true),
                new EstadoAsignacionEvaluacion(EstadoEvaluacion.FINALIZADA),
                new CambioAsesorEvaluacion(evaluacion, actual, nuevo, true)))
                .isInstanceOfSatisfying(DomainException.class, ex ->
                        assertThat(ex.getCodigoError()).isEqualTo(EvaluacionesCodes.Evaluacion.ASIGNACION_FINALIZADA));
    }

    @Test
    void debeLanzarConEvaluaciones_cuandoDistintoYConRegistros() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(
                new ExistenciaEvaluacion(evaluacion, true),
                new ExistenciaAsesor(nuevo, true),
                new EstadoAsignacionEvaluacion(EstadoEvaluacion.EN_PROGRESO),
                new CambioAsesorEvaluacion(evaluacion, actual, nuevo, true)))
                .isInstanceOfSatisfying(DomainException.class, ex ->
                        assertThat(ex.getCodigoError()).isEqualTo(EvaluacionesCodes.EvaluacionAsesor.CON_EVALUACIONES));
    }
}
