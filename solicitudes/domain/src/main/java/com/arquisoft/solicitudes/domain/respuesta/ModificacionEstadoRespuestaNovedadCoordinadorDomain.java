package com.arquisoft.solicitudes.domain.respuesta;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.shared.validation.ValidatorTexto;

import java.util.UUID;

public final class ModificacionEstadoRespuestaNovedadCoordinadorDomain {

    private UUID solicitud;
    private UUID coordinadorUsuario;
    private String nuevoEstado;

    private ModificacionEstadoRespuestaNovedadCoordinadorDomain() {}

    public static ModificacionEstadoRespuestaNovedadCoordinadorDomain crear(
            UUID solicitud, UUID coordinadorUsuario, String nuevoEstado) {
        var modificacion = new ModificacionEstadoRespuestaNovedadCoordinadorDomain();
        var result = new ValidationResult();

        modificacion.setSolicitud(solicitud, result);
        modificacion.setCoordinadorUsuario(coordinadorUsuario, result);
        modificacion.setNuevoEstado(nuevoEstado, result);

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

    private void setCoordinadorUsuario(UUID coordinadorUsuario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(coordinadorUsuario,
                SolicitudesFields.Solicitud.DESTINATARIO,
                SolicitudesCodes.Solicitud.DESTINATARIO_REQUERIDO, result)) {
            return;
        }
        this.coordinadorUsuario = coordinadorUsuario;
    }

    private void setNuevoEstado(String nuevoEstado, ValidationResult result) {
        if (!ValidatorTexto.noEnBlanco(nuevoEstado,
                SolicitudesFields.Respuesta.ESTADO,
                SolicitudesCodes.Respuesta.ESTADO_REQUERIDO, result)) {
            return;
        }
        this.nuevoEstado = nuevoEstado;
    }

    public UUID getSolicitud() {
        return solicitud;
    }

    public UUID getCoordinadorUsuario() {
        return coordinadorUsuario;
    }

    public String getNuevoEstado() {
        return nuevoEstado;
    }
}
