package com.arquisoft.proyectos.domain.estudianteproyectogrado.event;

import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.List;
import java.util.UUID;

public class EstudiantesProyectoGradoAsignadosEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Proyectos.ESTUDIANTES_PROYECTO_GRADO_ASIGNADOS;
    public static final String EVENT_TYPE = "EstudiantesProyectoGradoAsignadosEvent";

    private final UUID proyectoGradoId;
    private final String tituloProyecto;
    private final List<ContactoEstudiante> estudiantes;

    public EstudiantesProyectoGradoAsignadosEvent(
            UUID proyectoGradoId,
            String tituloProyecto,
            List<ContactoEstudiante> estudiantes) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.proyectoGradoId = proyectoGradoId;
        this.tituloProyecto = tituloProyecto;
        this.estudiantes = List.copyOf(estudiantes);
    }

    public UUID getProyectoGradoId() {
        return proyectoGradoId;
    }

    public String getTituloProyecto() {
        return tituloProyecto;
    }

    public List<ContactoEstudiante> getEstudiantes() {
        return estudiantes;
    }
}
