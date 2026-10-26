package com.arquisoft.proyectos.domain.asesorproyectogrado.event;

import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ContactoAsesor;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public class AsesorProyectoGradoAsignadoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Proyectos.ASESOR_PROYECTO_GRADO_ASIGNADO;
    public static final String EVENT_TYPE = "AsesorProyectoGradoAsignadoEvent";

    private final UUID proyectoGradoId;
    private final String tituloProyecto;
    private final UUID asesorId;
    private final ContactoAsesor asesor;

    public AsesorProyectoGradoAsignadoEvent(
            UUID proyectoGradoId,
            String tituloProyecto,
            UUID asesorId,
            ContactoAsesor asesor) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.proyectoGradoId = proyectoGradoId;
        this.tituloProyecto = tituloProyecto;
        this.asesorId = asesorId;
        this.asesor = asesor;
    }

    public UUID getProyectoGradoId() {
        return proyectoGradoId;
    }

    public String getTituloProyecto() {
        return tituloProyecto;
    }

    public UUID getAsesorId() {
        return asesorId;
    }

    public ContactoAsesor getAsesor() {
        return asesor;
    }
}
