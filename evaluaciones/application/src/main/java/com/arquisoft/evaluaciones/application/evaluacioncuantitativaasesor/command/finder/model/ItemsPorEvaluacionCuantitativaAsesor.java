package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.model;

import java.util.Set;
import java.util.UUID;

public record ItemsPorEvaluacionCuantitativaAsesor(UUID evaluacionAsesor, Set<UUID> items) {}
