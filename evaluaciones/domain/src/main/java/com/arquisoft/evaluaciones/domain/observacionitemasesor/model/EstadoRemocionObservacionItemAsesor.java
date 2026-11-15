package com.arquisoft.evaluaciones.domain.observacionitemasesor.model;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;

import java.util.UUID;

public record EstadoRemocionObservacionItemAsesor(UUID evaluacionAsesor, EstadoEvaluacion estado) {}
