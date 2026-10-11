package com.arquisoft.fichas.application.representantecomite.command.usecase;

import com.arquisoft.fichas.application.representantecomite.command.result.RemocionRepresentanteComiteResult;
import com.arquisoft.fichas.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface RemoverRepresentanteComiteUseCase extends UseCase<RepresentanteComiteDomain, RemocionRepresentanteComiteResult> {
}
