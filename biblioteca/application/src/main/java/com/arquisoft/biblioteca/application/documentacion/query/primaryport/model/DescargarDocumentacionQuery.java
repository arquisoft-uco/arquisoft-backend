package com.arquisoft.biblioteca.application.documentacion.query.primaryport.model;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.UUID;

public record DescargarDocumentacionQuery(
        UUID documentacion,
        List<RolConsultaDocumentacion> roles
) {

    public static DescargarDocumentacionQuery crear(UUID documentacion, List<RolConsultaDocumentacion> roles) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(documentacion,
                BibliotecaFields.Documentacion.DOCUMENTACION,
                BibliotecaCodes.Documentacion.DOCUMENTACION_ID_REQUERIDO, result);
        ValidatorObjeto.noNulo(roles,
                BibliotecaFields.Documentacion.ROLES,
                BibliotecaCodes.Documentacion.ROLES_REQUERIDOS, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new DescargarDocumentacionQuery(documentacion, List.copyOf(roles));
    }
}
