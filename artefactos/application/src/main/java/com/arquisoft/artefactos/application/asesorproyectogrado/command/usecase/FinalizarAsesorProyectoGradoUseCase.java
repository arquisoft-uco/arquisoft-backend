package com.arquisoft.artefactos.application.asesorproyectogrado.command.usecase;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.result.FinalizacionAsesorProyectoGradoResult;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface FinalizarAsesorProyectoGradoUseCase
        extends UseCase<AsesorProyectoGradoDomain, FinalizacionAsesorProyectoGradoResult> {
}
