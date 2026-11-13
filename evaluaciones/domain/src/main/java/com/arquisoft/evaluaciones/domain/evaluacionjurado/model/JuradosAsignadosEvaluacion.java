package com.arquisoft.evaluaciones.domain.evaluacionjurado.model;

import java.util.Set;
import java.util.UUID;

public record JuradosAsignadosEvaluacion(Set<UUID> solicitados, Set<UUID> asignados) {}
