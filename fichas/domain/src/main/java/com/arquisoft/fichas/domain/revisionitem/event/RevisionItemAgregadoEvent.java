package com.arquisoft.fichas.domain.revisionitem.event;

import com.arquisoft.shared.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class RevisionItemAgregadoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = "fichas.revision_item.agregado";
    public static final String EVENT_TYPE = "RevisionItemAgregadoEvent";

    private final UUID revisionItemId;
    private final UUID itemId;
    private final String estadoRevisionId;
    private final String estadoRevisionNombre;
    private final Instant fechaCreacion;

    public RevisionItemAgregadoEvent(
            UUID revisionItemId,
            UUID itemId,
            String estadoRevisionId,
            String estadoRevisionNombre,
            Instant fechaCreacion) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.revisionItemId = revisionItemId;
        this.itemId = itemId;
        this.estadoRevisionId = estadoRevisionId;
        this.estadoRevisionNombre = estadoRevisionNombre;
        this.fechaCreacion = fechaCreacion;
    }

    public UUID getRevisionItemId() {
        return revisionItemId;
    }

    public UUID getItemId() {
        return itemId;
    }

    public String getEstadoRevisionId() {
        return estadoRevisionId;
    }

    public String getEstadoRevisionNombre() {
        return estadoRevisionNombre;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }
}
