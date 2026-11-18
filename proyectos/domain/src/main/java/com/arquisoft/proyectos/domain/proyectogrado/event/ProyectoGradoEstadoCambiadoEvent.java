package com.arquisoft.proyectos.domain.proyectogrado.event;

import com.arquisoft.proyectos.domain.asesorproyectogrado.model.ContactoAsesor;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.List;
import java.util.UUID;

public class ProyectoGradoEstadoCambiadoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Proyectos.PROYECTO_GRADO_ESTADO_CAMBIADO;
    public static final String EVENT_TYPE = "ProyectoGradoEstadoCambiadoEvent";

    private final UUID proyectoGradoId;
    private final String tituloProyecto;
    private final String estadoAnterior;
    private final String estadoNuevo;
    private final String motivo;
    private final List<ContactoEstudiante> estudiantes;
    private final List<ContactoAsesor> asesores;

    public ProyectoGradoEstadoCambiadoEvent(
            UUID proyectoGradoId,
            String tituloProyecto,
            String estadoAnterior,
            String estadoNuevo,
            String motivo,
            List<ContactoEstudiante> estudiantes,
            List<ContactoAsesor> asesores) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.proyectoGradoId = proyectoGradoId;
        this.tituloProyecto = tituloProyecto;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.motivo = motivo;
        this.estudiantes = List.copyOf(estudiantes);
        this.asesores = List.copyOf(asesores);
    }

    public UUID getProyectoGradoId() {
        return proyectoGradoId;
    }

    public String getTituloProyecto() {
        return tituloProyecto;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public String getEstadoNuevo() {
        return estadoNuevo;
    }

    public String getMotivo() {
        return motivo;
    }

    public List<ContactoEstudiante> getEstudiantes() {
        return estudiantes;
    }

    public List<ContactoAsesor> getAsesores() {
        return asesores;
    }
}
