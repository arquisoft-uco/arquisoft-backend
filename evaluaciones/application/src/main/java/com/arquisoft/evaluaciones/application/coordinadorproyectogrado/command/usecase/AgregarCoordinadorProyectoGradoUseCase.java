package com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.usecase;

import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.result.AgregacionCoordinadorProyectoGradoResult;
import com.arquisoft.evaluaciones.domain.coordinadorproyectogrado.CoordinadorProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface AgregarCoordinadorProyectoGradoUseCase
        extends UseCase<CoordinadorProyectoGradoDomain, AgregacionCoordinadorProyectoGradoResult> {
}
