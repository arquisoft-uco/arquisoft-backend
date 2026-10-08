package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudCambioAsesorCommand;
import com.arquisoft.solicitudes.domain.destinatario.DestinatarioDomain;
import com.arquisoft.solicitudes.domain.remitente.RemitenteDomain;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudDomain;
import com.arquisoft.solicitudes.domain.solicitud.SolicitudDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class EnviarSolicitudCambioAsesorMapper {

    private EnviarSolicitudCambioAsesorMapper() {}

    public static EnvioSolicitudDomain toDomain(
            EnviarSolicitudCambioAsesorCommand command) {
        var remitente = RemitenteDomain.crear(command.remitenteUsuario());
        var destinatario = DestinatarioDomain.crear(command.destinatarioUsuario());
        var solicitud = SolicitudDomain.crear(
                destinatario.getUsuario(), remitente.getUsuario(),
                command.mensajeSolicitud(), TipoSolicitud.CAMBIO_DE_ASESOR);

        return EnvioSolicitudDomain.crear(solicitud, remitente, destinatario);
    }
}
