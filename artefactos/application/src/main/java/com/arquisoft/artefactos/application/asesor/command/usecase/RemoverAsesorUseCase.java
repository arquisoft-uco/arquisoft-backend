package com.arquisoft.artefactos.application.asesor.command.usecase;

import com.arquisoft.artefactos.application.asesor.command.result.RemocionAsesorResult;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface RemoverAsesorUseCase extends UseCase<AsesorDomain, RemocionAsesorResult> {
}
