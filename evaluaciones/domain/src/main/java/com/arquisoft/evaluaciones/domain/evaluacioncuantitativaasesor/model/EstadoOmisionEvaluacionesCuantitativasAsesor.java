package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;

import java.util.UUID;

public record EstadoOmisionEvaluacionesCuantitativasAsesor(UUID evaluacionAsesor, EstadoEvaluacion estado) {}
