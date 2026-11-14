package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model;

import java.util.UUID;

public record CriterioActualVsNuevo(UUID evaluacionCualitativaJurado, UUID criterioActual, UUID criterioNuevo) {}
