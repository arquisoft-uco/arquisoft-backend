package com.arquisoft.mapas_ruta.domain.maparuta.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.time.LocalDate;
import java.util.UUID;

public class MapaRutaAgregadoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.MapasRuta.MAPA_RUTA_AGREGADO;
    public static final String EVENT_TYPE = "MapaRutaAgregadoEvent";

    private final UUID mapaRuta;
    private final UUID proyectoGrado;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFin;

    public MapaRutaAgregadoEvent(UUID mapaRuta, UUID proyectoGrado, LocalDate fechaInicio, LocalDate fechaFin) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.mapaRuta = mapaRuta;
        this.proyectoGrado = proyectoGrado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public UUID getMapaRuta() {
        return mapaRuta;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }
}
