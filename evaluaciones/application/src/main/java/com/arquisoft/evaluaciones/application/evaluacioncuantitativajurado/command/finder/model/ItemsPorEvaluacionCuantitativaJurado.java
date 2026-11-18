package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.model;

import java.util.Set;
import java.util.UUID;

public record ItemsPorEvaluacionCuantitativaJurado(UUID evaluacionJurado, Set<UUID> items) {}
