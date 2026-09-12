package com.arquisoft.evaluaciones.domain.evaluacion;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class InicioEvaluacionDomain {

    private UUID evaluacion;
    private EstadoEvaluacion estadoActual;

    private InicioEvaluacionDomain() {}

    public static InicioEvaluacionDomain crear(UUID evaluacion, EstadoEvaluacion estadoActual) {
        var accion = new InicioEvaluacionDomain();
        var result = new ValidationResult();

        accion.setEvaluacion(evaluacion, result);
        accion.setEstadoActual(estadoActual, result);

        result.lanzarSiTieneErrores();
        return accion;
    }

    private void setEvaluacion(UUID evaluacion, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacion,
                EvaluacionesFields.Evaluacion.EVALUACION,
                EvaluacionesCodes.Evaluacion.EVALUACION_REQUERIDO, result)) {
            return;
        }
        this.evaluacion = evaluacion;
    }

    private void setEstadoActual(EstadoEvaluacion estadoActual, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(estadoActual,
                EvaluacionesFields.Evaluacion.ESTADO,
                EvaluacionesCodes.Evaluacion.ESTADO_REQUERIDO, result)) {
            return;
        }
        this.estadoActual = estadoActual;
    }

    public UUID getEvaluacion() {
        return evaluacion;
    }

    public EstadoEvaluacion getEstadoActual() {
        return estadoActual;
    }

    public EstadoEvaluacion estadoDestino() {
        return EstadoEvaluacion.EN_PROGRESO;
    }
}
