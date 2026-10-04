package com.arquisoft.solicitudes.domain.respuesta;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class EliminacionRespuestaNovedadCoordinadorDomain {

    private UUID solicitud;
    private UUID coordinadorUsuario;

    private EliminacionRespuestaNovedadCoordinadorDomain() {}

    public static EliminacionRespuestaNovedadCoordinadorDomain crear(UUID solicitud, UUID coordinadorUsuario) {
        var eliminacion = new EliminacionRespuestaNovedadCoordinadorDomain();
        var result = new ValidationResult();

        eliminacion.setSolicitud(solicitud, result);
        eliminacion.setCoordinadorUsuario(coordinadorUsuario, result);

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

    public UUID getCoordinadorUsuario() {
        return coordinadorUsuario;
    }
}
