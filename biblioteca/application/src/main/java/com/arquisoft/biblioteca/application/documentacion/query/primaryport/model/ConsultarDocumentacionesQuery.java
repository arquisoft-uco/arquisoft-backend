package com.arquisoft.biblioteca.application.documentacion.query.primaryport.model;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;

public record ConsultarDocumentacionesQuery(
        List<RolConsultaDocumentacion> roles,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarDocumentacionesQuery crear(
            List<RolConsultaDocumentacion> roles, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(roles,
                BibliotecaFields.Documentacion.ROLES,
                BibliotecaCodes.Documentacion.ROLES_REQUERIDOS, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarDocumentacionesQuery(List.copyOf(roles), criterio);
    }
}
