package com.arquisoft.fichas.domain.estadoevaluacionficha.model;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;

import java.util.UUID;

public record UltimoEstadoEvaluacion(UUID evaluacionFichaPerfil, EstadoEvaluacion ultimoEstado) {}
