package com.arquisoft.evaluaciones.domain.observacionitemjurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class RegistroObservacionItemJuradoDomain {

    private ObservacionItemJuradoDomain observacion;
    private UUID jurado;

    private RegistroObservacionItemJuradoDomain() {}

    public static RegistroObservacionItemJuradoDomain crear(ObservacionItemJuradoDomain observacion, UUID jurado) {
        var registro = new RegistroObservacionItemJuradoDomain();
        var result = new ValidationResult();

        registro.setObservacion(observacion, result);
        registro.setJurado(jurado, result);

        result.lanzarSiTieneErrores();
        return registro;
    }

    private void setObservacion(ObservacionItemJuradoDomain observacion, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(observacion,
                EvaluacionesFields.ObservacionItemJurado.EVALUACION_CUANTITATIVA_JURADO,
                EvaluacionesCodes.ObservacionItemJurado.EVALUACION_CUANTITATIVA_JURADO_REQUERIDA, result)) {
            return;
        }
        this.observacion = observacion;
    }

    private void setJurado(UUID jurado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(jurado,
                EvaluacionesFields.ObservacionItemJurado.JURADO,
                EvaluacionesCodes.ObservacionItemJurado.JURADO_REQUERIDO, result)) {
            return;
        }
        this.jurado = jurado;
    }

    public ObservacionItemJuradoDomain getObservacion() {
        return observacion;
    }

    public UUID getJurado() {
        return jurado;
    }
}
