package com.arquisoft.fichas.domain.revisionitem.event;

import com.arquisoft.shared.events.DomainEvent;

import java.util.UUID;

public class RevisionItemModificadoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = "fichas.revision_item.modificado";
    public static final String EVENT_TYPE = "RevisionItemModificadoEvent";

    private final UUID itemId;
    private final String estadoRevisionId;
    private final String estadoRevisionNombre;

    public RevisionItemModificadoEvent(
            UUID itemId,
            String estadoRevisionId,
            String estadoRevisionNombre) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.itemId = itemId;
        this.estadoRevisionId = estadoRevisionId;
        this.estadoRevisionNombre = estadoRevisionNombre;
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
}
