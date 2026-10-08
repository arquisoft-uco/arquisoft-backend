package com.arquisoft.solicitudes.domain.respuesta;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

import java.util.UUID;

public final class RespuestaSolicitudDomain {

    private RespuestaDomain respuesta;
    private UUID responsableUsuario;
    private TipoSolicitud tipoEsperado;

    private RespuestaSolicitudDomain() {}

    public static RespuestaSolicitudDomain crear(RespuestaDomain respuesta, UUID responsableUsuario,
                                                 TipoSolicitud tipoEsperado) {
        var accion = new RespuestaSolicitudDomain();
        var result = new ValidationResult();

        accion.setRespuesta(respuesta, result);
        accion.setResponsableUsuario(responsableUsuario, result);
        accion.setTipoEsperado(tipoEsperado, result);

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

    private void setResponsableUsuario(UUID responsableUsuario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(responsableUsuario,
                SolicitudesFields.Solicitud.DESTINATARIO,
                SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result)) {
            return;
        }
        this.responsableUsuario = responsableUsuario;
    }

    private void setTipoEsperado(TipoSolicitud tipoEsperado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(tipoEsperado,
                SolicitudesFields.Solicitud.TIPO_SOLICITUD,
                SolicitudesCodes.Solicitud.TIPO_REQUERIDO, result)) {
            return;
        }
        this.tipoEsperado = tipoEsperado;
    }

    public RespuestaDomain getRespuesta() {
        return respuesta;
    }

    public UUID getSolicitud() {
        return respuesta.getSolicitud();
    }

    public UUID getResponsableUsuario() {
        return responsableUsuario;
    }

    public TipoSolicitud getTipoEsperado() {
        return tipoEsperado;
    }
}
