package com.arquisoft.evaluaciones.domain.evaluacion.event;

import com.arquisoft.evaluaciones.domain.coordinadorproyectogrado.model.ContactoCoordinador;
import com.arquisoft.evaluaciones.domain.entregable.EntregableDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.EvaluacionDomain;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public class EvaluacionGeneradaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Evaluaciones.EVALUACION_GENERADA;
    public static final String EVENT_TYPE = "EvaluacionGeneradaEvent";

    private final UUID evaluacionId;
    private final UUID entregableId;
    private final UUID proyectoGradoId;
    private final String nombreProyecto;
    private final int versionEntregable;
    private final ContactoCoordinador coordinador;

    public EvaluacionGeneradaEvent(
            EvaluacionDomain evaluacion, EntregableDomain entregable, ContactoCoordinador coordinador) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.evaluacionId = evaluacion.getId();
        this.entregableId = entregable.getId();
        this.proyectoGradoId = entregable.getProyectoGrado();
        this.nombreProyecto = entregable.getProyecto();
        this.versionEntregable = entregable.getVersion();
        this.coordinador = coordinador;
    }

    public UUID getEvaluacionId() {
        return evaluacionId;
    }

    public UUID getEntregableId() {
        return entregableId;
    }

    public UUID getProyectoGradoId() {
        return proyectoGradoId;
    }

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public int getVersionEntregable() {
        return versionEntregable;
    }

    public ContactoCoordinador getCoordinador() {
        return coordinador;
    }
}
