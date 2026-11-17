package com.arquisoft.entregables.application.proyectogrado.command.usecase;

import com.arquisoft.entregables.application.proyectogrado.command.result.ActualizacionEstadoProyectoGradoResult;
import com.arquisoft.entregables.domain.proyectogrado.ActualizacionEstadoProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface ActualizarEstadoProyectoGradoUseCase
        extends UseCase<ActualizacionEstadoProyectoGradoDomain, ActualizacionEstadoProyectoGradoResult> {
}
