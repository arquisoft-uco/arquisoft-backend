package com.arquisoft.artefactos.domain.observacion;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class EliminacionObservacionDomain {

    private UUID observacion;
    private UUID asesor;

    private EliminacionObservacionDomain() {}

    public static EliminacionObservacionDomain crear(UUID observacion, UUID asesor) {
        var eliminacion = new EliminacionObservacionDomain();
        var result = new ValidationResult();

        eliminacion.setObservacion(observacion, result);
        eliminacion.setAsesor(asesor, result);

        result.lanzarSiTieneErrores();
        return eliminacion;
    }

    private void setObservacion(UUID observacion, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(observacion,
                ArtefactosFields.Observacion.ID,
                ArtefactosCodes.Observacion.ID_REQUERIDO, result)) {
            return;
        }
        this.observacion = observacion;
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.Observacion.ASESOR,
                ArtefactosCodes.Observacion.ASESOR_REQUERIDO, result)) {
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
