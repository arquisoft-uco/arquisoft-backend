package com.arquisoft.mapas_ruta.application.planestimado.command.usecase;

import com.arquisoft.mapas_ruta.domain.planestimado.PlanEstimadoDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface AgregarPlanEstimadoUseCase extends UseCase<PlanEstimadoDomain, UUID> {}
