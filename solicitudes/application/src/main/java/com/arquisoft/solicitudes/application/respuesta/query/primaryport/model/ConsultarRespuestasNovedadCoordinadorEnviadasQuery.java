package com.arquisoft.solicitudes.application.respuesta.query.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarRespuestasNovedadCoordinadorEnviadasQuery(
        UUID coordinadorUsuario,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarRespuestasNovedadCoordinadorEnviadasQuery crear(
            UUID coordinadorUsuario, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(coordinadorUsuario,
                SolicitudesFields.Solicitud.DESTINATARIO,
                SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarRespuestasNovedadCoordinadorEnviadasQuery(coordinadorUsuario, criterio);
    }
}
