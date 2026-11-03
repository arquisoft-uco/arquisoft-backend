package com.arquisoft.artefactos.domain.versionartefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class EliminacionVersionArtefactoDomain {

    private UUID versionArtefacto;
    private UUID solicitante;

    private EliminacionVersionArtefactoDomain() {}

    public static EliminacionVersionArtefactoDomain crear(UUID versionArtefacto, UUID solicitante) {
        var eliminacion = new EliminacionVersionArtefactoDomain();
        var result = new ValidationResult();

        eliminacion.setVersionArtefacto(versionArtefacto, result);
        eliminacion.setSolicitante(solicitante, result);

        result.lanzarSiTieneErrores();
        return eliminacion;
    }

    private void setVersionArtefacto(UUID versionArtefacto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(versionArtefacto,
                ArtefactosFields.VersionArtefacto.VERSION_ARTEFACTO,
                ArtefactosCodes.VersionArtefacto.VERSION_ARTEFACTO_REQUERIDA, result)) {
            return;
        }
        this.versionArtefacto = versionArtefacto;
    }

    private void setSolicitante(UUID solicitante, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(solicitante,
                ArtefactosFields.VersionArtefacto.SOLICITANTE,
                ArtefactosCodes.VersionArtefacto.SOLICITANTE_REQUERIDO, result)) {
            return;
        }
        this.solicitante = solicitante;
    }

    public UUID getVersionArtefacto() {
        return versionArtefacto;
    }

    public UUID getSolicitante() {
        return solicitante;
    }
}
