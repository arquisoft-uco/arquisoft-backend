package com.arquisoft.mapas_ruta.domain.planestimado.model;

import java.time.LocalDate;
import java.util.UUID;

public record PlazoPlanesEstimados(
        UUID mapaRuta,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        long diasAcumulados,
        int diasNuevoPlan) {}
