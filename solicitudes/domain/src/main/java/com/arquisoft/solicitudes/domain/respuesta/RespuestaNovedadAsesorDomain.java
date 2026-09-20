package com.arquisoft.solicitudes.domain.respuesta;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class RespuestaNovedadAsesorDomain {

    private UUID solicitud;
    private String contenido;
    private UUID asesorUsuario;

    private RespuestaNovedadAsesorDomain() {}

    public static RespuestaNovedadAsesorDomain crear(
            UUID solicitud, String contenido, UUID asesorUsuario) {
        var respuesta = new RespuestaNovedadAsesorDomain();
        var result = new ValidationResult();

        respuesta.setSolicitud(solicitud, result);
        respuesta.setContenido(contenido, result);
        respuesta.setAsesorUsuario(asesorUsuario, result);

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
        if (!ValidatorTexto.noEnBlanco(contenido,
                SolicitudesFields.Respuesta.CONTENIDO,
                SolicitudesCodes.Respuesta.CONTENIDO_REQUERIDO, result)) {
            return;
        }
        this.contenido = contenido;
    }

    private void setAsesorUsuario(UUID asesorUsuario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(asesorUsuario,
                SolicitudesFields.Solicitud.DESTINATARIO,
                SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result)) {
            return;
        }
        this.asesorUsuario = asesorUsuario;
    }

    public UUID getSolicitud() {
        return solicitud;
    }

    public String getContenido() {
        return contenido;
    }

    public UUID getAsesorUsuario() {
        return asesorUsuario;
    }
}
