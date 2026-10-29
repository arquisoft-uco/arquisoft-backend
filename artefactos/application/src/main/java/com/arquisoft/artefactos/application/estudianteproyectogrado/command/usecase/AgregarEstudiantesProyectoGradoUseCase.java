package com.arquisoft.artefactos.application.estudianteproyectogrado.command.usecase;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.result.AgregacionEstudiantesProyectoGradoResult;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface AgregarEstudiantesProyectoGradoUseCase
        extends UseCase<AgregacionEstudiantesProyectoGradoDomain, AgregacionEstudiantesProyectoGradoResult> {
}
