package com.arquisoft.evaluaciones.application.asesor.command.usecase;

import com.arquisoft.evaluaciones.application.asesor.command.result.RemocionAsesorResult;
import com.arquisoft.evaluaciones.domain.asesor.AsesorDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface RemoverAsesorUseCase extends UseCase<AsesorDomain, RemocionAsesorResult> {
}
