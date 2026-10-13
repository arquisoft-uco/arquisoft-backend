package com.arquisoft.solicitudes.domain.respuesta;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

import java.util.UUID;

public final class EliminacionRespuestaDomain {

    private UUID solicitud;
    private UUID responsableUsuario;
    private TipoSolicitud tipoEsperado;

    private EliminacionRespuestaDomain() {}

    public static EliminacionRespuestaDomain crear(
            UUID solicitud, UUID responsableUsuario, TipoSolicitud tipoEsperado) {
        var eliminacion = new EliminacionRespuestaDomain();
        var result = new ValidationResult();

        eliminacion.setSolicitud(solicitud, result);
        eliminacion.setResponsableUsuario(responsableUsuario, result);
        eliminacion.setTipoEsperado(tipoEsperado, result);

        result.lanzarSiTieneErrores();
        return eliminacion;
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

    public TipoSolicitud getTipoEsperado() {
        return tipoEsperado;
    }
}
