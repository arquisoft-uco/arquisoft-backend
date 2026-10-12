package com.arquisoft.solicitudes.application.respuesta.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.message.constant.SolicitudesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record ResponderSolicitudNovedadCoordinadorCommand(
        UUID solicitud,
        String contenido,
        UUID coordinadorUsuario
) {
    public static ResponderSolicitudNovedadCoordinadorCommand crear(
            String solicitud, String contenido, UUID coordinadorUsuario) {
        var result = new ValidationResult();
        var contenidoRecortado = UtilTexto.aplicarTrim(contenido);

        if (ValidatorTexto.noEnBlanco(solicitud,
                SolicitudesFields.Solicitud.ID,
                SolicitudesCodes.Solicitud.ID_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(solicitud,
                    SolicitudesFields.Solicitud.ID,
                    SolicitudesCodes.Solicitud.ID_REQUERIDO, result);
        }

        if (ValidatorTexto.noEnBlanco(contenidoRecortado,
                SolicitudesFields.Respuesta.CONTENIDO,
                SolicitudesCodes.Respuesta.CONTENIDO_REQUERIDO, result)) {
            ValidatorLongitud.longitudEntre(contenidoRecortado,
                    SolicitudesLimits.Respuesta.CONTENIDO_MIN, SolicitudesLimits.Respuesta.CONTENIDO_MAX,
                    SolicitudesFields.Respuesta.CONTENIDO,
                    SolicitudesCodes.Respuesta.CONTENIDO_DEMASIADO_LARGO, result);
        }

        ValidatorObjeto.noNulo(coordinadorUsuario,
                SolicitudesFields.Solicitud.DESTINATARIO,
                SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ResponderSolicitudNovedadCoordinadorCommand(
                UtilUUID.generarUUIDDesdeTexto(solicitud),
                contenidoRecortado,
                coordinadorUsuario);
    }
}
