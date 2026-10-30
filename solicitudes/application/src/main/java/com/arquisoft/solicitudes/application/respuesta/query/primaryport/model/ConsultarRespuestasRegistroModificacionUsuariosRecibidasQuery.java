package com.arquisoft.solicitudes.application.respuesta.query.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarRespuestasRegistroModificacionUsuariosRecibidasQuery(
        UUID coordinadorUsuario,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarRespuestasRegistroModificacionUsuariosRecibidasQuery crear(
            UUID coordinadorUsuario, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(coordinadorUsuario,
                SolicitudesFields.Solicitud.REMITENTE,
                SolicitudesCodes.Solicitud.REMITENTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarRespuestasRegistroModificacionUsuariosRecibidasQuery(coordinadorUsuario, criterio);
    }
}
