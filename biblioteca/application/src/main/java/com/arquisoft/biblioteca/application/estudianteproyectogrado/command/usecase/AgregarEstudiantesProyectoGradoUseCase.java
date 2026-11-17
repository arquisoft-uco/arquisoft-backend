package com.arquisoft.biblioteca.application.estudianteproyectogrado.command.usecase;

import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.result.AgregacionEstudiantesProyectoGradoResult;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface AgregarEstudiantesProyectoGradoUseCase
        extends UseCase<AgregacionEstudiantesProyectoGradoDomain, AgregacionEstudiantesProyectoGradoResult> {
}
