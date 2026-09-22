package com.arquisoft.solicitudes.application.solicitud.command.usecase;

import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudAmpliacionPlazoDomain;

import java.util.UUID;

public interface EnviarSolicitudAmpliacionPlazoUseCase
        extends UseCase<EnvioSolicitudAmpliacionPlazoDomain, UUID> {}
