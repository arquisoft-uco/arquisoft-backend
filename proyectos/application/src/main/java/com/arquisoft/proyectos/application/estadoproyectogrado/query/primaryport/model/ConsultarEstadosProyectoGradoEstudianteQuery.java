package com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEstadosProyectoGradoEstudianteQuery(UUID proyectoGrado, UUID estudiante) {

    public static ConsultarEstadosProyectoGradoEstudianteQuery crear(UUID proyectoGrado, UUID estudiante) {
        var result = new ValidationResult();
        ValidatorObjeto.noNulo(proyectoGrado,
                ProyectosFields.EstadoProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.EstadoProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result);
        ValidatorObjeto.noNulo(estudiante,
                ProyectosFields.EstadoProyectoGrado.ESTUDIANTE,
                ProyectosCodes.EstadoProyectoGrado.ESTUDIANTE_ID_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();
        return new ConsultarEstadosProyectoGradoEstudianteQuery(proyectoGrado, estudiante);
    }
}
