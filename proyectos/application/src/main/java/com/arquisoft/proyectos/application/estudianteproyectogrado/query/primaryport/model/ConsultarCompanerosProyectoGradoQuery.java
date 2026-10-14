package com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarCompanerosProyectoGradoQuery(UUID proyectoGrado, UUID estudiante) {

    public static ConsultarCompanerosProyectoGradoQuery crear(UUID proyectoGrado, UUID estudiante) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(proyectoGrado,
                ProyectosFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.EstudianteProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result);

        ValidatorObjeto.noNulo(estudiante,
                ProyectosFields.EstudianteProyectoGrado.ESTUDIANTE,
                ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTE_ID_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarCompanerosProyectoGradoQuery(proyectoGrado, estudiante);
    }
}
