package com.arquisoft.artefactos.domain.revisionasesor;

import com.arquisoft.artefactos.domain.observacion.AvanceObservacionesRevisionDomain;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class EnvioRevisionAsesorDomain {

    private UUID revisionAsesor;
    private UUID asesor;
    private AvanceObservacionesRevisionDomain observaciones;

    private EnvioRevisionAsesorDomain() {}

    public static EnvioRevisionAsesorDomain crear(
            UUID revisionAsesor, UUID asesor, AvanceObservacionesRevisionDomain observaciones) {
        var envio = new EnvioRevisionAsesorDomain();
        var result = new ValidationResult();

        envio.setRevisionAsesor(revisionAsesor, result);
        envio.setAsesor(asesor, result);
        envio.setObservaciones(observaciones, result);

        result.lanzarSiTieneErrores();
        return envio;
    }

    private void setRevisionAsesor(UUID revisionAsesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(revisionAsesor,
                ArtefactosFields.RevisionAsesor.ID,
                ArtefactosCodes.RevisionAsesor.ID_REQUERIDO, result)) {
            return;
        }
        this.revisionAsesor = revisionAsesor;
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.RevisionAsesor.ASESOR,
                ArtefactosCodes.RevisionAsesor.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
    }

    private void setObservaciones(AvanceObservacionesRevisionDomain observaciones, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(observaciones,
                ArtefactosFields.Observacion.REVISION_ASESOR,
                ArtefactosCodes.Observacion.REVISION_ASESOR_ID_REQUERIDO, result)) {
            return;
        }
        this.observaciones = observaciones;
    }

    public UUID getRevisionAsesor() {
        return revisionAsesor;
    }

    public UUID getAsesor() {
        return asesor;
    }

    public AvanceObservacionesRevisionDomain getObservaciones() {
        return observaciones;
    }
}
