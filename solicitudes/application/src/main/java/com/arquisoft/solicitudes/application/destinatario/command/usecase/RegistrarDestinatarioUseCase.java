package com.arquisoft.solicitudes.application.destinatario.command.usecase;

import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.solicitudes.domain.destinatario.DestinatarioDomain;

import java.util.UUID;

public interface RegistrarDestinatarioUseCase extends UseCase<DestinatarioDomain, UUID> {
}
