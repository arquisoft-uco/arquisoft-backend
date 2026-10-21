package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model;

import java.util.UUID;

public record PropiedadEvaluacionAsesor(UUID evaluacionAsesor, UUID asesorAsignado, UUID asesorSolicitante) {}
