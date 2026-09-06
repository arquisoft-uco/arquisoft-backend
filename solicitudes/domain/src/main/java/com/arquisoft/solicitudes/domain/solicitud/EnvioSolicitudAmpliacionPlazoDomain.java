package com.arquisoft.solicitudes.domain.solicitud;

import com.arquisoft.shared.message.constant.SolicitudesCodes;
import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorObjeto;
import com.arquisoft.solicitudes.domain.destinatario.DestinatarioDomain;
import com.arquisoft.solicitudes.domain.remitente.RemitenteDomain;

import java.util.UUID;

public final class EnvioSolicitudAmpliacionPlazoDomain {

    private SolicitudDomain solicitud;
    private RemitenteDomain remitente;
    private DestinatarioDomain destinatario;

    private EnvioSolicitudAmpliacionPlazoDomain() {}

    public static EnvioSolicitudAmpliacionPlazoDomain crear(SolicitudDomain solicitud,
                                                            RemitenteDomain remitente,
                                                            DestinatarioDomain destinatario) {
        var envio = new EnvioSolicitudAmpliacionPlazoDomain();
        var result = new ValidationResult();

        envio.setSolicitud(solicitud, result);
        envio.setRemitente(remitente, result);
        envio.setDestinatario(destinatario, result);

        result.lanzarSiTieneErrores();
        return envio;
    }

    private void setSolicitud(SolicitudDomain solicitud, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(solicitud,
                SolicitudesFields.Solicitud.ID,
                SolicitudesCodes.Solicitud.ID_REQUERIDO, result)) {
            return;
        }
        this.solicitud = solicitud;
    }

    private void setRemitente(RemitenteDomain remitente, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(remitente,
                SolicitudesFields.Remitente.REMITENTE,
                SolicitudesCodes.Remitente.REMITENTE_REQUERIDO, result)) {
            return;
        }
        this.remitente = remitente;
    }

    private void setDestinatario(DestinatarioDomain destinatario, ValidationResult result) {
        if (!ValidatorObjeto.noNulo(destinatario,
                SolicitudesFields.Destinatario.DESTINATARIO,
                SolicitudesCodes.Destinatario.DESTINATARIO_REQUERIDO, result)) {
            return;
        }
        this.destinatario = destinatario;
    }

    public SolicitudDomain getSolicitud() {
        return solicitud;
    }

    public RemitenteDomain getRemitente() {
        return remitente;
    }

    public DestinatarioDomain getDestinatario() {
        return destinatario;
    }

    public UUID getRemitenteUsuario() {
        return remitente.getUsuario();
    }

    public UUID getDestinatarioUsuario() {
        return destinatario.getUsuario();
    }
}
