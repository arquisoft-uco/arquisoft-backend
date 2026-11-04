package com.arquisoft.proyectos.domain.estudianteproyectogrado.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public class EstudianteProyectoGradoDestituidoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Proyectos.ESTUDIANTE_PROYECTO_GRADO_DESTITUIDO;
    public static final String EVENT_TYPE = "EstudianteProyectoGradoDestituidoEvent";

    private final UUID proyectoGradoId;
    private final UUID estudianteId;

    public EstudianteProyectoGradoDestituidoEvent(UUID proyectoGradoId, UUID estudianteId) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.proyectoGradoId = proyectoGradoId;
        this.estudianteId = estudianteId;
    }

    public UUID getProyectoGradoId() {
        return proyectoGradoId;
    }

    public UUID getEstudianteId() {
        return estudianteId;
    }
}
