package com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.usecase;

import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.result.RemocionEstudianteProyectoGradoResult;
import com.arquisoft.evaluaciones.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface RemoverEstudianteProyectoGradoUseCase
        extends UseCase<EstudianteProyectoGradoDomain, RemocionEstudianteProyectoGradoResult> {
}
