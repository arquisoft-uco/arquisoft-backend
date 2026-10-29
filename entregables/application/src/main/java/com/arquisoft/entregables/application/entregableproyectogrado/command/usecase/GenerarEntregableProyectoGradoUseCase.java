package com.arquisoft.entregables.application.entregableproyectogrado.command.usecase;

import com.arquisoft.entregables.domain.entregableproyectogrado.GeneracionEntregableProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface GenerarEntregableProyectoGradoUseCase
        extends UseCase<GeneracionEntregableProyectoGradoDomain, UUID> {
}
