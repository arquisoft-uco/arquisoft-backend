package com.arquisoft.evaluaciones.domain.evaluacionasesor.model;

import java.util.UUID;

public record CambioAsesorEvaluacion(UUID evaluacion, UUID asesorActual, UUID asesorNuevo, boolean conRegistros) {}
