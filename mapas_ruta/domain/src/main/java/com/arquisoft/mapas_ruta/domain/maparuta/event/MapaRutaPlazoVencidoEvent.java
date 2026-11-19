package com.arquisoft.mapas_ruta.domain.maparuta.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.time.LocalDate;
import java.util.UUID;

public class MapaRutaPlazoVencidoEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.MapasRuta.MAPA_RUTA_PLAZO_VENCIDO;
    public static final String EVENT_TYPE = "MapaRutaPlazoVencidoEvent";

    private final UUID mapaRuta;
    private final UUID proyectoGrado;
    private final LocalDate fechaFin;

    public MapaRutaPlazoVencidoEvent(UUID mapaRuta, UUID proyectoGrado, LocalDate fechaFin) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.mapaRuta = mapaRuta;
        this.proyectoGrado = proyectoGrado;
        this.fechaFin = fechaFin;
    }

    public UUID getMapaRuta() {
        return mapaRuta;
    }

    public UUID getProyectoGrado() {
        return proyectoGrado;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }
}
