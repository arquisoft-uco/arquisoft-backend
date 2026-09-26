package com.arquisoft.solicitudes.application.solicitud.query.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarSolicitudesNovedadCoordinadorRecibidasQuery(
        UUID coordinadorUsuario,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarSolicitudesNovedadCoordinadorRecibidasQuery crear(
            UUID coordinadorUsuario, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(coordinadorUsuario,
                SolicitudesFields.Solicitud.DESTINATARIO,
                SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarSolicitudesNovedadCoordinadorRecibidasQuery(coordinadorUsuario, criterio);
    }
}
