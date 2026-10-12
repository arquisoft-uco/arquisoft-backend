package com.arquisoft.fichas.application.asesorficha.command.primaryport.model;

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

public record ActualizarAsesorFichaCommand(
        UUID id,
        String identificador,
        String nombre,
        String email,
        Instant ocurridoEn
) {
    public ActualizarAsesorFichaCommand {
        identificador = UtilTexto.aplicarTrim(identificador);
        nombre = UtilTexto.aplicarTrim(nombre);
        email = UtilTexto.aplicarTrim(email);
    }

    public static ActualizarAsesorFichaCommand crear(
            String id, String identificador, String nombre, String email, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(id, FichasFields.AsesorFicha.ID, FichasCodes.AsesorFicha.ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(identificador, FichasFields.AsesorFicha.IDENTIFICADOR,
                FichasCodes.AsesorFicha.IDENTIFICADOR_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(nombre, FichasFields.AsesorFicha.NOMBRE,
                FichasCodes.AsesorFicha.NOMBRE_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(email, FichasFields.AsesorFicha.EMAIL,
                FichasCodes.AsesorFicha.EMAIL_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn, FichasFields.AsesorFicha.OCURRIDO_EN,
                FichasCodes.AsesorFicha.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ActualizarAsesorFichaCommand(
                UtilUUID.generarUUIDDesdeTexto(id), identificador, nombre, email, ocurridoEn);
    }
}
