package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record ExistenciaItemsCuantitativosAsesor(Set<UUID> solicitados, Map<UUID, Integer> valoresMaximos) {}
