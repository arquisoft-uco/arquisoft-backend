package com.arquisoft.solicitudes.domain.respuesta;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.message.key.solicitudes.EstadoRespuestaKey;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;
import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

import java.util.UUID;

public final class ModificacionEstadoRespuestaDomain {

    private UUID solicitud;
    private UUID responsableUsuario;
    private EstadoRespuesta nuevoEstado;
    private TipoSolicitud tipoEsperado;

    private ModificacionEstadoRespuestaDomain() {}

    public static ModificacionEstadoRespuestaDomain crear(
            UUID solicitud, UUID responsableUsuario, String nuevoEstado, TipoSolicitud tipoEsperado) {
        var modificacion = new ModificacionEstadoRespuestaDomain();
        var result = new ValidationResult();

        modificacion.setSolicitud(solicitud, result);
        modificacion.setResponsableUsuario(responsableUsuario, result);
        modificacion.setNuevoEstado(nuevoEstado, result);
        modificacion.setTipoEsperado(tipoEsperado, result);

        result.lanzarSiTieneErrores();
        return modificacion;
    }

    private void setSolicitud(UUID solicitud, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(solicitud,
                SolicitudesFields.Solicitud.ID,
                SolicitudesCodes.Solicitud.ID_REQUERIDO, result)) {
            return;
        }
        this.solicitud = solicitud;
    }

    private void setResponsableUsuario(UUID responsableUsuario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(responsableUsuario,
                SolicitudesFields.Solicitud.DESTINATARIO,
                SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result)) {
            return;
        }
        this.responsableUsuario = responsableUsuario;
    }

    private void setNuevoEstado(String nuevoEstado, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(nuevoEstado);
        if (!ValidatorTexto.noEnBlanco(recortado,
                SolicitudesFields.Respuesta.ESTADO,
                SolicitudesCodes.Respuesta.ESTADO_REQUERIDO, result)) {
            return;
        }
        if (!EstadoRespuesta.esValido(recortado)) {
            result.agregarError(
                    SolicitudesFields.Respuesta.ESTADO,
                    SolicitudesCodes.EstadoRespuesta.ESTADO_NO_ENCONTRADO,
                    Mensajes.formatear(EstadoRespuestaKey.ERROR_ESTADO_RESPUESTA_NO_ENCONTRADO, recortado));
            return;
        }
        this.nuevoEstado = EstadoRespuesta.desde(recortado);
    }

    private void setTipoEsperado(TipoSolicitud tipoEsperado, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(tipoEsperado,
                SolicitudesFields.Solicitud.TIPO_SOLICITUD,
                SolicitudesCodes.Solicitud.TIPO_REQUERIDO, result)) {
            return;
        }
        this.tipoEsperado = tipoEsperado;
    }

    public UUID getSolicitud() {
        return solicitud;
    }

    public UUID getResponsableUsuario() {
        return responsableUsuario;
    }

    public EstadoRespuesta getNuevoEstado() {
        return nuevoEstado;
    }

    public TipoSolicitud getTipoEsperado() {
        return tipoEsperado;
    }
}
