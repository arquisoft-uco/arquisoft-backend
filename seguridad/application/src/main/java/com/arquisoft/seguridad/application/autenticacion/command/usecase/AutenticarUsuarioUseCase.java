package com.arquisoft.seguridad.application.autenticacion.command.usecase;

import com.arquisoft.seguridad.application.autenticacion.command.result.AutenticacionResult;
import com.arquisoft.seguridad.domain.autenticacion.AutenticacionDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface AutenticarUsuarioUseCase extends UseCase<AutenticacionDomain, AutenticacionResult> {}
