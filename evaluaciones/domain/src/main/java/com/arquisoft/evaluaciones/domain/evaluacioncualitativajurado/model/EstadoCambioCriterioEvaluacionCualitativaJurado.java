package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;

import java.util.UUID;

public record EstadoCambioCriterioEvaluacionCualitativaJurado(
        UUID evaluacionCualitativaJurado, EstadoEvaluacion estado) {}
