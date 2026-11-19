package com.arquisoft.mapas_ruta.application.proyectogrado.command.usecase;

import com.arquisoft.mapas_ruta.application.proyectogrado.command.result.ActualizacionEstadoProyectoGradoResult;
import com.arquisoft.mapas_ruta.domain.proyectogrado.ActualizacionEstadoProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface ActualizarEstadoProyectoGradoUseCase
        extends UseCase<ActualizacionEstadoProyectoGradoDomain, ActualizacionEstadoProyectoGradoResult> {
}
