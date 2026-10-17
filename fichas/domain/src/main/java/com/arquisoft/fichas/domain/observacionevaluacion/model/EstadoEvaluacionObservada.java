package com.arquisoft.fichas.domain.observacionevaluacion.model;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;

import java.util.UUID;

public record EstadoEvaluacionObservada(UUID evaluacionFichaPerfil, EstadoEvaluacion ultimoEstado) {}
