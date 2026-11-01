package com.arquisoft.artefactos.domain.artefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class CambioVersionRepositorioArtefactoDomain {

    private UUID artefacto;
    private UUID asesor;
    private UUID versionRepositorioArtefacto;

    private CambioVersionRepositorioArtefactoDomain() {}

    public static CambioVersionRepositorioArtefactoDomain crear(
            UUID artefacto, UUID asesor, UUID versionRepositorioArtefacto) {
        var cambio = new CambioVersionRepositorioArtefactoDomain();
        var result = new ValidationResult();
        cambio.setArtefacto(artefacto, result);
        cambio.setAsesor(asesor, result);
        cambio.setVersionRepositorioArtefacto(versionRepositorioArtefacto, result);
        result.lanzarSiTieneErrores();
        return cambio;
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

    private void setVersionRepositorioArtefacto(UUID versionRepositorioArtefacto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(versionRepositorioArtefacto,
                ArtefactosFields.Artefacto.VERSION_REPOSITORIO_ARTEFACTO,
                ArtefactosCodes.Artefacto.VERSION_REPOSITORIO_ARTEFACTO_REQUERIDA, result)) {
            return;
        }
        this.versionRepositorioArtefacto = versionRepositorioArtefacto;
    }

    public UUID getArtefacto() {
        return artefacto;
    }

    public UUID getAsesor() {
        return asesor;
    }

    public UUID getVersionRepositorioArtefacto() {
        return versionRepositorioArtefacto;
    }
}
