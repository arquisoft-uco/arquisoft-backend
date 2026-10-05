package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudNovedadAsesorCommand;
import com.arquisoft.solicitudes.domain.destinatario.DestinatarioDomain;
import com.arquisoft.solicitudes.domain.remitente.RemitenteDomain;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudDomain;
import com.arquisoft.solicitudes.domain.solicitud.SolicitudDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class EnviarSolicitudNovedadAsesorMapper {

    private EnviarSolicitudNovedadAsesorMapper() {}

    public static EnvioSolicitudDomain toDomain(
            EnviarSolicitudNovedadAsesorCommand command) {
        var remitente = RemitenteDomain.crear(command.remitenteUsuario());
        var destinatario = DestinatarioDomain.crear(command.destinatarioUsuario());
        var solicitud = SolicitudDomain.crear(
                destinatario.getUsuario(), remitente.getUsuario(),
                command.mensajeSolicitud(), TipoSolicitud.NOVEDAD_PARA_EL_ASESOR);

        return EnvioSolicitudDomain.crear(solicitud, remitente, destinatario);
    }
}
