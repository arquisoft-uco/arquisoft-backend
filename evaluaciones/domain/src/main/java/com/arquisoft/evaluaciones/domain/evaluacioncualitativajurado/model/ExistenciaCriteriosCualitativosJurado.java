package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model;

import java.util.Set;
import java.util.UUID;

public record ExistenciaCriteriosCualitativosJurado(Set<UUID> solicitados, Set<UUID> existentes) {}
