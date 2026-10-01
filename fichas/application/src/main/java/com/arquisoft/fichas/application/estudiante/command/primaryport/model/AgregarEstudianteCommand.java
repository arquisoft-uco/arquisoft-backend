package com.arquisoft.fichas.application.estudiante.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record AgregarEstudianteCommand(
        UUID id,
        String identificador,
        String nombre,
        String email,
        Instant ocurridoEn
) {
    public static AgregarEstudianteCommand crear(
            String id, String identificador, String nombre, String email, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(id, FichasFields.Estudiante.ID, FichasCodes.Estudiante.ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(identificador, FichasFields.Estudiante.IDENTIFICADOR,
                FichasCodes.Estudiante.IDENTIFICADOR_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(nombre, FichasFields.Estudiante.NOMBRE,
                FichasCodes.Estudiante.NOMBRE_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(email, FichasFields.Estudiante.EMAIL,
                FichasCodes.Estudiante.EMAIL_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn, FichasFields.Estudiante.OCURRIDO_EN,
                FichasCodes.Estudiante.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarEstudianteCommand(
                UtilUUID.generarUUIDDesdeTexto(id), identificador, nombre, email, ocurridoEn);
    }
}
