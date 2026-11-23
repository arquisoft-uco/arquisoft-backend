package com.arquisoft.evaluaciones.domain.evaluacionasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class CompletitudEvaluacionAsesorDomain {

    private UUID evaluacionAsesor;
    private boolean completa;

    private CompletitudEvaluacionAsesorDomain() {}

    public static CompletitudEvaluacionAsesorDomain crear(UUID evaluacionAsesor, boolean completa) {
        var accion = new CompletitudEvaluacionAsesorDomain();
        var result = new ValidationResult();

        accion.setEvaluacionAsesor(evaluacionAsesor, result);
        accion.setCompleta(completa);

        result.lanzarSiTieneErrores();
        return accion;
    }

    private void setEvaluacionAsesor(UUID evaluacionAsesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacionAsesor,
                EvaluacionesFields.EvaluacionAsesor.EVALUACION_ASESOR,
                EvaluacionesCodes.EvaluacionAsesor.EVALUACION_ASESOR_REQUERIDO, result)) {
            return;
        }
        this.evaluacionAsesor = evaluacionAsesor;
    }

    private void setCompleta(boolean completa) {
        this.completa = completa;
    }

    public UUID getEvaluacionAsesor() {
        return evaluacionAsesor;
    }

    public boolean isCompleta() {
        return completa;
    }
}
