package com.arquisoft.proyectos.application.estudiante.command.primaryport.model;

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

public record ActualizarEstudianteCommand(
        UUID id,
        String identificador,
        String nombre,
        String email,
        Instant ocurridoEn
) {
    public ActualizarEstudianteCommand {
        identificador = UtilTexto.aplicarTrim(identificador);
        nombre = UtilTexto.aplicarTrim(nombre);
        email = UtilTexto.aplicarTrim(email);
    }

    public static ActualizarEstudianteCommand crear(
            String id, String identificador, String nombre, String email, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(id, ProyectosFields.Estudiante.ID, ProyectosCodes.Estudiante.ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(identificador, ProyectosFields.Estudiante.IDENTIFICADOR,
                ProyectosCodes.Estudiante.IDENTIFICADOR_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(nombre, ProyectosFields.Estudiante.NOMBRE,
                ProyectosCodes.Estudiante.NOMBRE_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(email, ProyectosFields.Estudiante.EMAIL,
                ProyectosCodes.Estudiante.EMAIL_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn, ProyectosFields.Estudiante.OCURRIDO_EN,
                ProyectosCodes.Estudiante.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ActualizarEstudianteCommand(
                UtilUUID.generarUUIDDesdeTexto(id), identificador, nombre, email, ocurridoEn);
    }
}
