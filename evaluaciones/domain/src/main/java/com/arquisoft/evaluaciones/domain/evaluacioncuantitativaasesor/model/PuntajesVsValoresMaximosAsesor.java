package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model;

import java.util.Map;
import java.util.UUID;

public record PuntajesVsValoresMaximosAsesor(Map<UUID, Integer> puntajes, Map<UUID, Integer> valoresMaximos) {}
