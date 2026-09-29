package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.model;

import java.util.Set;
import java.util.UUID;

public record CriterioItemsEvaluacion(UUID evaluacionJurado, Set<UUID> items) {}
