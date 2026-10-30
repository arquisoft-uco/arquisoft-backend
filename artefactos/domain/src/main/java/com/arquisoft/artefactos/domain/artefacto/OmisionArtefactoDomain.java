package com.arquisoft.artefactos.domain.artefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class OmisionArtefactoDomain {

    private UUID artefacto;
    private UUID asesor;

    private OmisionArtefactoDomain() {}

    public static OmisionArtefactoDomain crear(UUID artefacto, UUID asesor) {
        var omision = new OmisionArtefactoDomain();
        var result = new ValidationResult();
        omision.setArtefacto(artefacto, result);
        omision.setAsesor(asesor, result);
        result.lanzarSiTieneErrores();
        return omision;
    }

    private void setArtefacto(UUID artefacto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(artefacto,
                ArtefactosFields.Artefacto.ID,
                ArtefactosCodes.Artefacto.ID_REQUERIDO, result)) {
            return;
        }
        this.artefacto = artefacto;
    }

    private void setAsesor(UUID asesor, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesor,
                ArtefactosFields.Artefacto.ASESOR,
                ArtefactosCodes.Artefacto.ASESOR_REQUERIDO, result)) {
            return;
        }
        this.asesor = asesor;
    }

    public UUID getArtefacto() {
        return artefacto;
    }

    public UUID getAsesor() {
        return asesor;
    }
}
