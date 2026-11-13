package com.arquisoft.evaluaciones.domain.observacionitemasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class RegistroObservacionItemAsesorDomain {

    private ObservacionItemAsesorDomain observacion;
    private UUID asesor;

    private RegistroObservacionItemAsesorDomain() {}

    public static RegistroObservacionItemAsesorDomain crear(ObservacionItemAsesorDomain observacion, UUID asesor) {
        var registro = new RegistroObservacionItemAsesorDomain();
        var result = new ValidationResult();

        registro.setObservacion(observacion, result);
        registro.setAsesor(asesor, result);

        result.lanzarSiTieneErrores();
        return registro;
    }

    private void setObservacion(ObservacionItemAsesorDomain observacion, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(observacion,
                EvaluacionesFields.ObservacionItemAsesor.OBSERVACION,
                EvaluacionesCodes.ObservacionItemAsesor.OBSERVACION_REQUERIDA, result)) {
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

    public ObservacionItemAsesorDomain getObservacion() {
        return observacion;
    }

    public UUID getAsesor() {
        return asesor;
    }
}
