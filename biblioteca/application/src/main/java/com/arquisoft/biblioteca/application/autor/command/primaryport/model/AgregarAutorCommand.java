package com.arquisoft.biblioteca.application.autor.command.primaryport.model;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.time.Instant;
import java.util.UUID;

public record AgregarAutorCommand(
        UUID id,
        String identificador,
        String nombre,
        String email,
        Instant ocurridoEn
) {
    public static AgregarAutorCommand crear(
            String id, String identificador, String nombre, String email, Instant ocurridoEn) {
        var result = new ValidationResult();

        ValidatorUUID.uuidValido(id, BibliotecaFields.Autor.ID,
                BibliotecaCodes.Autor.ID_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(identificador, BibliotecaFields.Autor.IDENTIFICADOR,
                BibliotecaCodes.Autor.IDENTIFICADOR_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(nombre, BibliotecaFields.Autor.NOMBRE,
                BibliotecaCodes.Autor.NOMBRE_REQUERIDO, result);
        ValidatorTexto.noEnBlanco(email, BibliotecaFields.Autor.EMAIL,
                BibliotecaCodes.Autor.EMAIL_REQUERIDO, result);
        ValidatorObjeto.noNulo(ocurridoEn, BibliotecaFields.Autor.OCURRIDO_EN,
                BibliotecaCodes.Autor.OCURRIDO_EN_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new AgregarAutorCommand(
                UtilUUID.generarUUIDDesdeTexto(id), identificador, nombre, email, ocurridoEn);
    }
}
