package com.arquisoft.solicitudes.application.respuesta.command.usecase;

import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaNovedadCoordinadorDomain;

import java.util.UUID;

public interface ResponderSolicitudNovedadCoordinadorUseCase
        extends UseCase<RespuestaNovedadCoordinadorDomain, UUID> {}
