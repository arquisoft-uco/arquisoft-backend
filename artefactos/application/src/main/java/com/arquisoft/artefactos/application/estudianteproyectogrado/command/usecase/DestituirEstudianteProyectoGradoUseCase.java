package com.arquisoft.artefactos.application.estudianteproyectogrado.command.usecase;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.result.DestitucionEstudianteProyectoGradoResult;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.DestitucionEstudianteProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface DestituirEstudianteProyectoGradoUseCase
        extends UseCase<DestitucionEstudianteProyectoGradoDomain, DestitucionEstudianteProyectoGradoResult> {
}
