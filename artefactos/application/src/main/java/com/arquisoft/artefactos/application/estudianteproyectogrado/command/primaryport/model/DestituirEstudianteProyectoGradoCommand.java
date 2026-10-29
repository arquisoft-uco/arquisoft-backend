package com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record DestituirEstudianteProyectoGradoCommand(UUID estudiante, UUID proyectoGrado, Instant ocurridoEn) {

    public static DestituirEstudianteProyectoGradoCommand crear(
            String estudiante, String proyectoGrado, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(estudiante,
                ArtefactosFields.EstudianteProyectoGrado.ESTUDIANTE,
                ArtefactosCodes.EstudianteProyectoGrado.ESTUDIANTE_REQUERIDO, result);
        ValidatorUUID.uuidValido(proyectoGrado,
                ArtefactosFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                ArtefactosCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn,
                ArtefactosFields.EstudianteProyectoGrado.OCURRIDO_EN,
                ArtefactosCodes.EstudianteProyectoGrado.OCURRIDO_EN_REQUERIDO, result);
        result.lanzarSiTieneErroresDeEntrada();

        return new DestituirEstudianteProyectoGradoCommand(
                UtilUUID.generarUUIDDesdeTexto(estudiante), UtilUUID.generarUUIDDesdeTexto(proyectoGrado),
                ocurridoEn);
    }
}
