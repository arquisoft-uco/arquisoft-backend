package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model;

import java.util.UUID;

public record PropiedadEvaluacionJurado(UUID evaluacionJurado, UUID juradoAsignado, UUID juradoSolicitante) {}
