package com.arquisoft.solicitudes.application.respuesta.command.usecase;

import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaNovedadAsesorDomain;

import java.util.UUID;

public interface ResponderSolicitudNovedadAsesorUseCase
        extends UseCase<RespuestaNovedadAsesorDomain, UUID> {}
