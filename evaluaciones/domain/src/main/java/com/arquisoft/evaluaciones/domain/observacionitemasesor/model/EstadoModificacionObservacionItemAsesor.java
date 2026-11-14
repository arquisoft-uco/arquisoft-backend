package com.arquisoft.evaluaciones.domain.observacionitemasesor.model;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;

import java.util.UUID;

public record EstadoModificacionObservacionItemAsesor(UUID evaluacionAsesor, EstadoEvaluacion estado) {}
