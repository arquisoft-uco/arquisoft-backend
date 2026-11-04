package com.arquisoft.evaluaciones.application.jurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record AgregarJuradoCommand(
        UUID id,
        String identificador,
        String nombre,
        String email,
        Instant ocurridoEn
) {
    public static AgregarJuradoCommand crear(
            String id, String identificador, String nombre, String email, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(id, EvaluacionesFields.Jurado.ID,
                EvaluacionesCodes.Jurado.ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(identificador, EvaluacionesFields.Jurado.IDENTIFICADOR,
                EvaluacionesCodes.Jurado.IDENTIFICADOR_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(nombre, EvaluacionesFields.Jurado.NOMBRE,
                EvaluacionesCodes.Jurado.NOMBRE_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(email, EvaluacionesFields.Jurado.EMAIL,
                EvaluacionesCodes.Jurado.EMAIL_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn, EvaluacionesFields.Jurado.OCURRIDO_EN,
                EvaluacionesCodes.Jurado.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarJuradoCommand(
                UtilUUID.generarUUIDDesdeTexto(id), identificador, nombre, email, ocurridoEn);
    }
}
