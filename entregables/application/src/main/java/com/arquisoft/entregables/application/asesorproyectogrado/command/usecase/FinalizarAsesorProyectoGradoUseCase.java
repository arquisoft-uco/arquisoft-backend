package com.arquisoft.entregables.application.asesorproyectogrado.command.usecase;

import com.arquisoft.entregables.application.asesorproyectogrado.command.result.FinalizacionAsesorProyectoGradoResult;
import com.arquisoft.entregables.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface FinalizarAsesorProyectoGradoUseCase
        extends UseCase<AsesorProyectoGradoDomain, FinalizacionAsesorProyectoGradoResult> {
}
