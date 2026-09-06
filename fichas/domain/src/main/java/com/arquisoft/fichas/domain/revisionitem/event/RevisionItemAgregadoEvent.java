package com.arquisoft.fichas.domain.revisionitem.event;

import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class RevisionItemAgregadoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Fichas.REVISION_ITEM_AGREGADO;
    public static final String EVENT_TYPE = "RevisionItemAgregadoEvent";

    private final UUID revisionItemId;
    private final UUID itemId;
    private final String estadoRevisionId;
    private final String estadoRevisionNombre;
    private final Instant fechaCreacion;
    private final String tituloProyecto;
    private final List<ContactoEstudiante> estudiantes;

    public RevisionItemAgregadoEvent(
            UUID revisionItemId,
            UUID itemId,
            String estadoRevisionId,
            String estadoRevisionNombre,
            Instant fechaCreacion,
            String tituloProyecto,
            List<ContactoEstudiante> estudiantes) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.revisionItemId = revisionItemId;
        this.itemId = itemId;
        this.estadoRevisionId = estadoRevisionId;
        this.estadoRevisionNombre = estadoRevisionNombre;
        this.fechaCreacion = fechaCreacion;
        this.tituloProyecto = tituloProyecto;
        this.estudiantes = List.copyOf(estudiantes);
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

    public String getTituloProyecto() {
        return tituloProyecto;
    }

    public List<ContactoEstudiante> getEstudiantes() {
        return estudiantes;
    }
}
