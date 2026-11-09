package com.arquisoft.artefactos.domain.paqueteentregable.event;

import com.arquisoft.artefactos.domain.paqueteentregable.model.ArchivoPaquete;
import com.arquisoft.artefactos.domain.paqueteentregable.model.ArtefactoPaqueteGenerado;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;
import com.arquisoft.shared.util.UtilColeccion;

import java.time.Instant;
import java.util.List;
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
    private final List<ArtefactoPaqueteGenerado> artefactos;

    @SuppressWarnings("checkstyle:ParameterNumber")
    public PaqueteEntregableGeneradoEvent(UUID paqueteEntregable, UUID entregableProyectoGrado, String bucket,
                                          String claveObjeto, String nombreArchivo, long tamanoBytes,
                                          Instant generadoEn, List<ArtefactoPaqueteGenerado> artefactos) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.paqueteEntregable = paqueteEntregable;
        this.entregableProyectoGrado = entregableProyectoGrado;
        this.bucket = bucket;
        this.claveObjeto = claveObjeto;
        this.nombreArchivo = nombreArchivo;
        this.tamanoBytes = tamanoBytes;
        this.generadoEn = generadoEn;
        this.artefactos = UtilColeccion.aplicarPorDefecto(artefactos);
    }

    public static PaqueteEntregableGeneradoEvent de(UUID paqueteEntregable, UUID entregableProyectoGrado,
                                                    ArchivoPaquete archivo, Instant generadoEn,
                                                    List<ArtefactoPaqueteGenerado> artefactos) {
        return new PaqueteEntregableGeneradoEvent(paqueteEntregable, entregableProyectoGrado, archivo.bucket(),
                archivo.claveObjeto(), archivo.nombreArchivo(), archivo.tamanoBytes(), generadoEn, artefactos);
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

    public List<ArtefactoPaqueteGenerado> getArtefactos() {
        return artefactos;
    }
}
