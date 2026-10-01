package com.arquisoft.fichas.domain.estudiantefichaperfil.event;

import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.List;
import java.util.UUID;

public class EstudiantesFichaPerfilAsignadosEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Fichas.ESTUDIANTES_FICHA_PERFIL_ASIGNADOS;
    public static final String EVENT_TYPE = "EstudiantesFichaPerfilAsignadosEvent";

    private final UUID fichaPerfilId;
    private final String tituloProyecto;
    private final List<ContactoEstudiante> estudiantes;

    public EstudiantesFichaPerfilAsignadosEvent(
            UUID fichaPerfilId,
            String tituloProyecto,
            List<ContactoEstudiante> estudiantes) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.fichaPerfilId = fichaPerfilId;
        this.tituloProyecto = tituloProyecto;
        this.estudiantes = List.copyOf(estudiantes);
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
