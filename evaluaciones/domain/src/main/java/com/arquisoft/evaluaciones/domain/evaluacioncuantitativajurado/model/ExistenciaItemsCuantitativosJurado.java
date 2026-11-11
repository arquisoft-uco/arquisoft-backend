package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record ExistenciaItemsCuantitativosJurado(Set<UUID> solicitados, Map<UUID, Integer> valoresMaximos) {}
