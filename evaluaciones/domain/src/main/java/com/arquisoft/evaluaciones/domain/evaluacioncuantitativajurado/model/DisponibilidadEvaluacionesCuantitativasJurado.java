package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model;

import java.util.Set;
import java.util.UUID;

public record DisponibilidadEvaluacionesCuantitativasJurado(
        UUID evaluacionJurado, Set<UUID> itemsSolicitados, Set<UUID> itemsRegistrados) {}
