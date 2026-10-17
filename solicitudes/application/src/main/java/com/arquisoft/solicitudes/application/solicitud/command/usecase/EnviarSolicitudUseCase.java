package com.arquisoft.solicitudes.application.solicitud.command.usecase;

import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudDomain;

import java.util.UUID;

public interface EnviarSolicitudUseCase extends UseCase<EnvioSolicitudDomain, UUID> {
}
