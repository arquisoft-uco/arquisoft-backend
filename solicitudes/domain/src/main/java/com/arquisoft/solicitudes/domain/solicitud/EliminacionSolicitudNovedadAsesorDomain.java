package com.arquisoft.solicitudes.domain.solicitud;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;

import java.util.UUID;

public final class EliminacionSolicitudNovedadAsesorDomain {

    private UUID solicitud;
    private UUID remitenteUsuario;

    private EliminacionSolicitudNovedadAsesorDomain() {}

    public static EliminacionSolicitudNovedadAsesorDomain crear(UUID solicitud, UUID remitenteUsuario) {
        var eliminacion = new EliminacionSolicitudNovedadAsesorDomain();
        var result = new ValidationResult();

        eliminacion.setSolicitud(solicitud, result);
        eliminacion.setRemitenteUsuario(remitenteUsuario, result);

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

    private void setRemitenteUsuario(UUID remitenteUsuario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(remitenteUsuario,
                SolicitudesFields.Solicitud.REMITENTE,
                SolicitudesCodes.Solicitud.REMITENTE_REQUERIDO, result)) {
            return;
        }
        this.remitenteUsuario = remitenteUsuario;
    }

    public UUID getSolicitud() {
        return solicitud;
    }

    public UUID getRemitenteUsuario() {
        return remitenteUsuario;
    }
}
