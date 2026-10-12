package com.arquisoft.fichas.domain.estadofichaperfil.event;

import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.List;
import java.util.UUID;

public class EstadoFichaPerfilAgregadoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Fichas.ESTADO_FICHA_PERFIL_AGREGADO;
    public static final String EVENT_TYPE = "EstadoFichaPerfilAgregadoEvent";

    private final UUID estadoFichaPerfilId;
    private final UUID fichaPerfilId;
    private final String tituloProyecto;
    private final String estadoFicha;
    private final String estadoFichaNombre;
    private final List<ContactoEstudiante> estudiantes;

    public EstadoFichaPerfilAgregadoEvent(
            UUID estadoFichaPerfilId,
            UUID fichaPerfilId,
            String tituloProyecto,
            String estadoFicha,
            String estadoFichaNombre,
            List<ContactoEstudiante> estudiantes) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.estadoFichaPerfilId = estadoFichaPerfilId;
        this.fichaPerfilId = fichaPerfilId;
        this.tituloProyecto = tituloProyecto;
        this.estadoFicha = estadoFicha;
        this.estadoFichaNombre = estadoFichaNombre;
        this.estudiantes = List.copyOf(estudiantes);
    }

    public UUID getEstadoFichaPerfilId() {
        return estadoFichaPerfilId;
    }

    public UUID getFichaPerfilId() {
        return fichaPerfilId;
    }

    public String getTituloProyecto() {
        return tituloProyecto;
    }

    public String getEstadoFicha() {
        return estadoFicha;
    }

    public String getEstadoFichaNombre() {
        return estadoFichaNombre;
    }

    public List<ContactoEstudiante> getEstudiantes() {
        return estudiantes;
    }
}
