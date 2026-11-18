package com.arquisoft.biblioteca.application.documentacion.command.usecase;

import com.arquisoft.biblioteca.domain.documentacion.RegistroDocumentacionDomain;
import com.arquisoft.shared.usecase.UseCase;

import java.util.UUID;

public interface RegistrarDocumentacionUseCase extends UseCase<RegistroDocumentacionDomain, UUID> {
}
