package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record AtrasarProyectoGradoCommand(UUID proyectoGrado) {

    public static AtrasarProyectoGradoCommand crear(String proyectoGrado) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(proyectoGrado,
                ProyectosFields.ProyectoGrado.PROYECTO_GRADO,
                ProyectosCodes.ProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new AtrasarProyectoGradoCommand(UtilUUID.generarUUIDDesdeTexto(proyectoGrado));
    }
}
