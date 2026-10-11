package com.arquisoft.proyectos.domain.proyectogrado.event;

import com.arquisoft.proyectos.domain.coordinador.model.ContactoCoordinador;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public class ProyectoGradoRegistradoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Proyectos.PROYECTO_GRADO_REGISTRADO;
    public static final String EVENT_TYPE = "ProyectoGradoRegistradoEvent";

    private final UUID proyectoGradoId;
    private final UUID fichaPerfilId;
    private final String tituloProyecto;
    private final ContactoCoordinador coordinador;

    public ProyectoGradoRegistradoEvent(
            UUID proyectoGradoId,
            UUID fichaPerfilId,
            String tituloProyecto,
            ContactoCoordinador coordinador) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.proyectoGradoId = proyectoGradoId;
        this.fichaPerfilId = fichaPerfilId;
        this.tituloProyecto = tituloProyecto;
        this.coordinador = coordinador;
    }

    public UUID getProyectoGradoId() {
        return proyectoGradoId;
    }

    public UUID getFichaPerfilId() {
        return fichaPerfilId;
    }

    public String getTituloProyecto() {
        return tituloProyecto;
    }

    public ContactoCoordinador getCoordinador() {
        return coordinador;
    }
}
