package com.arquisoft.evaluaciones.domain.observacionitemjurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class RemocionObservacionItemJuradoDomain {

    private UUID observacionItemJurado;

    private RemocionObservacionItemJuradoDomain() {}

    public static RemocionObservacionItemJuradoDomain crear(UUID observacionItemJurado) {
        var remocion = new RemocionObservacionItemJuradoDomain();
        var result = new ValidationResult();

        remocion.setObservacionItemJurado(observacionItemJurado, result);

        result.lanzarSiTieneErrores();
        return remocion;
    }

    private void setObservacionItemJurado(UUID observacionItemJurado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(observacionItemJurado,
                EvaluacionesFields.ObservacionItemJurado.ID,
                EvaluacionesCodes.ObservacionItemJurado.ID_REQUERIDO, result)) {
            return;
        }
        this.observacionItemJurado = observacionItemJurado;
    }

    public UUID getObservacionItemJurado() {
        return observacionItemJurado;
    }
}
