package com.arquisoft.solicitudes.domain.respuesta;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class RespuestaNovedadCoordinadorDomain {

    private RespuestaDomain respuesta;
    private UUID coordinadorUsuario;

    private RespuestaNovedadCoordinadorDomain() {}

    public static RespuestaNovedadCoordinadorDomain crear(
            RespuestaDomain respuesta, UUID coordinadorUsuario) {
        var accion = new RespuestaNovedadCoordinadorDomain();
        var result = new ValidationResult();

        accion.setRespuesta(respuesta, result);
        accion.setCoordinadorUsuario(coordinadorUsuario, result);

        result.lanzarSiTieneErrores();
        return accion;
    }

    private void setRespuesta(RespuestaDomain respuesta, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(respuesta,
                SolicitudesFields.Respuesta.SOLICITUD,
                SolicitudesCodes.Respuesta.SOLICITUD_REQUERIDO, result)) {
            return;
        }
        this.respuesta = respuesta;
    }

    private void setCoordinadorUsuario(UUID coordinadorUsuario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(coordinadorUsuario,
                SolicitudesFields.Solicitud.DESTINATARIO,
                SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result)) {
            return;
        }
        this.coordinadorUsuario = coordinadorUsuario;
    }

    public RespuestaDomain getRespuesta() {
        return respuesta;
    }

    public UUID getSolicitud() {
        return respuesta.getSolicitud();
    }

    public UUID getCoordinadorUsuario() {
        return coordinadorUsuario;
    }
}
