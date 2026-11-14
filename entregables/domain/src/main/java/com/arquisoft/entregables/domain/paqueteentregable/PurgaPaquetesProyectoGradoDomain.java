package com.arquisoft.entregables.domain.paqueteentregable;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
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
