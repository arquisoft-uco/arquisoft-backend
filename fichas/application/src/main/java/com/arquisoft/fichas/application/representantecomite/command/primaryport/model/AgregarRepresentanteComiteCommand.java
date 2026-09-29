package com.arquisoft.fichas.application.representantecomite.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record AgregarRepresentanteComiteCommand(
        UUID id,
        String identificador,
        String nombre,
        String email,
        Instant ocurridoEn
) {
    public AgregarRepresentanteComiteCommand {
        identificador = UtilTexto.aplicarTrim(identificador);
        nombre = UtilTexto.aplicarTrim(nombre);
        email = UtilTexto.aplicarTrim(email);
    }

    public static AgregarRepresentanteComiteCommand crear(
            String id, String identificador, String nombre, String email, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(id, FichasFields.RepresentanteComite.ID,
                FichasCodes.RepresentanteComite.ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(identificador, FichasFields.RepresentanteComite.IDENTIFICADOR,
                FichasCodes.RepresentanteComite.IDENTIFICADOR_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(nombre, FichasFields.RepresentanteComite.NOMBRE,
                FichasCodes.RepresentanteComite.NOMBRE_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(email, FichasFields.RepresentanteComite.EMAIL,
                FichasCodes.RepresentanteComite.EMAIL_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn, FichasFields.RepresentanteComite.OCURRIDO_EN,
                FichasCodes.RepresentanteComite.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarRepresentanteComiteCommand(
                UtilUUID.generarUUIDDesdeTexto(id), identificador, nombre, email, ocurridoEn);
    }
}
