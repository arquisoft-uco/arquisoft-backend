package com.arquisoft.fichas.domain.observacionitem.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public class ObservacionItemAgregadaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Fichas.OBSERVACION_ITEM_AGREGADA;
    public static final String EVENT_TYPE = "ObservacionItemAgregadaEvent";

    private final UUID observacionItemId;
    private final UUID revisionItemId;
    private final String observacion;
    private final String estadoObservacionRevisionId;
    private final String estadoObservacionRevisionNombre;

    public ObservacionItemAgregadaEvent(
            UUID observacionItemId,
            UUID revisionItemId,
            String observacion,
            String estadoObservacionRevisionId,
            String estadoObservacionRevisionNombre) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.observacionItemId = observacionItemId;
        this.revisionItemId = revisionItemId;
        this.observacion = observacion;
        this.estadoObservacionRevisionId = estadoObservacionRevisionId;
        this.estadoObservacionRevisionNombre = estadoObservacionRevisionNombre;
    }

    public UUID getObservacionItemId() {
        return observacionItemId;
    }

    public UUID getRevisionItemId() {
        return revisionItemId;
    }

    public String getObservacion() {
        return observacion;
    }

    public String getEstadoObservacionRevisionId() {
        return estadoObservacionRevisionId;
    }

    public String getEstadoObservacionRevisionNombre() {
        return estadoObservacionRevisionNombre;
    }
}
