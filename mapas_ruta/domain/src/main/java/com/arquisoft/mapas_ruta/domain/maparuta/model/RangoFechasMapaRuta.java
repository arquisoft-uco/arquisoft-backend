package com.arquisoft.mapas_ruta.domain.maparuta.model;

import java.time.LocalDate;
import java.util.UUID;

public record RangoFechasMapaRuta(UUID mapaRuta, LocalDate fechaInicio, LocalDate fechaFin) {}
