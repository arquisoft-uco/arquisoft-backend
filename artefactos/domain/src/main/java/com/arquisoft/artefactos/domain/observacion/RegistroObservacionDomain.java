package com.arquisoft.artefactos.domain.observacion;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class RegistroObservacionDomain {

    private ObservacionDomain observacion;
    private UUID asesor;

    private RegistroObservacionDomain() {}

    public static RegistroObservacionDomain crear(ObservacionDomain observacion, UUID asesor) {
        var registro = new RegistroObservacionDomain();
        var result = new ValidationResult();

        registro.setObservacion(observacion, result);
        registro.setAsesor(asesor, result);

        result.lanzarSiTieneErrores();
        return registro;
    }

    private void setObservacion(ObservacionDomain observacion, ValidationResult result) {
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

    public ObservacionDomain getObservacion() {
        return observacion;
    }

    public UUID getAsesor() {
        return asesor;
    }
}
