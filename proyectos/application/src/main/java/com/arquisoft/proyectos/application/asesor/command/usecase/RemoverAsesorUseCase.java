package com.arquisoft.proyectos.application.asesor.command.usecase;

import com.arquisoft.proyectos.application.asesor.command.result.RemocionAsesorResult;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface RemoverAsesorUseCase extends UseCase<AsesorDomain, RemocionAsesorResult> {
}
