package com.arquisoft.proyectos.application.estudiante.command.usecase;

import com.arquisoft.proyectos.application.estudiante.command.result.RemocionEstudianteResult;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface RemoverEstudianteUseCase extends UseCase<EstudianteDomain, RemocionEstudianteResult> {
}
