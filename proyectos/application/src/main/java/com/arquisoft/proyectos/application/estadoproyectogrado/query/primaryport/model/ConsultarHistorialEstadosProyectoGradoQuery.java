package com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarHistorialEstadosProyectoGradoQuery(UUID proyectoGrado, UUID usuario) {

    public static ConsultarHistorialEstadosProyectoGradoQuery crear(UUID proyectoGrado, UUID usuario) {
        var result = new ValidationResult();
        ValidatorObjeto.noNulo(proyectoGrado,
                ProyectosFields.EstadoProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.EstadoProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result);
        ValidatorObjeto.noNulo(usuario,
                ProyectosFields.EstadoProyectoGrado.USUARIO,
                ProyectosCodes.EstadoProyectoGrado.USUARIO_ID_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();
        return new ConsultarHistorialEstadosProyectoGradoQuery(proyectoGrado, usuario);
    }
}
