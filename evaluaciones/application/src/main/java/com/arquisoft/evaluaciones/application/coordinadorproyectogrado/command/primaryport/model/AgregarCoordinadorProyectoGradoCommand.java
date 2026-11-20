package com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.primaryport.model;

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

public record AgregarCoordinadorProyectoGradoCommand(
        UUID proyectoGrado,
        UUID coordinador,
        String nombre,
        String email,
        Instant ocurridoEn
) {
    public static AgregarCoordinadorProyectoGradoCommand crear(
            String proyectoGrado, String coordinador, String nombre, String email, Instant ocurridoEn) {
        var result = new ValidationResult();

        if (ValidatorUUID.uuidValido(proyectoGrado, EvaluacionesFields.CoordinadorProyectoGrado.PROYECTO_GRADO,
                EvaluacionesCodes.CoordinadorProyectoGrado.PROYECTO_GRADO_REQUERIDO, result)
                && UtilUUID.esPorDefecto(UtilUUID.generarUUIDDesdeTexto(proyectoGrado))) {
            result.agregarError(EvaluacionesFields.CoordinadorProyectoGrado.PROYECTO_GRADO,
                    EvaluacionesCodes.CoordinadorProyectoGrado.PROYECTO_GRADO_REQUERIDO,
                    Mensajes.obtener(ValidadorKey.UUID_INVALIDO));
        }
        ValidatorUUID.uuidValido(coordinador, EvaluacionesFields.CoordinadorProyectoGrado.COORDINADOR,
                EvaluacionesCodes.CoordinadorProyectoGrado.COORDINADOR_REQUERIDO, result);
        ValidatorObjeto.noNulo(nombre, EvaluacionesFields.CoordinadorProyectoGrado.NOMBRE,
                EvaluacionesCodes.CoordinadorProyectoGrado.NOMBRE_REQUERIDO, result);
        ValidatorObjeto.noNulo(email, EvaluacionesFields.CoordinadorProyectoGrado.EMAIL,
                EvaluacionesCodes.CoordinadorProyectoGrado.EMAIL_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn, EvaluacionesFields.CoordinadorProyectoGrado.OCURRIDO_EN,
                EvaluacionesCodes.CoordinadorProyectoGrado.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarCoordinadorProyectoGradoCommand(
                UtilUUID.generarUUIDDesdeTexto(proyectoGrado),
                UtilUUID.generarUUIDDesdeTexto(coordinador),
                nombre,
                email,
                ocurridoEn);
    }
}
