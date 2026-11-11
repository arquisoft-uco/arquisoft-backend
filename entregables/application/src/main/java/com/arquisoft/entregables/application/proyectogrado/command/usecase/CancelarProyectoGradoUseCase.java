package com.arquisoft.entregables.application.proyectogrado.command.usecase;

import com.arquisoft.entregables.application.proyectogrado.command.result.CancelacionProyectoGradoResult;
import com.arquisoft.entregables.domain.proyectogrado.CancelacionProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface CancelarProyectoGradoUseCase
        extends UseCase<CancelacionProyectoGradoDomain, CancelacionProyectoGradoResult> {
}
