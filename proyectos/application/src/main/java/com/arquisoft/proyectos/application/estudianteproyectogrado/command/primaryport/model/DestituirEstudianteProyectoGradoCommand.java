package com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record DestituirEstudianteProyectoGradoCommand(
        UUID proyectoGrado,
        UUID estudiante,
        UUID coordinador
) {

    public static DestituirEstudianteProyectoGradoCommand crear(UUID proyectoGrado, UUID estudiante,
                                                                UUID coordinador) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(proyectoGrado,
                ProyectosFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.EstudianteProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result);

        ValidatorObjeto.noNulo(estudiante,
                ProyectosFields.EstudianteProyectoGrado.ESTUDIANTE,
                ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTE_ID_REQUERIDO, result);

        ValidatorObjeto.noNulo(coordinador,
                ProyectosFields.EstudianteProyectoGrado.COORDINADOR,
                ProyectosCodes.EstudianteProyectoGrado.COORDINADOR_ID_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new DestituirEstudianteProyectoGradoCommand(proyectoGrado, estudiante, coordinador);
    }
}
