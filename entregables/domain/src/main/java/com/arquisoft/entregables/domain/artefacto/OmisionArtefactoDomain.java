package com.arquisoft.entregables.domain.artefacto;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.time.Instant;
import java.util.UUID;

public final class OmisionArtefactoDomain {

    private UUID artefacto;
    private Instant ocurridoEn;

    private OmisionArtefactoDomain() {}

    public static OmisionArtefactoDomain crear(UUID artefacto, Instant ocurridoEn) {
        var omision = new OmisionArtefactoDomain();
        var result = new ValidationResult();
        omision.setArtefacto(artefacto, result);
        omision.setOcurridoEn(ocurridoEn, result);
        result.lanzarSiTieneErrores();
        return omision;
    }

    private void setArtefacto(UUID artefacto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(artefacto,
                EntregablesFields.Artefacto.ID,
                EntregablesCodes.Artefacto.ID_REQUERIDO, result)) {
            return;
        }
        this.artefacto = artefacto;
    }

    private void setOcurridoEn(Instant ocurridoEn, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(ocurridoEn,
                EntregablesFields.Artefacto.OCURRIDO_EN,
                EntregablesCodes.Artefacto.OCURRIDO_EN_REQUERIDO, result)) {
            return;
        }
        this.ocurridoEn = ocurridoEn;
    }

    public UUID getArtefacto() {
        return artefacto;
    }

    public Instant getOcurridoEn() {
        return ocurridoEn;
    }
}
