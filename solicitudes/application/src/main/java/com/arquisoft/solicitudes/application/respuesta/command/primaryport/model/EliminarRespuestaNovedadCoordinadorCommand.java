package com.arquisoft.solicitudes.application.respuesta.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record EliminarRespuestaNovedadCoordinadorCommand(
        UUID solicitud,
        UUID coordinadorUsuario
) {
    public static EliminarRespuestaNovedadCoordinadorCommand crear(
            String solicitud, String coordinadorUsuario) {
        var result = new ValidationResult();

        if (ValidatorTexto.noEnBlanco(solicitud,
                SolicitudesFields.Solicitud.ID,
                SolicitudesCodes.Solicitud.ID_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(solicitud,
                    SolicitudesFields.Solicitud.ID,
                    SolicitudesCodes.Solicitud.ID_REQUERIDO, result);
        }

        if (ValidatorTexto.noEnBlanco(coordinadorUsuario,
                SolicitudesFields.Solicitud.DESTINATARIO,
                SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(coordinadorUsuario,
                    SolicitudesFields.Solicitud.DESTINATARIO,
                    SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result);
        }

        result.lanzarSiTieneErroresDeEntrada();

        return new EliminarRespuestaNovedadCoordinadorCommand(
                UtilUUID.generarUUIDDesdeTexto(solicitud),
                UtilUUID.generarUUIDDesdeTexto(coordinadorUsuario));
    }
}
