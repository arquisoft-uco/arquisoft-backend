package com.arquisoft.entregables.domain.entregableproyectogrado.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public final class EntregableProyectoGradoDisponibleEvaluacionEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_DISPONIBLE_EVALUACION;
    private static final String EVENT_TYPE = "EntregableProyectoGradoDisponibleEvaluacionEvent";

    private final UUID entregableProyectoGrado;
    private final UUID proyectoGrado;
    private final String nombreProyecto;
    private final int version;

    public EntregableProyectoGradoDisponibleEvaluacionEvent(UUID entregableProyectoGrado, UUID proyectoGrado,
                                                            String nombreProyecto, int version) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.entregableProyectoGrado = entregableProyectoGrado;
        this.proyectoGrado = proyectoGrado;
        this.nombreProyecto = nombreProyecto;
        this.version = version;
    }

    public UUID getEntregableProyectoGrado() {
        return entregableProyectoGrado;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public int getVersion() {
        return version;
    }
}
