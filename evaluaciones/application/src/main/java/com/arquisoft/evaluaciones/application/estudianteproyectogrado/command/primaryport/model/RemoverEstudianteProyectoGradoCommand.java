package com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record RemoverEstudianteProyectoGradoCommand(
        UUID proyectoGrado,
        UUID estudiante,
        Instant ocurridoEn
) {
    public static RemoverEstudianteProyectoGradoCommand crear(
            String proyectoGrado, String estudiante, Instant ocurridoEn) {
        var result = new ValidationResult();

        if (ValidatorUUID.uuidValido(proyectoGrado, EvaluacionesFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                EvaluacionesCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO, result)
                && UtilUUID.esPorDefecto(UtilUUID.generarUUIDDesdeTexto(proyectoGrado))) {
            result.agregarError(EvaluacionesFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                    EvaluacionesCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO,
                    Mensajes.formatear(ValidadorKey.UUID_INVALIDO,
                            EvaluacionesFields.EstudianteProyectoGrado.PROYECTO_GRADO));
        }
        ValidatorUUID.uuidValido(estudiante, EvaluacionesFields.EstudianteProyectoGrado.ESTUDIANTE,
                EvaluacionesCodes.EstudianteProyectoGrado.ESTUDIANTE_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn, EvaluacionesFields.EstudianteProyectoGrado.OCURRIDO_EN,
                EvaluacionesCodes.EstudianteProyectoGrado.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverEstudianteProyectoGradoCommand(
                UtilUUID.generarUUIDDesdeTexto(proyectoGrado), UtilUUID.generarUUIDDesdeTexto(estudiante),
                ocurridoEn);
    }
}
