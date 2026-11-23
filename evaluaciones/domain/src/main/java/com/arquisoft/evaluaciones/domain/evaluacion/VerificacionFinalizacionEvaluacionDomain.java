package com.arquisoft.evaluaciones.domain.evaluacion;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class VerificacionFinalizacionEvaluacionDomain {

    private UUID evaluacion;

    private VerificacionFinalizacionEvaluacionDomain() {}

    public static VerificacionFinalizacionEvaluacionDomain crear(UUID evaluacion) {
        var verificacion = new VerificacionFinalizacionEvaluacionDomain();
        var result = new ValidationResult();

        verificacion.setEvaluacion(evaluacion, result);

        result.lanzarSiTieneErrores();
        return verificacion;
    }

    private void setEvaluacion(UUID evaluacion, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacion,
                EvaluacionesFields.Evaluacion.EVALUACION,
                EvaluacionesCodes.Evaluacion.EVALUACION_REQUERIDO, result)) {
            return;
        }
        this.evaluacion = evaluacion;
    }

    public UUID getEvaluacion() {
        return evaluacion;
    }
}
