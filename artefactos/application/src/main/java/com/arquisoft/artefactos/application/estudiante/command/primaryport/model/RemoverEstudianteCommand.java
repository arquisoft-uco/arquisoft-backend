package com.arquisoft.artefactos.application.estudiante.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record RemoverEstudianteCommand(
        UUID id,
        String identificador,
        String nombre,
        String email,
        Instant ocurridoEn
) {
    public static RemoverEstudianteCommand crear(
            String id, String identificador, String nombre, String email, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(id, ArtefactosFields.Estudiante.ID, ArtefactosCodes.Estudiante.ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(identificador, ArtefactosFields.Estudiante.IDENTIFICADOR,
                ArtefactosCodes.Estudiante.IDENTIFICADOR_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(nombre, ArtefactosFields.Estudiante.NOMBRE,
                ArtefactosCodes.Estudiante.NOMBRE_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(email, ArtefactosFields.Estudiante.EMAIL,
                ArtefactosCodes.Estudiante.EMAIL_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn, ArtefactosFields.Estudiante.OCURRIDO_EN,
                ArtefactosCodes.Estudiante.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverEstudianteCommand(
                UtilUUID.generarUUIDDesdeTexto(id), identificador, nombre, email, ocurridoEn);
    }
}
