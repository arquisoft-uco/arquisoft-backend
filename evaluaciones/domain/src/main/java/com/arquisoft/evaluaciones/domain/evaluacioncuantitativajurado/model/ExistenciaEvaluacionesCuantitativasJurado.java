package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model;

import java.util.Set;
import java.util.UUID;

public record ExistenciaEvaluacionesCuantitativasJurado(
        UUID evaluacionJurado, Set<UUID> evaluacionesSolicitadas, Set<UUID> evaluacionesEncontradas) {}
