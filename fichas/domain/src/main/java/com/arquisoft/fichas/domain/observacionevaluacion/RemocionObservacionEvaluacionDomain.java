package com.arquisoft.fichas.domain.observacionevaluacion;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class RemocionObservacionEvaluacionDomain {

    private UUID observacionEvaluacion;
    private UUID representanteComite;

    private RemocionObservacionEvaluacionDomain() {}

    public static RemocionObservacionEvaluacionDomain crear(UUID observacionEvaluacion, UUID representanteComite) {
        var remocion = new RemocionObservacionEvaluacionDomain();
        var result = new ValidationResult();

        remocion.setObservacionEvaluacion(observacionEvaluacion, result);
        remocion.setRepresentanteComite(representanteComite, result);

        result.lanzarSiTieneErrores();
        return remocion;
    }

    private void setObservacionEvaluacion(UUID observacionEvaluacion, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(observacionEvaluacion,
                FichasFields.ObservacionEvaluacion.OBSERVACION_EVALUACION,
                FichasCodes.ObservacionEvaluacion.OBSERVACION_EVALUACION_REQUERIDA, result)) {
            return;
        }
        this.observacionEvaluacion = observacionEvaluacion;
    }

    private void setRepresentanteComite(UUID representanteComite, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(representanteComite,
                FichasFields.ObservacionEvaluacion.REPRESENTANTE_COMITE,
                FichasCodes.ObservacionEvaluacion.REPRESENTANTE_COMITE_REQUERIDO, result)) {
            return;
        }
        this.representanteComite = representanteComite;
    }

    public UUID getObservacionEvaluacion() {
        return observacionEvaluacion;
    }

    public UUID getRepresentanteComite() {
        return representanteComite;
    }
}
