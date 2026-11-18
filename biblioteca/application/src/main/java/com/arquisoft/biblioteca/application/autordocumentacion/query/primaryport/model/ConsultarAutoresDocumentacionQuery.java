package com.arquisoft.biblioteca.application.autordocumentacion.query.primaryport.model;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarAutoresDocumentacionQuery(UUID documentacion) {

    public static ConsultarAutoresDocumentacionQuery crear(UUID documentacion) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(documentacion,
                BibliotecaFields.AutorDocumentacion.DOCUMENTACION,
                BibliotecaCodes.AutorDocumentacion.DOCUMENTACION_ID_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarAutoresDocumentacionQuery(documentacion);
    }
}
