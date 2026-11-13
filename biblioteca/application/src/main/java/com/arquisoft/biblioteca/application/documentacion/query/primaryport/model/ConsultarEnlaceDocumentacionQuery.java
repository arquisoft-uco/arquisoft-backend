package com.arquisoft.biblioteca.application.documentacion.query.primaryport.model;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarEnlaceDocumentacionQuery(UUID documentacion) {

    public static ConsultarEnlaceDocumentacionQuery crear(UUID documentacion) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(documentacion,
                BibliotecaFields.Documentacion.DOCUMENTACION,
                BibliotecaCodes.Documentacion.DOCUMENTACION_ID_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEnlaceDocumentacionQuery(documentacion);
    }
}
