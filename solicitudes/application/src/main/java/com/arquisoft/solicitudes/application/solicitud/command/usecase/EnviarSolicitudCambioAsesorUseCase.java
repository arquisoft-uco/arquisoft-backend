package com.arquisoft.solicitudes.application.solicitud.command.usecase;

import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudCambioAsesorDomain;

import java.util.UUID;

public interface EnviarSolicitudCambioAsesorUseCase
        extends UseCase<EnvioSolicitudCambioAsesorDomain, UUID> {}
