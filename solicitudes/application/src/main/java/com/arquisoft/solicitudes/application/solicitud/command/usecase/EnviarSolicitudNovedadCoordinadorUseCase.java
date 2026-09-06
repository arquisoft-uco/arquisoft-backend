package com.arquisoft.solicitudes.application.solicitud.command.usecase;

import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudNovedadCoordinadorDomain;

import java.util.UUID;

public interface EnviarSolicitudNovedadCoordinadorUseCase
        extends UseCase<EnvioSolicitudNovedadCoordinadorDomain, UUID> {}
