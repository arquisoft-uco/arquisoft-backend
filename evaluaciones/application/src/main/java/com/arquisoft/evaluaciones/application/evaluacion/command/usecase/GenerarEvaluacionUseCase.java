package com.arquisoft.evaluaciones.application.evaluacion.command.usecase;

import com.arquisoft.evaluaciones.application.evaluacion.command.result.GeneracionEvaluacionResult;
import com.arquisoft.evaluaciones.domain.evaluacion.GeneracionEvaluacionDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface GenerarEvaluacionUseCase extends UseCase<GeneracionEvaluacionDomain, GeneracionEvaluacionResult> {
}
