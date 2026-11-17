package com.arquisoft.entregables.domain.artefacto;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class PurgaArtefactosProyectoGradoDomain {

    private UUID proyectoGrado;

    private PurgaArtefactosProyectoGradoDomain() {}

    public static PurgaArtefactosProyectoGradoDomain crear(UUID proyectoGrado) {
        var purga = new PurgaArtefactosProyectoGradoDomain();
        var result = new ValidationResult();
        purga.setProyectoGrado(proyectoGrado, result);
        result.lanzarSiTieneErrores();
        return purga;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                EntregablesFields.ProyectoGrado.ID,
                EntregablesCodes.ProyectoGrado.ID_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }
}
