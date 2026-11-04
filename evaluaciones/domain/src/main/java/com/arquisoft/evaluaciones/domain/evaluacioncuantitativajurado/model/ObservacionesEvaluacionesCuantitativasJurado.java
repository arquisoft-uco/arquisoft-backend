package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model;

import java.util.Set;
import java.util.UUID;

public record ObservacionesEvaluacionesCuantitativasJurado(
        UUID evaluacionJurado, Set<UUID> evaluaciones, boolean existenObservaciones) {}
