package com.arquisoft.solicitudes.application.respuesta.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.message.constant.SolicitudesLimits;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record ResponderSolicitudNovedadCoordinadorCommand(
        UUID solicitud,
        String contenido,
        UUID coordinadorUsuario
) {
    public static ResponderSolicitudNovedadCoordinadorCommand crear(
            String solicitud, String contenido, String coordinadorUsuario) {
        var result = new ValidationResult();

        if (ValidatorTexto.noEnBlanco(solicitud,
                SolicitudesFields.Solicitud.ID,
                SolicitudesCodes.Solicitud.ID_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(solicitud,
                    SolicitudesFields.Solicitud.ID,
                    SolicitudesCodes.Solicitud.ID_REQUERIDO, result);
        }

        if (ValidatorTexto.noEnBlanco(contenido,
                SolicitudesFields.Respuesta.CONTENIDO,
                SolicitudesCodes.Respuesta.CONTENIDO_REQUERIDO, result)) {
            ValidatorLongitud.longitudEntre(contenido,
                    SolicitudesLimits.Respuesta.CONTENIDO_MIN, SolicitudesLimits.Respuesta.CONTENIDO_MAX,
                    SolicitudesFields.Respuesta.CONTENIDO,
                    SolicitudesCodes.Respuesta.CONTENIDO_DEMASIADO_LARGO, result);
        }

        if (ValidatorTexto.noEnBlanco(coordinadorUsuario,
                SolicitudesFields.Solicitud.DESTINATARIO,
                SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(coordinadorUsuario,
                    SolicitudesFields.Solicitud.DESTINATARIO,
                    SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new ResponderSolicitudNovedadCoordinadorCommand(
                UtilUUID.generarUUIDDesdeTexto(solicitud),
                contenido,
                UtilUUID.generarUUIDDesdeTexto(coordinadorUsuario));
    }
}
