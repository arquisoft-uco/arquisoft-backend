package com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.model;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record AgregarBibliotecarioCommand(
        UUID id,
        String identificador,
        String nombre,
        String email,
        Instant ocurridoEn
) {
    public static AgregarBibliotecarioCommand crear(
            String id, String identificador, String nombre, String email, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(id, BibliotecaFields.Bibliotecario.ID,
                BibliotecaCodes.Bibliotecario.ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(identificador, BibliotecaFields.Bibliotecario.IDENTIFICADOR,
                BibliotecaCodes.Bibliotecario.IDENTIFICADOR_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(nombre, BibliotecaFields.Bibliotecario.NOMBRE,
                BibliotecaCodes.Bibliotecario.NOMBRE_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(email, BibliotecaFields.Bibliotecario.EMAIL,
                BibliotecaCodes.Bibliotecario.EMAIL_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn, BibliotecaFields.Bibliotecario.OCURRIDO_EN,
                BibliotecaCodes.Bibliotecario.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarBibliotecarioCommand(
                UtilUUID.generarUUIDDesdeTexto(id), identificador, nombre, email, ocurridoEn);
    }
}
