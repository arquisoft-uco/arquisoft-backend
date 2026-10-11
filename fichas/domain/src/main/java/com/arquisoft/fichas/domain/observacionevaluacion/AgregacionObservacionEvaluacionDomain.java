package com.arquisoft.fichas.domain.observacionevaluacion;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class AgregacionObservacionEvaluacionDomain {

    private ObservacionEvaluacionDomain observacionEvaluacion;
    private UUID representanteComite;

    private AgregacionObservacionEvaluacionDomain() {}

    public static AgregacionObservacionEvaluacionDomain crear(ObservacionEvaluacionDomain observacionEvaluacion,
                                                              UUID representanteComite) {
        var agregacion = new AgregacionObservacionEvaluacionDomain();
        var result = new ValidationResult();

        agregacion.setObservacionEvaluacion(observacionEvaluacion, result);
        agregacion.setRepresentanteComite(representanteComite, result);

        result.lanzarSiTieneErrores();
        return agregacion;
    }

    private void setObservacionEvaluacion(ObservacionEvaluacionDomain observacionEvaluacion, ValidationResult result) {
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

    public ObservacionEvaluacionDomain getObservacionEvaluacion() {
        return observacionEvaluacion;
    }

    public UUID getEvaluacionFichaPerfil() {
        return observacionEvaluacion.getEvaluacionFichaPerfil();
    }

    public String getObservacion() {
        return observacionEvaluacion.getObservacion();
    }

    public UUID getRepresentanteComite() {
        return representanteComite;
    }
}
