package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model;

import java.util.UUID;

public record ExistenciaEvaluacionCualitativaAsesor(
        UUID evaluacionCualitativaAsesor, boolean existeContexto, boolean existeRegistro) {}
