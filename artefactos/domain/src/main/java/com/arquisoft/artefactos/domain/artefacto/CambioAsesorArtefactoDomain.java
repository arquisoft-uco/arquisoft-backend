package com.arquisoft.artefactos.domain.artefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class CambioAsesorArtefactoDomain {

    private UUID artefacto;
    private UUID asesor;

    private CambioAsesorArtefactoDomain() {}

    public static CambioAsesorArtefactoDomain crear(UUID artefacto, UUID asesor) {
        var cambio = new CambioAsesorArtefactoDomain();
        var result = new ValidationResult();
        cambio.setArtefacto(artefacto, result);
        cambio.setAsesor(asesor, result);
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

    public UUID getArtefacto() {
        return artefacto;
    }

    public UUID getAsesor() {
        return asesor;
    }
}
