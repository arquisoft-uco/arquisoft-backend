package com.arquisoft.solicitudes.application.remitente.command.usecase;

import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.solicitudes.domain.remitente.RemitenteDomain;

import java.util.UUID;

public interface RegistrarRemitenteUseCase extends UseCase<RemitenteDomain, UUID> {
}
