package com.arquisoft.fichas.domain.evaluacionfichaperfil.model;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;

public record ConteoEvaluacionesPorEstado(EstadoEvaluacion estado, long evaluaciones, long evaluacionesConObservaciones) {}
