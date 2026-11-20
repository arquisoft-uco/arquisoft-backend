package com.arquisoft.biblioteca.application.entregableproyectogrado.command.usecase;

import com.arquisoft.biblioteca.application.entregableproyectogrado.command.result.AgregacionEntregableProyectoGradoResult;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface AgregarEntregableProyectoGradoUseCase
        extends UseCase<EntregableProyectoGradoDomain, AgregacionEntregableProyectoGradoResult> {
}
