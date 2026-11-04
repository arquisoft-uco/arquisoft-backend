package com.arquisoft.proyectos.application.asesorproyectogrado.command.usecase;

import com.arquisoft.proyectos.domain.asesorproyectogrado.AsignacionAsesorProyectoGradoDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface AsignarAsesorProyectoGradoUseCase
        extends UseCase<AsignacionAsesorProyectoGradoDomain, UUID> {}
