package com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.List;
import java.util.UUID;

public record ConsultarTiposSolicitudParaRecibirQuery(UUID actor, List<String> rolesIdentidad) {

    public static ConsultarTiposSolicitudParaRecibirQuery crear(UUID actor, List<String> rolesIdentidad) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(actor,
                SolicitudesFields.TipoSolicitud.ACTOR,
                SolicitudesCodes.TipoSolicitud.ACTOR_REQUERIDO, result);
        ValidatorObjeto.noNulo(rolesIdentidad,
                SolicitudesFields.TipoSolicitud.ROLES,
                SolicitudesCodes.TipoSolicitud.ROLES_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarTiposSolicitudParaRecibirQuery(actor, rolesIdentidad);
    }
}
