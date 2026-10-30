package com.arquisoft.artefactos.application.observacion.command.usecase;

import com.arquisoft.artefactos.domain.observacion.RegistroObservacionDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface RegistrarObservacionUseCase extends UseCase<RegistroObservacionDomain, UUID> {
}
