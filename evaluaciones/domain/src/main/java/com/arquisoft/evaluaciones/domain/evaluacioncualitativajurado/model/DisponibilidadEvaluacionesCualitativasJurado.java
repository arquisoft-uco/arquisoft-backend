package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model;

import java.util.Set;
import java.util.UUID;

public record DisponibilidadEvaluacionesCualitativasJurado(
        UUID evaluacionJurado, Set<UUID> itemsSolicitados, Set<UUID> itemsRegistrados) {}
