package com.arquisoft.entregables.application.estudianteproyectogrado.command.usecase;

import com.arquisoft.entregables.application.estudianteproyectogrado.command.result.RemocionEstudianteProyectoGradoResult;
import com.arquisoft.entregables.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface RemoverEstudianteProyectoGradoUseCase
        extends UseCase<EstudianteProyectoGradoDomain, RemocionEstudianteProyectoGradoResult> {
}
