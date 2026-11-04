package com.arquisoft.solicitudes.application.respuesta.query.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public record ConsultarRespuestasRegistroModificacionUsuariosEnviadasQuery(
        UUID administradorUsuario,
        ConsultaCriteriaQuery criterio
) {

    public static ConsultarRespuestasRegistroModificacionUsuariosEnviadasQuery crear(
            UUID administradorUsuario, ConsultaCriteriaQuery criterio) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(administradorUsuario,
                SolicitudesFields.Solicitud.DESTINATARIO,
                SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ConsultarRespuestasRegistroModificacionUsuariosEnviadasQuery(administradorUsuario, criterio);
    }
}
