package com.arquisoft.fichas.application.estadoficha.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;

public record ConsultarEstadosFichaQuery(
        List<String> roles
) {

    public static ConsultarEstadosFichaQuery crear(List<String> roles) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(roles,
                FichasFields.EstadoFicha.ROLES,
                FichasCodes.EstadoFicha.ROLES_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEstadosFichaQuery(roles);
    }
}
