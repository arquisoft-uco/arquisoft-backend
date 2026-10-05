package com.arquisoft.mapas_ruta.application.maparuta.query.readmodel;

import java.time.LocalDate;
import java.util.UUID;

public record MapaRutaReadModel(
        UUID id,
        UUID proyectoGrado,
        String tituloProyecto,
        LocalDate fechaInicio,
        LocalDate fechaFin
) {}
