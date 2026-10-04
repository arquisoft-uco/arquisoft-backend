package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;

import java.util.UUID;

public record EstadoOmisionEvaluacionesCuantitativasJurado(UUID evaluacionJurado, EstadoEvaluacion estado) {}
