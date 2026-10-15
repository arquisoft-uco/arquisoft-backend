package com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.model;

import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record ActualizarAdministradorCommand(
        UUID id,
        String identificador,
        String nombre,
        String email,
        Instant ocurridoEn
) {
    public ActualizarAdministradorCommand {
        identificador = UtilTexto.aplicarTrim(identificador);
        nombre = UtilTexto.aplicarTrim(nombre);
        email = UtilTexto.aplicarTrim(email);
    }

    public static ActualizarAdministradorCommand crear(
            String id, String identificador, String nombre, String email, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(id, RepositorioArtefactosFields.Administrador.ID, RepositorioArtefactosCodes.Administrador.ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(identificador, RepositorioArtefactosFields.Administrador.IDENTIFICADOR,
                RepositorioArtefactosCodes.Administrador.IDENTIFICADOR_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(nombre, RepositorioArtefactosFields.Administrador.NOMBRE,
                RepositorioArtefactosCodes.Administrador.NOMBRE_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(email, RepositorioArtefactosFields.Administrador.EMAIL,
                RepositorioArtefactosCodes.Administrador.EMAIL_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn, RepositorioArtefactosFields.Administrador.OCURRIDO_EN,
                RepositorioArtefactosCodes.Administrador.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ActualizarAdministradorCommand(
                UtilUUID.generarUUIDDesdeTexto(id), identificador, nombre, email, ocurridoEn);
    }
}
