package com.arquisoft.solicitudes.application.solicitud.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.message.constant.SolicitudesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record EnviarSolicitudNovedadAsesorCommand(
        UUID remitenteUsuario,
        UUID destinatarioUsuario,
        String mensajeSolicitud
) {
    public EnviarSolicitudNovedadAsesorCommand {
        mensajeSolicitud = UtilTexto.aplicarTrim(mensajeSolicitud);
    }

    public static EnviarSolicitudNovedadAsesorCommand crear(
            String remitenteUsuario, String destinatarioUsuario, String mensajeSolicitud) {
        var result = new ValidationResult();

        if (ValidatorTexto.noEnBlanco(destinatarioUsuario,
                SolicitudesFields.Solicitud.DESTINATARIO,
                SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(destinatarioUsuario,
                    SolicitudesFields.Solicitud.DESTINATARIO,
                    SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result);
        }

        if (ValidatorTexto.noEnBlanco(remitenteUsuario,
                SolicitudesFields.Solicitud.REMITENTE,
                SolicitudesCodes.Solicitud.REMITENTE_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(remitenteUsuario,
                    SolicitudesFields.Solicitud.REMITENTE,
                    SolicitudesCodes.Solicitud.REMITENTE_REQUERIDO, result);
        }

        if (ValidatorTexto.noEnBlanco(mensajeSolicitud,
                SolicitudesFields.Solicitud.MENSAJE,
                SolicitudesCodes.Solicitud.MENSAJE_REQUERIDO, result)) {
            ValidatorLongitud.longitudEntre(mensajeSolicitud,
                    SolicitudesLimits.Solicitud.MENSAJE_MIN, SolicitudesLimits.Solicitud.MENSAJE_MAX,
                    SolicitudesFields.Solicitud.MENSAJE,
                    SolicitudesCodes.Solicitud.MENSAJE_DEMASIADO_LARGO, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new EnviarSolicitudNovedadAsesorCommand(
                UtilUUID.generarUUIDDesdeTexto(remitenteUsuario),
                UtilUUID.generarUUIDDesdeTexto(destinatarioUsuario),
                mensajeSolicitud);
    }
}
