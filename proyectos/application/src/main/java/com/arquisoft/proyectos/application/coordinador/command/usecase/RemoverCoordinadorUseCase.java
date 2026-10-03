package com.arquisoft.proyectos.application.coordinador.command.usecase;

import com.arquisoft.proyectos.application.coordinador.command.result.RemocionCoordinadorResult;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface RemoverCoordinadorUseCase extends UseCase<CoordinadorDomain, RemocionCoordinadorResult> {
}
