package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model;

import java.util.Set;
import java.util.UUID;

public record ExistenciaEvaluacionesCualitativasJurado(
        UUID evaluacionJurado, Set<UUID> evaluacionesSolicitadas, Set<UUID> evaluacionesEncontradas) {}
