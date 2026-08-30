package com.arquisoft.solicitudes.application.solicitud.command.usecase;

import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudNovedadAsesorDomain;

import java.util.UUID;

public interface EnviarSolicitudNovedadAsesorUseCase
        extends UseCase<EnvioSolicitudNovedadAsesorDomain, UUID> {}
