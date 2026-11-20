package com.arquisoft.entregables.application.entregableproyectogrado.command.usecase;

import com.arquisoft.entregables.application.entregableproyectogrado.command.result.RegistroResultadoEvaluacionEntregableProyectoGradoResult;
import com.arquisoft.entregables.domain.entregableproyectogrado.ResultadoEvaluacionEntregableProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface RegistrarResultadoEvaluacionEntregableProyectoGradoUseCase
        extends UseCase<ResultadoEvaluacionEntregableProyectoGradoDomain,
        RegistroResultadoEvaluacionEntregableProyectoGradoResult> {
}
