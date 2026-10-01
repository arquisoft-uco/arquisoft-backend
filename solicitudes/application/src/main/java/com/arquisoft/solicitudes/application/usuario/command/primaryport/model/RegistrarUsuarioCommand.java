package com.arquisoft.solicitudes.application.usuario.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record RegistrarUsuarioCommand(
        UUID usuarioId,
        String identificador,
        String nombre,
        String email,
        Instant ocurridoEn
) {
    public RegistrarUsuarioCommand {
        identificador = UtilTexto.aplicarTrim(identificador);
        nombre = UtilTexto.aplicarTrim(nombre);
        email = UtilTexto.aplicarTrim(email);
    }

    public static RegistrarUsuarioCommand crear(
            String usuarioId, String identificador, String nombre, String email, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(usuarioId, SolicitudesFields.Usuario.ID,
                SolicitudesCodes.Usuario.ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(identificador, SolicitudesFields.Usuario.IDENTIFICADOR,
                SolicitudesCodes.Usuario.IDENTIFICADOR_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(nombre, SolicitudesFields.Usuario.NOMBRE,
                SolicitudesCodes.Usuario.NOMBRE_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(email, SolicitudesFields.Usuario.EMAIL,
                SolicitudesCodes.Usuario.EMAIL_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn, SolicitudesFields.Usuario.OCURRIDO_EN,
                SolicitudesCodes.Usuario.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new RegistrarUsuarioCommand(
                UtilUUID.generarUUIDDesdeTexto(usuarioId), identificador, nombre, email, ocurridoEn);
    }
}
