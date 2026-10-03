package com.arquisoft.solicitudes.application.solicitud.command.mapper;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.solicitudes.domain.solicitud.SolicitudDomain;
import com.arquisoft.solicitudes.domain.solicitud.event.SolicitudAmpliacionPlazoEnviadaEvent;
import com.arquisoft.solicitudes.domain.solicitud.event.SolicitudCambioAsesorEnviadaEvent;
import com.arquisoft.solicitudes.domain.solicitud.event.SolicitudNovedadAsesorEnviadaEvent;
import com.arquisoft.solicitudes.domain.solicitud.event.SolicitudNovedadCoordinadorEnviadaEvent;
import com.arquisoft.solicitudes.domain.tiposolicitud.exception.TipoSolicitudNoEncontradoException;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

public final class SolicitudEnviadaEventMapper {

    private SolicitudEnviadaEventMapper() {}

    public static DomainEvent toEvent(SolicitudDomain solicitud, UsuarioDomain remitente,
                                      UsuarioDomain destinatario) {
        var tipo = solicitud.getTipoSolicitud();
        return switch (tipo) {
            case NOVEDAD_PARA_EL_ASESOR -> new SolicitudNovedadAsesorEnviadaEvent(
                    solicitud.getId(), remitente.getNombre(), destinatario.getNombre(),
                    destinatario.getEmail(), solicitud.getMensajeSolicitud());
            case NOVEDAD_PARA_EL_COORDINADOR -> new SolicitudNovedadCoordinadorEnviadaEvent(
                    solicitud.getId(), remitente.getNombre(), destinatario.getNombre(),
                    destinatario.getEmail(), solicitud.getMensajeSolicitud());
            case CAMBIO_DE_ASESOR -> new SolicitudCambioAsesorEnviadaEvent(
                    solicitud.getId(), remitente.getNombre(), destinatario.getNombre(),
                    destinatario.getEmail(), solicitud.getMensajeSolicitud());
            case AMPLIACION_DE_PLAZO -> new SolicitudAmpliacionPlazoEnviadaEvent(
                    solicitud.getId(), remitente.getNombre(), destinatario.getNombre(),
                    destinatario.getEmail(), solicitud.getMensajeSolicitud());
            case REGISTRO_Y_MODIFICACION_DE_USUARIOS, VACIO ->
                    throw new TipoSolicitudNoEncontradoException(tipo.getId());
        };
    }
}
