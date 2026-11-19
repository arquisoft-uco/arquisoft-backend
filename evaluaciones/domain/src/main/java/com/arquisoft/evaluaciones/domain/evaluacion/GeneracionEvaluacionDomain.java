package com.arquisoft.evaluaciones.domain.evaluacion;

import com.arquisoft.evaluaciones.domain.entregable.EntregableDomain;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.EvaluacionAsesorDomain;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

public final class GeneracionEvaluacionDomain {

    private EntregableDomain entregable;
    private EvaluacionDomain evaluacion;
    private EvaluacionAsesorDomain evaluacionAsesor;

    private GeneracionEvaluacionDomain() {}

    public static GeneracionEvaluacionDomain crear(
            EntregableDomain entregable, EvaluacionDomain evaluacion, EvaluacionAsesorDomain evaluacionAsesor) {
        var generacion = new GeneracionEvaluacionDomain();
        var result = new ValidationResult();

        generacion.setEntregable(entregable, result);
        generacion.setEvaluacion(evaluacion, result);
        generacion.setEvaluacionAsesor(evaluacionAsesor, result);

        result.lanzarSiTieneErrores();
        return generacion;
    }

    private void setEntregable(EntregableDomain entregable, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(entregable,
                EvaluacionesFields.GeneracionEvaluacion.ENTREGABLE,
                EvaluacionesCodes.GeneracionEvaluacion.ENTREGABLE_REQUERIDO, result)) {
            return;
        }
        this.entregable = entregable;
    }

    private void setEvaluacion(EvaluacionDomain evaluacion, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacion,
                EvaluacionesFields.GeneracionEvaluacion.EVALUACION,
                EvaluacionesCodes.GeneracionEvaluacion.EVALUACION_REQUERIDA, result)) {
            return;
        }
        this.evaluacion = evaluacion;
    }

    private void setEvaluacionAsesor(EvaluacionAsesorDomain evaluacionAsesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacionAsesor,
                EvaluacionesFields.GeneracionEvaluacion.EVALUACION_ASESOR,
                EvaluacionesCodes.GeneracionEvaluacion.EVALUACION_ASESOR_REQUERIDA, result)) {
            return;
        }
        this.evaluacionAsesor = evaluacionAsesor;
    }

    public EntregableDomain getEntregable() {
        return entregable;
    }

    public EvaluacionDomain getEvaluacion() {
        return evaluacion;
    }

    public EvaluacionAsesorDomain getEvaluacionAsesor() {
        return evaluacionAsesor;
    }
}
