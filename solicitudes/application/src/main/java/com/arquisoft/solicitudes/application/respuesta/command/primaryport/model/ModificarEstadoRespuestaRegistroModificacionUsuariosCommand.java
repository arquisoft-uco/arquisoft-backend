package com.arquisoft.solicitudes.application.respuesta.command.primaryport.model;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public record ModificarEstadoRespuestaRegistroModificacionUsuariosCommand(
        UUID solicitud,
        String nuevoEstado,
        UUID administradorUsuario
) {
    public static ModificarEstadoRespuestaRegistroModificacionUsuariosCommand crear(
            UUID solicitud, String nuevoEstado, UUID administradorUsuario) {
        var result = new ValidationResult();

        ValidatorObjeto.noNulo(solicitud,
                SolicitudesFields.Solicitud.ID,
                SolicitudesCodes.Solicitud.ID_REQUERIDO, result);

        ValidatorTexto.noEnBlanco(nuevoEstado,
                SolicitudesFields.Respuesta.ESTADO,
                SolicitudesCodes.Respuesta.ESTADO_REQUERIDO, result);

        ValidatorObjeto.noNulo(administradorUsuario,
                SolicitudesFields.Solicitud.DESTINATARIO,
                SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result);

        result.lanzarSiTieneErroresDeEntrada();

        return new ModificarEstadoRespuestaRegistroModificacionUsuariosCommand(
                solicitud, nuevoEstado, administradorUsuario);
    }
}
