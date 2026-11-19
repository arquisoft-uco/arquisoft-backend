package com.arquisoft.evaluaciones.application.asesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record RemoverAsesorCommand(
        UUID id,
        String identificador,
        String nombre,
        String email,
        Instant ocurridoEn
) {
    public static RemoverAsesorCommand crear(
            String id, String identificador, String nombre, String email, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(id, EvaluacionesFields.Asesor.ID,
                EvaluacionesCodes.Asesor.ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(identificador, EvaluacionesFields.Asesor.IDENTIFICADOR,
                EvaluacionesCodes.Asesor.IDENTIFICADOR_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(nombre, EvaluacionesFields.Asesor.NOMBRE,
                EvaluacionesCodes.Asesor.NOMBRE_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(email, EvaluacionesFields.Asesor.EMAIL,
                EvaluacionesCodes.Asesor.EMAIL_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn, EvaluacionesFields.Asesor.OCURRIDO_EN,
                EvaluacionesCodes.Asesor.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverAsesorCommand(
                UtilUUID.generarUUIDDesdeTexto(id), identificador, nombre, email, ocurridoEn);
    }
}
