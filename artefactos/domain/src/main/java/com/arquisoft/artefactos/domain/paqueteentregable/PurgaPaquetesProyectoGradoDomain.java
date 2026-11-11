package com.arquisoft.artefactos.domain.paqueteentregable;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class PurgaPaquetesProyectoGradoDomain {

    private UUID proyectoGrado;

    private PurgaPaquetesProyectoGradoDomain() {}

    public static PurgaPaquetesProyectoGradoDomain crear(UUID proyectoGrado) {
        var purga = new PurgaPaquetesProyectoGradoDomain();
        var result = new ValidationResult();
        purga.setProyectoGrado(proyectoGrado, result);
        result.lanzarSiTieneErrores();
        return purga;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                ArtefactosFields.PaqueteEntregable.PROYECTO_GRADO,
                ArtefactosCodes.PaqueteEntregable.PROYECTO_GRADO_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }
}
