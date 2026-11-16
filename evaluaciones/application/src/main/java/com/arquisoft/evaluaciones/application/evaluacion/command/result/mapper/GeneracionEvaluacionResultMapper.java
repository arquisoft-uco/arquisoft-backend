package com.arquisoft.evaluaciones.application.evaluacion.command.result.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.command.result.GeneracionEvaluacionResult;
import com.arquisoft.evaluaciones.domain.evaluacion.EvaluacionDomain;

public final class GeneracionEvaluacionResultMapper {

    private GeneracionEvaluacionResultMapper() {}

    public static GeneracionEvaluacionResult.Generada toResultGenerada(EvaluacionDomain evaluacion) {
        return new GeneracionEvaluacionResult.Generada(evaluacion.getId(), evaluacion.getEntregable());
    }

    public static GeneracionEvaluacionResult.Duplicada toResultDuplicada(EvaluacionDomain evaluacion) {
        return new GeneracionEvaluacionResult.Duplicada(evaluacion.getEntregable());
    }
}
