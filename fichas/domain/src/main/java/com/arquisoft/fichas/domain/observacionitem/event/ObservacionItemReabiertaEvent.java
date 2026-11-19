package com.arquisoft.fichas.domain.observacionitem.event;

import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.List;
import java.util.UUID;

public class ObservacionItemReabiertaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Fichas.OBSERVACION_ITEM_REABIERTA;
    public static final String EVENT_TYPE = "ObservacionItemReabiertaEvent";

    private final UUID observacionItemId;
    private final UUID revisionItemId;
    private final UUID fichaPerfilId;
    private final String tituloProyecto;
    private final List<ContactoEstudiante> estudiantes;

    public ObservacionItemReabiertaEvent(
            UUID observacionItemId,
            UUID revisionItemId,
            UUID fichaPerfilId,
            String tituloProyecto,
            List<ContactoEstudiante> estudiantes) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.observacionItemId = observacionItemId;
        this.revisionItemId = revisionItemId;
        this.fichaPerfilId = fichaPerfilId;
        this.tituloProyecto = tituloProyecto;
        this.estudiantes = List.copyOf(estudiantes);
    }

    public UUID getObservacionItemId() {
        return observacionItemId;
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

    public List<ContactoEstudiante> getEstudiantes() {
        return estudiantes;
    }
}
