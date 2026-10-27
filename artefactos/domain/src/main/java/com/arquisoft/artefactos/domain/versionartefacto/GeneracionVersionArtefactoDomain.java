package com.arquisoft.artefactos.domain.versionartefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class GeneracionVersionArtefactoDomain {

    private UUID carga;
    private UUID artefacto;
    private UUID solicitante;

    private GeneracionVersionArtefactoDomain() {}

    public static GeneracionVersionArtefactoDomain crear(UUID carga, UUID artefacto, UUID solicitante) {
        var generacion = new GeneracionVersionArtefactoDomain();
        var result = new ValidationResult();

        generacion.setCarga(carga, result);
        generacion.setArtefacto(artefacto, result);
        generacion.setSolicitante(solicitante, result);

        result.lanzarSiTieneErrores();
        return generacion;
    }

    private void setCarga(UUID carga, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(carga,
                ArtefactosFields.VersionArtefacto.CARGA,
                ArtefactosCodes.VersionArtefacto.CARGA_REQUERIDA, result)) {
            return;
        }
        this.carga = carga;
    }

    private void setArtefacto(UUID artefacto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(artefacto,
                ArtefactosFields.VersionArtefacto.ARTEFACTO,
                ArtefactosCodes.VersionArtefacto.ARTEFACTO_REQUERIDO, result)) {
            return;
        }
        this.artefacto = artefacto;
    }

    private void setSolicitante(UUID solicitante, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(solicitante,
                ArtefactosFields.VersionArtefacto.SOLICITANTE,
                ArtefactosCodes.VersionArtefacto.SOLICITANTE_REQUERIDO, result)) {
            return;
        }
        this.solicitante = solicitante;
    }

    public UUID getCarga() {
        return carga;
    }

    public UUID getArtefacto() {
        return artefacto;
    }

    public UUID getSolicitante() {
        return solicitante;
    }
}
