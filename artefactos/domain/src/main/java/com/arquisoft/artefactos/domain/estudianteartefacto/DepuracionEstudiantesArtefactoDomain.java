package com.arquisoft.artefactos.domain.estudianteartefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class DepuracionEstudiantesArtefactoDomain {

    private UUID artefacto;

    private DepuracionEstudiantesArtefactoDomain() {}

    public static DepuracionEstudiantesArtefactoDomain crear(UUID artefacto) {
        var depuracion = new DepuracionEstudiantesArtefactoDomain();
        var result = new ValidationResult();
        depuracion.setArtefacto(artefacto, result);
        result.lanzarSiTieneErrores();
        return depuracion;
    }

    private void setArtefacto(UUID artefacto, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(artefacto,
                ArtefactosFields.EstudianteArtefacto.ARTEFACTO,
                ArtefactosCodes.EstudianteArtefacto.ARTEFACTO_ID_REQUERIDO, result)) {
            return;
        }
        this.artefacto = artefacto;
    }

    public UUID getArtefacto() {
        return artefacto;
    }
}
