package com.arquisoft.entregables.application.proyectogrado.command.usecase;

import com.arquisoft.entregables.application.proyectogrado.command.result.FinalizacionProyectoGradoResult;
import com.arquisoft.entregables.domain.proyectogrado.FinalizacionProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface FinalizarProyectoGradoUseCase
        extends UseCase<FinalizacionProyectoGradoDomain, FinalizacionProyectoGradoResult> {
}
