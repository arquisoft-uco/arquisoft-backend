package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model;

import java.util.Map;
import java.util.UUID;

public record PuntajesVsValoresMaximosJurado(Map<UUID, Integer> puntajes, Map<UUID, Integer> valoresMaximos) {}
