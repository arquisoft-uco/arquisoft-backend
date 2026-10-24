package com.arquisoft.fichas.application.estadoficha.query.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.UUID;

public record ConsultarEstadosFichaQuery(
        UUID actor,
        List<String> rolesIdentidad
) {

    public static ConsultarEstadosFichaQuery crear(UUID actor, List<String> rolesIdentidad) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(actor,
                FichasFields.EstadoFicha.ACTOR,
                FichasCodes.EstadoFicha.ACTOR_REQUERIDO, result);
        ValidatorObjeto.noNulo(rolesIdentidad,
                FichasFields.EstadoFicha.ROLES,
                FichasCodes.EstadoFicha.ROLES_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarEstadosFichaQuery(actor, rolesIdentidad);
    }
}
