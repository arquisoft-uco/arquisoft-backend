package com.arquisoft.artefactos.application.estudiante.command.usecase;

import com.arquisoft.artefactos.application.estudiante.command.result.RemocionEstudianteResult;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface RemoverEstudianteUseCase extends UseCase<EstudianteDomain, RemocionEstudianteResult> {
}
