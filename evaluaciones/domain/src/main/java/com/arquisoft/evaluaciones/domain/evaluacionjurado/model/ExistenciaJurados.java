package com.arquisoft.evaluaciones.domain.evaluacionjurado.model;

import java.util.Set;
import java.util.UUID;

public record ExistenciaJurados(Set<UUID> solicitados, Set<UUID> existentes) {}
