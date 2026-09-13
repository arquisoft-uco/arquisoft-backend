package com.arquisoft.fichas.application.coordinador.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record AgregarCoordinadorCommand(
        UUID id,
        String identificador,
        String nombre,
        String email,
        Instant ocurridoEn
) {
    public static AgregarCoordinadorCommand crear(
            String id, String identificador, String nombre, String email, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(id, FichasFields.Coordinador.ID, FichasCodes.Coordinador.ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(identificador, FichasFields.Coordinador.IDENTIFICADOR,
                FichasCodes.Coordinador.IDENTIFICADOR_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(nombre, FichasFields.Coordinador.NOMBRE,
                FichasCodes.Coordinador.NOMBRE_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(email, FichasFields.Coordinador.EMAIL,
                FichasCodes.Coordinador.EMAIL_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn, FichasFields.Coordinador.OCURRIDO_EN,
                FichasCodes.Coordinador.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarCoordinadorCommand(
                UtilUUID.generarUUIDDesdeTexto(id), identificador, nombre, email, ocurridoEn);
    }
}
