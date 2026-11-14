package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;

import java.util.UUID;

public record EstadoOmisionEvaluacionesCualitativasAsesor(UUID evaluacionAsesor, EstadoEvaluacion estado) {}
