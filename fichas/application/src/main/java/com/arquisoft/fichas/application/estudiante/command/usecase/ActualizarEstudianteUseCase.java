package com.arquisoft.fichas.application.estudiante.command.usecase;

import com.arquisoft.fichas.application.estudiante.command.result.ActualizacionEstudianteResult;
import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface ActualizarEstudianteUseCase extends UseCase<EstudianteDomain, ActualizacionEstudianteResult> {
}
