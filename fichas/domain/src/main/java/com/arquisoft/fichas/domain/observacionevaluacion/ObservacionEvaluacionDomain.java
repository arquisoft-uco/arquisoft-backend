package com.arquisoft.fichas.domain.observacionevaluacion;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ObservacionEvaluacionDomain {

    private UUID id;
    private UUID evaluacionFichaPerfil;
    private String observacion;

    private ObservacionEvaluacionDomain() {}

    public static ObservacionEvaluacionDomain crear(UUID evaluacionFichaPerfil, String observacion) {
        var observacionEvaluacion = new ObservacionEvaluacionDomain();
        var result = new ValidationResult();

        observacionEvaluacion.setId();
        observacionEvaluacion.setEvaluacionFichaPerfil(evaluacionFichaPerfil, result);
        observacionEvaluacion.setObservacion(observacion, result);

        result.lanzarSiTieneErrores();
        return observacionEvaluacion;
    }

    private void setId() {
        this.id = UtilUUID.generarNuevoUUID();
    }

    private void setEvaluacionFichaPerfil(UUID evaluacionFichaPerfil, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(evaluacionFichaPerfil,
                FichasFields.ObservacionEvaluacion.EVALUACION_FICHA_PERFIL,
                FichasCodes.ObservacionEvaluacion.EVALUACION_FICHA_PERFIL_REQUERIDA, result)) {
            return;
        }
        this.evaluacionFichaPerfil = evaluacionFichaPerfil;
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

    public UUID getId() {
        return id;
    }

    public UUID getEvaluacionFichaPerfil() {
        return evaluacionFichaPerfil;
    }

    public String getObservacion() {
        return observacion;
    }
}
