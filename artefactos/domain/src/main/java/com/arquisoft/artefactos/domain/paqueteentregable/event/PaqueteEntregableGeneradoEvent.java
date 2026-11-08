package com.arquisoft.artefactos.domain.paqueteentregable.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.time.Instant;
import java.util.UUID;

public final class PaqueteEntregableGeneradoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.Artefactos.PAQUETE_ENTREGABLE_GENERADO;
    private static final String EVENT_TYPE = "PaqueteEntregableGeneradoEvent";

    private final UUID paqueteEntregable;
    private final UUID entregableProyectoGrado;
    private final String bucket;
    private final String claveObjeto;
    private final String nombreArchivo;
    private final long tamanoBytes;
    private final Instant generadoEn;

    public PaqueteEntregableGeneradoEvent(UUID paqueteEntregable, UUID entregableProyectoGrado, String bucket,
                                          String claveObjeto, String nombreArchivo, long tamanoBytes,
                                          Instant generadoEn) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.paqueteEntregable = paqueteEntregable;
        this.entregableProyectoGrado = entregableProyectoGrado;
        this.bucket = bucket;
        this.claveObjeto = claveObjeto;
        this.nombreArchivo = nombreArchivo;
        this.tamanoBytes = tamanoBytes;
        this.generadoEn = generadoEn;
    }

    public UUID getPaqueteEntregable() {
        return paqueteEntregable;
    }

    public UUID getEntregableProyectoGrado() {
        return entregableProyectoGrado;
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

    public long getTamanoBytes() {
        return tamanoBytes;
    }

    public Instant getGeneradoEn() {
        return generadoEn;
    }
}
