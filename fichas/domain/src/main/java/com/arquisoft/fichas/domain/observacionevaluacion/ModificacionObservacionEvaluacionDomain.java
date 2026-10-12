package com.arquisoft.fichas.domain.observacionevaluacion;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ModificacionObservacionEvaluacionDomain {

    private UUID observacionEvaluacion;
    private String observacion;
    private UUID representanteComite;

    private ModificacionObservacionEvaluacionDomain() {}

    public static ModificacionObservacionEvaluacionDomain crear(UUID observacionEvaluacion, String observacion,
                                                                UUID representanteComite) {
        var modificacion = new ModificacionObservacionEvaluacionDomain();
        var result = new ValidationResult();

        modificacion.setObservacionEvaluacion(observacionEvaluacion, result);
        modificacion.setObservacion(observacion, result);
        modificacion.setRepresentanteComite(representanteComite, result);

        result.lanzarSiTieneErrores();
        return modificacion;
    }

    private void setObservacionEvaluacion(UUID observacionEvaluacion, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(observacionEvaluacion,
                FichasFields.ObservacionEvaluacion.OBSERVACION_EVALUACION,
                FichasCodes.ObservacionEvaluacion.OBSERVACION_EVALUACION_REQUERIDA, result)) {
            return;
        }
        this.observacionEvaluacion = observacionEvaluacion;
    }

    private void setObservacion(String observacion, ValidationResult result) {
        var recortada = UtilTexto.aplicarTrim(observacion);
        if (!ValidatorTexto.noEnBlanco(recortada,
                FichasFields.ObservacionEvaluacion.OBSERVACION,
                FichasCodes.ObservacionEvaluacion.OBSERVACION_REQUERIDA, result)) {
            return;
        }
        if (!ValidatorLongitud.longitudMaxima(recortada, FichasLimits.ObservacionEvaluacion.OBSERVACION_MAX,
                FichasFields.ObservacionEvaluacion.OBSERVACION,
                FichasCodes.ObservacionEvaluacion.OBSERVACION_DEMASIADO_LARGA, result)) {
            return;
        }
        this.observacion = recortada;
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

    public String getObservacion() {
        return observacion;
    }

    public UUID getRepresentanteComite() {
        return representanteComite;
    }
}
