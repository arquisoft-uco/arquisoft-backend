package com.arquisoft.fichas.application.estudiante.command.usecase;

import com.arquisoft.fichas.application.estudiante.command.result.AgregacionEstudianteResult;
import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface AgregarEstudianteUseCase extends UseCase<EstudianteDomain, AgregacionEstudianteResult> {
}
