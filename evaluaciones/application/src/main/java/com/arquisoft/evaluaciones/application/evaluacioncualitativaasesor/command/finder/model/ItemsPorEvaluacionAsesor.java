package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.finder.model;

import java.util.Set;
import java.util.UUID;

public record ItemsPorEvaluacionAsesor(UUID evaluacionAsesor, Set<UUID> items) {}
