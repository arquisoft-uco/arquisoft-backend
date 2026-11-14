package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model;

import java.util.Set;
import java.util.UUID;

public record ExistenciaEvaluacionesCuantitativasAsesorRegistradas(
        UUID evaluacionAsesor, Set<UUID> itemsSolicitados, Set<UUID> itemsRegistrados) {}
