package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.event;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ContactoEstudianteEvaluacion;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.List;
import java.util.UUID;

public class EvaluacionesCualitativasJuradoRegistradasEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Evaluaciones.EVALUACIONES_CUALITATIVAS_JURADO_REGISTRADAS;
    public static final String EVENT_TYPE = "EvaluacionesCualitativasJuradoRegistradasEvent";

    public record DatosLoteRegistrado(
            UUID evaluacionJuradoId,
            UUID evaluacionId,
            UUID entregableId,
            UUID juradoId,
            String proyecto,
            int versionEntregable,
            int cantidad) {}

    private final DatosLoteRegistrado datos;
    private final List<ContactoEstudianteEvaluacion> estudiantes;

    public EvaluacionesCualitativasJuradoRegistradasEvent(
            DatosLoteRegistrado datos, List<ContactoEstudianteEvaluacion> estudiantes) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.datos = datos;
        this.estudiantes = List.copyOf(estudiantes);
    }

    public UUID getEvaluacionJuradoId() {
        return datos.evaluacionJuradoId();
    }

    public UUID getEvaluacionId() {
        return datos.evaluacionId();
    }

    public UUID getEntregableId() {
        return datos.entregableId();
    }

    public UUID getJuradoId() {
        return datos.juradoId();
    }

    public String getProyecto() {
        return datos.proyecto();
    }

    public int getVersionEntregable() {
        return datos.versionEntregable();
    }

    public int getCantidad() {
        return datos.cantidad();
    }

    public List<ContactoEstudianteEvaluacion> getEstudiantes() {
        return estudiantes;
    }
}
