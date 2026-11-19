package com.arquisoft.proyectos.infrastructure.proyectogrado.command.primaryadapter.amqp.mapasruta.maparuta;

import java.time.Instant;
import java.time.LocalDate;

public record MapaRutaPlazoVencidoPayload(
        String idEvento,
        Instant ocurridoEn,
        String mapaRuta,
        String proyectoGrado,
        LocalDate fechaFin
) {
}
