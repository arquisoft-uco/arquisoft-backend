package com.arquisoft.artefactos.application.asesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
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

        ValidatorUUID.uuidValido(id, ArtefactosFields.Asesor.ID, ArtefactosCodes.Asesor.ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(identificador, ArtefactosFields.Asesor.IDENTIFICADOR,
                ArtefactosCodes.Asesor.IDENTIFICADOR_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(nombre, ArtefactosFields.Asesor.NOMBRE,
                ArtefactosCodes.Asesor.NOMBRE_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(email, ArtefactosFields.Asesor.EMAIL,
                ArtefactosCodes.Asesor.EMAIL_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn, ArtefactosFields.Asesor.OCURRIDO_EN,
                ArtefactosCodes.Asesor.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RemoverAsesorCommand(
                UtilUUID.generarUUIDDesdeTexto(id), identificador, nombre, email, ocurridoEn);
    }
}
