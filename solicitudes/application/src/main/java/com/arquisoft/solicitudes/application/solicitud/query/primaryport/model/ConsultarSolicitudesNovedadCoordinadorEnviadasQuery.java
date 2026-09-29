package com.arquisoft.solicitudes.application.solicitud.query.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarSolicitudesNovedadCoordinadorEnviadasQuery(
        UUID estudianteUsuario,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarSolicitudesNovedadCoordinadorEnviadasQuery crear(
            UUID estudianteUsuario, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(estudianteUsuario,
                SolicitudesFields.Solicitud.REMITENTE,
                SolicitudesCodes.Solicitud.REMITENTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarSolicitudesNovedadCoordinadorEnviadasQuery(estudianteUsuario, criterio);
    }
}
