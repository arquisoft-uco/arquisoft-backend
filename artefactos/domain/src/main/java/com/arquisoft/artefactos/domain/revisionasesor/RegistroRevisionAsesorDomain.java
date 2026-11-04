package com.arquisoft.artefactos.domain.revisionasesor;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class RegistroRevisionAsesorDomain {

    private RevisionAsesorDomain revision;
    private UUID asesor;

    private RegistroRevisionAsesorDomain() {}

    public static RegistroRevisionAsesorDomain crear(RevisionAsesorDomain revision, UUID asesor) {
        var registro = new RegistroRevisionAsesorDomain();
        var result = new ValidationResult();

        registro.setRevision(revision, result);
        registro.setAsesor(asesor, result);

        result.lanzarSiTieneErrores();
        return registro;
    }

    private void setRevision(RevisionAsesorDomain revision, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(revision,
                ArtefactosFields.RevisionAsesor.ID,
                ArtefactosCodes.RevisionAsesor.ID_REQUERIDO, result)) {
            return;
        }
        this.revision = revision;
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.RevisionAsesor.ASESOR,
                ArtefactosCodes.RevisionAsesor.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
    }

    public RevisionAsesorDomain getRevision() {
        return revision;
    }

    public UUID getAsesor() {
        return asesor;
    }
}
