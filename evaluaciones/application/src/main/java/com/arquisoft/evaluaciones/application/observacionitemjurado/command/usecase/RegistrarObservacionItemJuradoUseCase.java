package com.arquisoft.evaluaciones.application.observacionitemjurado.command.usecase;

import com.arquisoft.evaluaciones.domain.observacionitemjurado.RegistroObservacionItemJuradoDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface RegistrarObservacionItemJuradoUseCase extends UseCase<RegistroObservacionItemJuradoDomain, UUID> {
}
