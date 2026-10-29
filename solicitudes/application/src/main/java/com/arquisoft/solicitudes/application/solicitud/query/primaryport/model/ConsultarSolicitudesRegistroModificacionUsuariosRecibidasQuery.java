package com.arquisoft.solicitudes.application.solicitud.query.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarSolicitudesRegistroModificacionUsuariosRecibidasQuery(
        UUID administradorUsuario,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarSolicitudesRegistroModificacionUsuariosRecibidasQuery crear(
            UUID administradorUsuario, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(administradorUsuario,
                SolicitudesFields.Solicitud.DESTINATARIO,
                SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarSolicitudesRegistroModificacionUsuariosRecibidasQuery(administradorUsuario, criterio);
    }
}
