package com.arquisoft.entregables.application.estudianteproyectogrado.command.usecase;

import com.arquisoft.entregables.application.estudianteproyectogrado.command.result.AgregacionEstudiantesProyectoGradoResult;
import com.arquisoft.entregables.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface AgregarEstudiantesProyectoGradoUseCase
        extends UseCase<AgregacionEstudiantesProyectoGradoDomain, AgregacionEstudiantesProyectoGradoResult> {
}
