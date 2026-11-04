package com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.usecase;

import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.result.AgregacionEstudiantesProyectoGradoResult;
import com.arquisoft.evaluaciones.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface AgregarEstudiantesProyectoGradoUseCase
        extends UseCase<AgregacionEstudiantesProyectoGradoDomain, AgregacionEstudiantesProyectoGradoResult> {
}
