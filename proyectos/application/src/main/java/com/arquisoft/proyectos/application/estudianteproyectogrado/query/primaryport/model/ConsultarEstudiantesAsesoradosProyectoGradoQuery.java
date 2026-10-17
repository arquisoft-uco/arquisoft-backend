package com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEstudiantesAsesoradosProyectoGradoQuery(UUID proyectoGrado, UUID asesor) {

    public static ConsultarEstudiantesAsesoradosProyectoGradoQuery crear(UUID proyectoGrado, UUID asesor) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(proyectoGrado,
                ProyectosFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.EstudianteProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result);

        ValidatorObjeto.noNulo(asesor,
                ProyectosFields.EstudianteProyectoGrado.ASESOR,
                ProyectosCodes.EstudianteProyectoGrado.ASESOR_ID_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEstudiantesAsesoradosProyectoGradoQuery(proyectoGrado, asesor);
    }
}
