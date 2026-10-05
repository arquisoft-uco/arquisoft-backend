package com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.Set;

public record ConsultarTiposSolicitudQuery(Set<String> tipos) {

    public static ConsultarTiposSolicitudQuery crear(Set<String> tipos) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(tipos,
                SolicitudesFields.TipoSolicitud.TIPOS,
                SolicitudesCodes.TipoSolicitud.TIPOS_REQUERIDOS, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarTiposSolicitudQuery(Set.copyOf(tipos));
    }
}
