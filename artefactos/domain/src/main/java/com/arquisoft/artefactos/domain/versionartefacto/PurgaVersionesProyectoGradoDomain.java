package com.arquisoft.artefactos.domain.versionartefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class PurgaVersionesProyectoGradoDomain {

    private UUID proyectoGrado;

    private PurgaVersionesProyectoGradoDomain() {}

    public static PurgaVersionesProyectoGradoDomain crear(UUID proyectoGrado) {
        var purga = new PurgaVersionesProyectoGradoDomain();
        var result = new ValidationResult();
        purga.setProyectoGrado(proyectoGrado, result);
        result.lanzarSiTieneErrores();
        return purga;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                ArtefactosFields.ProyectoGrado.ID,
                ArtefactosCodes.ProyectoGrado.ID_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }
}
