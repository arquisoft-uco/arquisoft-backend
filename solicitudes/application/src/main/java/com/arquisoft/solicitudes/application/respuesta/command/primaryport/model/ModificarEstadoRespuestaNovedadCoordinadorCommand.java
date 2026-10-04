package com.arquisoft.solicitudes.application.respuesta.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record ModificarEstadoRespuestaNovedadCoordinadorCommand(
        UUID solicitud,
        String nuevoEstado,
        UUID coordinadorUsuario
) {
    public static ModificarEstadoRespuestaNovedadCoordinadorCommand crear(
            String solicitud, String nuevoEstado, UUID coordinadorUsuario) {
        var result = new ValidationResult();

        if (ValidatorTexto.noEnBlanco(solicitud,
                SolicitudesFields.Solicitud.ID,
                SolicitudesCodes.Solicitud.ID_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(solicitud,
                    SolicitudesFields.Solicitud.ID,
                    SolicitudesCodes.Solicitud.ID_REQUERIDO, result);
        }

        ValidatorTexto.noEnBlanco(nuevoEstado,
                SolicitudesFields.Respuesta.ESTADO,
                SolicitudesCodes.Respuesta.ESTADO_REQUERIDO, result);

        ValidatorObjeto.noNulo(coordinadorUsuario,
                SolicitudesFields.Solicitud.DESTINATARIO,
                SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ModificarEstadoRespuestaNovedadCoordinadorCommand(
                UtilUUID.generarUUIDDesdeTexto(solicitud), nuevoEstado, coordinadorUsuario);
    }
}
