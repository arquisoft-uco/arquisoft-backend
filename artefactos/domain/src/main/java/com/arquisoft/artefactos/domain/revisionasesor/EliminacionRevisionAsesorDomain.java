package com.arquisoft.artefactos.domain.revisionasesor;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class EliminacionRevisionAsesorDomain {

    private UUID revisionAsesor;
    private UUID asesor;

    private EliminacionRevisionAsesorDomain() {}

    public static EliminacionRevisionAsesorDomain crear(UUID revisionAsesor, UUID asesor) {
        var eliminacion = new EliminacionRevisionAsesorDomain();
        var result = new ValidationResult();

        eliminacion.setRevisionAsesor(revisionAsesor, result);
        eliminacion.setAsesor(asesor, result);

        result.lanzarSiTieneErrores();
        return eliminacion;
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

    public UUID getRevisionAsesor() {
        return revisionAsesor;
    }

    public UUID getAsesor() {
        return asesor;
    }
}
