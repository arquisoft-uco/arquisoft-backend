package com.arquisoft.solicitudes.domain.solicitud.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.solicitudes.domain.tiposolicitud.TipoSolicitud;
import com.arquisoft.solicitudes.domain.tiposolicitud.exception.TipoSolicitudNoEncontradoException;

import java.util.Map;
import java.util.UUID;

public final class SolicitudEnviadaEvent extends DomainEvent {

    private record IdentidadEvento(String tema, String tipoEvento) {}

    private static final Map<TipoSolicitud, IdentidadEvento> IDENTIDADES = Map.of(
            TipoSolicitud.NOVEDAD_PARA_EL_ASESOR, new IdentidadEvento(
                    EventTopics.Solicitudes.NOVEDAD_ASESOR_ENVIADA, "SolicitudNovedadAsesorEnviadaEvent"),
            TipoSolicitud.NOVEDAD_PARA_EL_COORDINADOR, new IdentidadEvento(
                    EventTopics.Solicitudes.NOVEDAD_COORDINADOR_ENVIADA, "SolicitudNovedadCoordinadorEnviadaEvent"),
            TipoSolicitud.CAMBIO_DE_ASESOR, new IdentidadEvento(
                    EventTopics.Solicitudes.CAMBIO_ASESOR_ENVIADA, "SolicitudCambioAsesorEnviadaEvent"),
            TipoSolicitud.AMPLIACION_DE_PLAZO, new IdentidadEvento(
                    EventTopics.Solicitudes.AMPLIACION_PLAZO_ENVIADA, "SolicitudAmpliacionPlazoEnviadaEvent"));

    private final UUID solicitudId;
    private final String remitenteNombre;
    private final String destinatarioNombre;
    private final String destinatarioEmail;
    private final String mensajeSolicitud;

    public SolicitudEnviadaEvent(TipoSolicitud tipoSolicitud, UUID solicitudId, String remitenteNombre,
                                 String destinatarioNombre, String destinatarioEmail,
                                 String mensajeSolicitud) {
        super(identidadDe(tipoSolicitud).tema(), identidadDe(tipoSolicitud).tipoEvento());
        this.solicitudId = solicitudId;
        this.remitenteNombre = remitenteNombre;
        this.destinatarioNombre = destinatarioNombre;
        this.destinatarioEmail = destinatarioEmail;
        this.mensajeSolicitud = mensajeSolicitud;
    }

    private static IdentidadEvento identidadDe(TipoSolicitud tipoSolicitud) {
        var identidad = IDENTIDADES.get(tipoSolicitud);
        if (UtilObjeto.esNulo(identidad)) {
            throw new TipoSolicitudNoEncontradoException(tipoSolicitud.getId());
        }
        return identidad;
    }

    public UUID getSolicitudId() {
        return solicitudId;
    }

    public String getRemitenteNombre() {
        return remitenteNombre;
    }

    public String getDestinatarioNombre() {
        return destinatarioNombre;
    }

    public String getDestinatarioEmail() {
        return destinatarioEmail;
    }

    public String getMensajeSolicitud() {
        return mensajeSolicitud;
    }
}
