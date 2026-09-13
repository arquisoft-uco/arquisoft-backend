package com.arquisoft.fichas.application.coordinador.command.usecase;

import com.arquisoft.fichas.application.coordinador.command.result.AgregacionCoordinadorResult;
import com.arquisoft.fichas.domain.coordinador.CoordinadorDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface AgregarCoordinadorFichasUseCase extends UseCase<CoordinadorDomain, AgregacionCoordinadorResult> {
}
