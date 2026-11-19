package com.arquisoft.proyectos.domain.proyectogrado;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class AtrasoProyectoGradoDomain {

    private UUID proyectoGrado;

    private AtrasoProyectoGradoDomain() {}

    public static AtrasoProyectoGradoDomain crear(UUID proyectoGrado) {
        var atraso = new AtrasoProyectoGradoDomain();
        var result = new ValidationResult();

        atraso.setProyectoGrado(proyectoGrado, result);

        result.lanzarSiTieneErrores();
        return atraso;
    }

    private void setProyectoGrado(UUID proyectoGrado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(proyectoGrado,
                ProyectosFields.ProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.ProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result)) {
            return;
        }
        this.proyectoGrado = proyectoGrado;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }
}
