package com.arquisoft.solicitudes.domain.respuesta;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class EliminacionRespuestaNovedadAsesorDomain {

    private UUID solicitud;
    private UUID asesorUsuario;

    private EliminacionRespuestaNovedadAsesorDomain() {}

    public static EliminacionRespuestaNovedadAsesorDomain crear(UUID solicitud, UUID asesorUsuario) {
        var eliminacion = new EliminacionRespuestaNovedadAsesorDomain();
        var result = new ValidationResult();

        eliminacion.setSolicitud(solicitud, result);
        eliminacion.setAsesorUsuario(asesorUsuario, result);

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

    public UUID getAsesorUsuario() {
        return asesorUsuario;
    }
}
