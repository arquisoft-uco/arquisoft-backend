package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model;

import java.util.Set;
import java.util.UUID;

public record ExistenciaCriteriosCualitativosAsesor(Set<UUID> solicitados, Set<UUID> existentes) {}
