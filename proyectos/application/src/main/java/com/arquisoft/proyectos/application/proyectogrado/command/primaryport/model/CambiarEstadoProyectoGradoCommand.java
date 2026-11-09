package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record CambiarEstadoProyectoGradoCommand(
        UUID proyectoGrado,
        UUID asesor,
        String estadoProyecto
) {

    public static CambiarEstadoProyectoGradoCommand crear(UUID proyectoGrado, UUID asesor, String estadoProyecto) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(proyectoGrado,
                ProyectosFields.ProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.ProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result);

        ValidatorObjeto.noNulo(asesor,
                ProyectosFields.ProyectoGrado.ASESOR,
                ProyectosCodes.ProyectoGrado.ASESOR_ID_REQUERIDO, result);

        ValidatorTexto.noEnBlanco(estadoProyecto,
                ProyectosFields.ProyectoGrado.ESTADO_PROYECTO,
                ProyectosCodes.ProyectoGrado.ESTADO_PROYECTO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new CambiarEstadoProyectoGradoCommand(proyectoGrado, asesor, estadoProyecto);
    }
}
