package com.arquisoft.fichas.application.estudiante.command.usecase;

import com.arquisoft.fichas.application.estudiante.command.result.RemocionEstudianteResult;
import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface RemoverEstudianteUseCase extends UseCase<EstudianteDomain, RemocionEstudianteResult> {
}
