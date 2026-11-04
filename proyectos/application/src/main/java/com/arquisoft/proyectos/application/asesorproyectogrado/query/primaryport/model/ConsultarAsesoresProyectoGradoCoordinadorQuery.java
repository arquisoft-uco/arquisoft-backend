package com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarAsesoresProyectoGradoCoordinadorQuery(UUID proyectoGrado, UUID coordinador) {

    public static ConsultarAsesoresProyectoGradoCoordinadorQuery crear(UUID proyectoGrado, UUID coordinador) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(proyectoGrado,
                ProyectosFields.AsesorProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.AsesorProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result);

        ValidatorObjeto.noNulo(coordinador,
                ProyectosFields.AsesorProyectoGrado.COORDINADOR,
                ProyectosCodes.AsesorProyectoGrado.COORDINADOR_ID_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarAsesoresProyectoGradoCoordinadorQuery(proyectoGrado, coordinador);
    }
}
