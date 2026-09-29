package com.arquisoft.solicitudes.application.solicitud.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.shared.validation.ValidatorUUID;

import java.util.UUID;

public record EliminarSolicitudNovedadCoordinadorCommand(
        UUID solicitud,
        UUID remitenteUsuario
) {
    public static EliminarSolicitudNovedadCoordinadorCommand crear(
            String solicitud, UUID remitenteUsuario) {
        var result = new ValidationResult();

        if (ValidatorTexto.noEnBlanco(solicitud,
                SolicitudesFields.Solicitud.ID,
                SolicitudesCodes.Solicitud.ID_REQUERIDO, result)) {
            ValidatorUUID.uuidValido(solicitud,
                    SolicitudesFields.Solicitud.ID,
                    SolicitudesCodes.Solicitud.ID_REQUERIDO, result);
        }

        ValidatorObjeto.noNulo(remitenteUsuario,
                SolicitudesFields.Solicitud.REMITENTE,
                SolicitudesCodes.Solicitud.REMITENTE_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new EliminarSolicitudNovedadCoordinadorCommand(
                UtilUUID.generarUUIDDesdeTexto(solicitud),
                remitenteUsuario);
    }
}
