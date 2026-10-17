package com.arquisoft.mapas_ruta.domain.maparuta.event;

import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.message.constant.EventTopics;

import java.time.LocalDate;
import java.util.UUID;

public class MapaRutaFechaFinCambiadaEvent extends DomainEvent {

    public static final String EVENT_TOPIC = EventTopics.MapasRuta.MAPA_RUTA_FECHA_FIN_CAMBIADA;
    public static final String EVENT_TYPE = "MapaRutaFechaFinCambiadaEvent";

    private final UUID mapaRuta;
    private final UUID proyectoGrado;
    private final LocalDate fechaInicio;
    private final LocalDate fechaFinAnterior;
    private final LocalDate fechaFin;

    public MapaRutaFechaFinCambiadaEvent(UUID mapaRuta, UUID proyectoGrado, LocalDate fechaInicio,
                                         LocalDate fechaFinAnterior, LocalDate fechaFin) {
        super(EVENT_TOPIC, EVENT_TYPE);
        this.mapaRuta = mapaRuta;
        this.proyectoGrado = proyectoGrado;
        this.fechaInicio = fechaInicio;
        this.fechaFinAnterior = fechaFinAnterior;
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

    public LocalDate getFechaFinAnterior() {
        return fechaFinAnterior;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }
}
