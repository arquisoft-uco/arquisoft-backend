package com.arquisoft.fichas.domain.observacionevaluacion.model;

import java.util.UUID;

public record DisponibilidadObservacionEvaluacion(UUID evaluacionFichaPerfil, String observacion, boolean yaExiste) {}
