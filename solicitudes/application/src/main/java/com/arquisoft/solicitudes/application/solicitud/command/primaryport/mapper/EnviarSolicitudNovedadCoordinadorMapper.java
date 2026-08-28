package com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.domain.destinatario.DestinatarioDomain;
import com.arquisoft.solicitudes.domain.remitente.RemitenteDomain;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudNovedadCoordinadorDomain;
import com.arquisoft.solicitudes.domain.solicitud.SolicitudDomain;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;

public final class EnviarSolicitudNovedadCoordinadorMapper {

    private EnviarSolicitudNovedadCoordinadorMapper() {}

    public static EnvioSolicitudNovedadCoordinadorDomain toDomain(
            EnviarSolicitudNovedadCoordinadorCommand command) {
        var remitente = RemitenteDomain.crear(command.remitenteUsuario());
        var destinatario = DestinatarioDomain.crear(command.destinatarioUsuario());
        var solicitud = SolicitudDomain.crear(
                destinatario.getId(), remitente.getId(),
                command.mensajeSolicitud(), TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR);

        return EnvioSolicitudNovedadCoordinadorDomain.crear(solicitud, remitente, destinatario);
    }
}
