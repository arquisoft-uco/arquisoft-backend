package com.arquisoft.solicitudes.application.respuesta.command.usecase;

import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.solicitudes.domain.respuesta.RespuestaSolicitudDomain;

import java.util.UUID;

public interface ResponderSolicitudUseCase extends UseCase<RespuestaSolicitudDomain, UUID> {
}
