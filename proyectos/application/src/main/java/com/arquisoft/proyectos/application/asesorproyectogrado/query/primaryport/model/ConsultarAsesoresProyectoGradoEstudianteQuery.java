package com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarAsesoresProyectoGradoEstudianteQuery(UUID proyectoGrado, UUID estudiante) {

    public static ConsultarAsesoresProyectoGradoEstudianteQuery crear(UUID proyectoGrado, UUID estudiante) {
        var result = new ValidationResult();
        ValidatorObjeto.noNulo(proyectoGrado,
                ProyectosFields.AsesorProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.AsesorProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result);
        ValidatorObjeto.noNulo(estudiante,
                ProyectosFields.AsesorProyectoGrado.ESTUDIANTE,
                ProyectosCodes.AsesorProyectoGrado.ESTUDIANTE_ID_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();
        return new ConsultarAsesoresProyectoGradoEstudianteQuery(proyectoGrado, estudiante);
    }
}
