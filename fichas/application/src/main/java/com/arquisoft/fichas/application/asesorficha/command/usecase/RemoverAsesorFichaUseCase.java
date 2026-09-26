package com.arquisoft.fichas.application.asesorficha.command.usecase;

import com.arquisoft.fichas.application.asesorficha.command.result.RemocionAsesorFichaResult;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface RemoverAsesorFichaUseCase extends UseCase<AsesorFichaDomain, RemocionAsesorFichaResult> {
}
