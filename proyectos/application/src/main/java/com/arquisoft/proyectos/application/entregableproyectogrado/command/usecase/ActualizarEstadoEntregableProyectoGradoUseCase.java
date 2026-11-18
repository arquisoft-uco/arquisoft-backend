package com.arquisoft.proyectos.application.entregableproyectogrado.command.usecase;

import com.arquisoft.proyectos.application.entregableproyectogrado.command.result.ActualizacionEstadoEntregableProyectoGradoResult;
import com.arquisoft.proyectos.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface ActualizarEstadoEntregableProyectoGradoUseCase
        extends UseCase<EntregableProyectoGradoDomain, ActualizacionEstadoEntregableProyectoGradoResult> {
}
