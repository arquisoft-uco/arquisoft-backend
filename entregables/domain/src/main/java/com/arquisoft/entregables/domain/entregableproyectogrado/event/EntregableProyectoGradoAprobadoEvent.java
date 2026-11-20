package com.arquisoft.entregables.domain.entregableproyectogrado.event;

import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.util.UUID;

public final class EntregableProyectoGradoAprobadoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Entregables.ENTREGABLE_PROYECTO_GRADO_APROBADO;
    private static final String EVENT_TYPE = "EntregableProyectoGradoAprobadoEvent";

    private final UUID entregableProyectoGrado;
    private final UUID proyectoGrado;
    private final int version;
    private final String estadoEntregable;
    private final String estadoEntregableNombre;
    private final String bucket;
    private final String claveObjeto;
    private final String nombreArchivo;

    public EntregableProyectoGradoAprobadoEvent(UUID entregableProyectoGrado, UUID proyectoGrado, int version,
                                                EstadoEntregable estadoEntregable, String bucket,
                                                String claveObjeto, String nombreArchivo) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.entregableProyectoGrado = entregableProyectoGrado;
        this.proyectoGrado = proyectoGrado;
        this.version = version;
        this.estadoEntregable = estadoEntregable.getId();
        this.estadoEntregableNombre = estadoEntregable.getNombre();
        this.bucket = bucket;
        this.claveObjeto = claveObjeto;
        this.nombreArchivo = nombreArchivo;
    }

    public UUID getEntregableProyectoGrado() {
        return entregableProyectoGrado;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public int getVersion() {
        return version;
    }

    public String getEstadoEntregable() {
        return estadoEntregable;
    }

    public String getEstadoEntregableNombre() {
        return estadoEntregableNombre;
    }

    public String getBucket() {
        return bucket;
    }

    public String getClaveObjeto() {
        return claveObjeto;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }
}
