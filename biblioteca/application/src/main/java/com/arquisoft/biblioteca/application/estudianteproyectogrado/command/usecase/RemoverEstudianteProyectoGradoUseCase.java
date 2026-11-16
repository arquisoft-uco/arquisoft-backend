package com.arquisoft.biblioteca.application.estudianteproyectogrado.command.usecase;

import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.result.RemocionEstudianteProyectoGradoResult;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface RemoverEstudianteProyectoGradoUseCase
        extends UseCase<EstudianteProyectoGradoDomain, RemocionEstudianteProyectoGradoResult> {
}
