package com.arquisoft.fichas.domain.revisionitem.event;

import com.arquisoft.fichas.domain.asesorficha.model.ContactoAsesor;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public class CorreccionesRevisionItemEnviadasEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Fichas.REVISION_ITEM_CORRECCIONES_ENVIADAS;
    public static final String EVENT_TYPE = "CorreccionesRevisionItemEnviadasEvent";

    private final UUID revisionItemId;
    private final UUID fichaPerfilId;
    private final String tituloProyecto;
    private final ContactoAsesor asesor;

    public CorreccionesRevisionItemEnviadasEvent(
            UUID revisionItemId,
            UUID fichaPerfilId,
            String tituloProyecto,
            ContactoAsesor asesor) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.revisionItemId = revisionItemId;
        this.fichaPerfilId = fichaPerfilId;
        this.tituloProyecto = tituloProyecto;
        this.asesor = asesor;
    }

    public UUID getRevisionItemId() {
        return revisionItemId;
    }

    public UUID getFichaPerfilId() {
        return fichaPerfilId;
    }

    public String getTituloProyecto() {
        return tituloProyecto;
    }

    public ContactoAsesor getAsesor() {
        return asesor;
    }
}
