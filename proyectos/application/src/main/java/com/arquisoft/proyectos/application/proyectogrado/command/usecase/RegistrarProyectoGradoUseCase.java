package com.arquisoft.proyectos.application.proyectogrado.command.usecase;

import com.arquisoft.proyectos.application.proyectogrado.command.result.RegistroProyectoGradoResult;
import com.arquisoft.proyectos.domain.proyectogrado.RegistroProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface RegistrarProyectoGradoUseCase
        extends UseCase<RegistroProyectoGradoDomain, RegistroProyectoGradoResult> {}
