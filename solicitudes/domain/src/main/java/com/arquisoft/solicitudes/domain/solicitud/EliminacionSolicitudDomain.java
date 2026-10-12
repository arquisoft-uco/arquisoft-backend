package com.arquisoft.solicitudes.domain.solicitud;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

import java.util.UUID;

public final class EliminacionSolicitudDomain {

    private UUID solicitud;
    private UUID remitenteUsuario;
    private TipoSolicitud tipoEsperado;

    private EliminacionSolicitudDomain() {}

    public static EliminacionSolicitudDomain crear(UUID solicitud, UUID remitenteUsuario,
                                                   TipoSolicitud tipoEsperado) {
        var eliminacion = new EliminacionSolicitudDomain();
        var result = new ValidationResult();

        eliminacion.setSolicitud(solicitud, result);
        eliminacion.setRemitenteUsuario(remitenteUsuario, result);
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

    private void setRemitenteUsuario(UUID remitenteUsuario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(remitenteUsuario,
                SolicitudesFields.Solicitud.REMITENTE,
                SolicitudesCodes.Solicitud.REMITENTE_REQUERIDO, result)) {
            return;
        }
        this.remitenteUsuario = remitenteUsuario;
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

    public UUID getRemitenteUsuario() {
        return remitenteUsuario;
    }

    public TipoSolicitud getTipoEsperado() {
        return tipoEsperado;
    }
}
