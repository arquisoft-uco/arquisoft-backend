package com.arquisoft.entregables.domain.entregableproyectogrado.event;

import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.entregables.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;
import java.util.UUID;

public final class EntregableProyectoGradoDisponibleEvaluacionEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_DISPONIBLE_EVALUACION;
    private static final String EVENT_TYPE = "EntregableProyectoGradoDisponibleEvaluacionEvent";
    private static final EstadoEntregable ESTADO = EstadoEntregable.DISPONIBLE_EVALUACION;

    private final UUID entregableProyectoGrado;
    private final UUID proyectoGrado;
    private final String nombreProyecto;
    private final int version;
    private final UUID asesor;
    private final String estadoEntregable;
    private final String estadoEntregableNombre;
    private final List<ContactoEstudiante> estudiantes;

    public EntregableProyectoGradoDisponibleEvaluacionEvent(UUID entregableProyectoGrado, UUID proyectoGrado,
                                            String nombreProyecto, int version, UUID asesor,
                                            List<ContactoEstudiante> estudiantes) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.entregableProyectoGrado = entregableProyectoGrado;
        this.proyectoGrado = proyectoGrado;
        this.nombreProyecto = nombreProyecto;
        this.version = version;
        this.asesor = asesor;
        this.estadoEntregable = ESTADO.getId();
        this.estadoEntregableNombre = ESTADO.getNombre();
        this.estudiantes = List.copyOf(UtilColeccion.aplicarPorDefecto(estudiantes));
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

    public UUID getAsesor() {
        return asesor;
    }

    public String getEstadoEntregable() {
        return estadoEntregable;
    }

    public String getEstadoEntregableNombre() {
        return estadoEntregableNombre;
    }

    public List<ContactoEstudiante> getEstudiantes() {
        return estudiantes;
    }
}
