package com.arquisoft.evaluaciones.domain.observacionitemasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class RemocionObservacionItemAsesorDomain {

    private UUID observacion;
    private UUID asesor;

    private RemocionObservacionItemAsesorDomain() {}

    public static RemocionObservacionItemAsesorDomain crear(UUID observacion, UUID asesor) {
        var remocion = new RemocionObservacionItemAsesorDomain();
        var result = new ValidationResult();

        remocion.setObservacion(observacion, result);
        remocion.setAsesor(asesor, result);

        result.lanzarSiTieneErrores();
        return remocion;
    }

    private void setObservacion(UUID observacion, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(observacion,
                EvaluacionesFields.ObservacionItemAsesor.ID,
                EvaluacionesCodes.ObservacionItemAsesor.ID_REQUERIDO, result)) {
            return;
        }
        this.observacion = observacion;
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                EvaluacionesFields.ObservacionItemAsesor.ASESOR,
                EvaluacionesCodes.ObservacionItemAsesor.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
    }

    public UUID getObservacion() {
        return observacion;
    }

    public UUID getAsesor() {
        return asesor;
    }
}
