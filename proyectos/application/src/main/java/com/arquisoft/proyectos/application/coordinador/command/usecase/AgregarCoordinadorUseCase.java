package com.arquisoft.proyectos.application.coordinador.command.usecase;

import com.arquisoft.proyectos.application.coordinador.command.result.AgregacionCoordinadorResult;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface AgregarCoordinadorUseCase extends UseCase<CoordinadorDomain, AgregacionCoordinadorResult> {
}
