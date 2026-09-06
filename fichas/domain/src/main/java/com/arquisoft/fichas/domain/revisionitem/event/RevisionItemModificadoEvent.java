package com.arquisoft.fichas.domain.revisionitem.event;

import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.List;
import java.util.UUID;

public class RevisionItemModificadoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Fichas.REVISION_ITEM_MODIFICADO;
    public static final String EVENT_TYPE = "RevisionItemModificadoEvent";

    private final UUID itemId;
    private final String estadoRevisionId;
    private final String estadoRevisionNombre;
    private final String tituloProyecto;
    private final List<ContactoEstudiante> estudiantes;

    public RevisionItemModificadoEvent(
            UUID itemId,
            String estadoRevisionId,
            String estadoRevisionNombre,
            String tituloProyecto,
            List<ContactoEstudiante> estudiantes) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.itemId = itemId;
        this.estadoRevisionId = estadoRevisionId;
        this.estadoRevisionNombre = estadoRevisionNombre;
        this.tituloProyecto = tituloProyecto;
        this.estudiantes = List.copyOf(estudiantes);
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

    public String getTituloProyecto() {
        return tituloProyecto;
    }

    public List<ContactoEstudiante> getEstudiantes() {
        return estudiantes;
    }
}
