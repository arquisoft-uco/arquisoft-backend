package com.arquisoft.proyectos.application.estudiante.command.usecase;

import com.arquisoft.proyectos.application.estudiante.command.result.ActualizacionEstudianteResult;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface ActualizarEstudianteUseCase extends UseCase<EstudianteDomain, ActualizacionEstudianteResult> {
}
