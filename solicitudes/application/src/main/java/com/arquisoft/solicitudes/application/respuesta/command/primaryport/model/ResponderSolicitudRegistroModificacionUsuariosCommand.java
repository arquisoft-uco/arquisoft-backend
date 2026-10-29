package com.arquisoft.solicitudes.application.respuesta.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.message.constant.SolicitudesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record ResponderSolicitudRegistroModificacionUsuariosCommand(
        UUID solicitud,
        String contenido,
        UUID administradorUsuario
) {
    public static ResponderSolicitudRegistroModificacionUsuariosCommand crear(
            UUID solicitud, String contenido, UUID administradorUsuario) {
        var result = new ValidationResult();
        var contenidoRecortado = UtilTexto.aplicarTrim(contenido);

        ValidatorObjeto.noNulo(solicitud,
                SolicitudesFields.Solicitud.ID,
                SolicitudesCodes.Solicitud.ID_REQUERIDO, result);

        if (ValidatorTexto.noEnBlanco(contenidoRecortado,
                SolicitudesFields.Respuesta.CONTENIDO,
                SolicitudesCodes.Respuesta.CONTENIDO_REQUERIDO, result)) {
            ValidatorLongitud.longitudEntre(contenidoRecortado,
                    SolicitudesLimits.Respuesta.CONTENIDO_MIN, SolicitudesLimits.Respuesta.CONTENIDO_MAX,
                    SolicitudesFields.Respuesta.CONTENIDO,
                    SolicitudesCodes.Respuesta.CONTENIDO_DEMASIADO_LARGO, result);
        }

        ValidatorObjeto.noNulo(administradorUsuario,
                SolicitudesFields.Solicitud.DESTINATARIO,
                SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ResponderSolicitudRegistroModificacionUsuariosCommand(
                solicitud,
                contenidoRecortado,
                administradorUsuario);
    }
}
