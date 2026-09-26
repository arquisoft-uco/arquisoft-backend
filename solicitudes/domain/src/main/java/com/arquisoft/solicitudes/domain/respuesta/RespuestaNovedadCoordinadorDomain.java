package com.arquisoft.solicitudes.domain.respuesta;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class RespuestaNovedadCoordinadorDomain {

    private UUID solicitud;
    private String contenido;
    private UUID coordinadorUsuario;

    private RespuestaNovedadCoordinadorDomain() {}

    public static RespuestaNovedadCoordinadorDomain crear(
            UUID solicitud, String contenido, UUID coordinadorUsuario) {
        var respuesta = new RespuestaNovedadCoordinadorDomain();
        var result = new ValidationResult();

        respuesta.setSolicitud(solicitud, result);
        respuesta.setContenido(contenido, result);
        respuesta.setCoordinadorUsuario(coordinadorUsuario, result);

        result.lanzarSiTieneErrores();
        return respuesta;
    }

    private void setSolicitud(UUID solicitud, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(solicitud,
                SolicitudesFields.Respuesta.SOLICITUD,
                SolicitudesCodes.Respuesta.SOLICITUD_REQUERIDO, result)) {
            return;
        }
        this.solicitud = solicitud;
    }

    private void setContenido(String contenido, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(contenido);
        if (!ValidatorTexto.noEnBlanco(recortado,
                SolicitudesFields.Respuesta.CONTENIDO,
                SolicitudesCodes.Respuesta.CONTENIDO_REQUERIDO, result)) {
            return;
        }
        this.contenido = recortado;
    }

    private void setCoordinadorUsuario(UUID coordinadorUsuario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(coordinadorUsuario,
                SolicitudesFields.Solicitud.DESTINATARIO,
                SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result)) {
            return;
        }
        this.coordinadorUsuario = coordinadorUsuario;
    }

    public UUID getSolicitud() {
        return solicitud;
    }

    public String getContenido() {
        return contenido;
    }

    public UUID getCoordinadorUsuario() {
        return coordinadorUsuario;
    }
}
