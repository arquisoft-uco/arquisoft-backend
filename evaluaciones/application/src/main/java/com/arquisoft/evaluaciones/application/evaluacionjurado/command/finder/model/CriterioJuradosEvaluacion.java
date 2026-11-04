package com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.model;

import java.util.Set;
import java.util.UUID;

public record CriterioJuradosEvaluacion(UUID evaluacion, Set<UUID> jurados) {}
