package com.arquisoft.proyectos.application.coordinador.command.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record ActualizarCoordinadorCommand(
        UUID id,
        String identificador,
        String nombre,
        String email,
        Instant ocurridoEn
) {
    public ActualizarCoordinadorCommand {
        identificador = UtilTexto.aplicarTrim(identificador);
        nombre = UtilTexto.aplicarTrim(nombre);
        email = UtilTexto.aplicarTrim(email);
    }

    public static ActualizarCoordinadorCommand crear(
            String id, String identificador, String nombre, String email, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(id, ProyectosFields.Coordinador.ID, ProyectosCodes.Coordinador.ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(identificador, ProyectosFields.Coordinador.IDENTIFICADOR,
                ProyectosCodes.Coordinador.IDENTIFICADOR_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(nombre, ProyectosFields.Coordinador.NOMBRE,
                ProyectosCodes.Coordinador.NOMBRE_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(email, ProyectosFields.Coordinador.EMAIL,
                ProyectosCodes.Coordinador.EMAIL_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn, ProyectosFields.Coordinador.OCURRIDO_EN,
                ProyectosCodes.Coordinador.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ActualizarCoordinadorCommand(
                UtilUUID.generarUUIDDesdeTexto(id), identificador, nombre, email, ocurridoEn);
    }
}
