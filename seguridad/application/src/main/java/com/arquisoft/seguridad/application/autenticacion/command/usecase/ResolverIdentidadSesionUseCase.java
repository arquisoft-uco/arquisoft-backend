package com.arquisoft.seguridad.application.autenticacion.command.usecase;

import com.arquisoft.seguridad.application.autenticacion.command.result.ResolucionSesionResult;
import com.arquisoft.seguridad.domain.autenticacion.CredencialSesionDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface ResolverIdentidadSesionUseCase extends UseCase<CredencialSesionDomain, ResolucionSesionResult> {}
