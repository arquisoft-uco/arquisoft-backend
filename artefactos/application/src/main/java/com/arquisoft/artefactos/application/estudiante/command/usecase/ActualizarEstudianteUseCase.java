package com.arquisoft.artefactos.application.estudiante.command.usecase;

import com.arquisoft.artefactos.application.estudiante.command.result.ActualizacionEstudianteResult;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface ActualizarEstudianteUseCase extends UseCase<EstudianteDomain, ActualizacionEstudianteResult> {
}
