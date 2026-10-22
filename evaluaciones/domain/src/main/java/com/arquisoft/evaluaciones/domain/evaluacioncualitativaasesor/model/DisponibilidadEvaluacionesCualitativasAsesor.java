package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model;

import java.util.Set;
import java.util.UUID;

public record DisponibilidadEvaluacionesCualitativasAsesor(UUID evaluacionAsesor, Set<UUID> itemsSolicitados, Set<UUID> itemsRegistrados) {}
