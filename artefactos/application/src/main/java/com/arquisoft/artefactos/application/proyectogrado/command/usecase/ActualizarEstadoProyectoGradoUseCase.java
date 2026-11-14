package com.arquisoft.artefactos.application.proyectogrado.command.usecase;

import com.arquisoft.artefactos.application.proyectogrado.command.result.ActualizacionEstadoProyectoGradoResult;
import com.arquisoft.artefactos.domain.proyectogrado.ActualizacionEstadoProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface ActualizarEstadoProyectoGradoUseCase
        extends UseCase<ActualizacionEstadoProyectoGradoDomain, ActualizacionEstadoProyectoGradoResult> {
}
