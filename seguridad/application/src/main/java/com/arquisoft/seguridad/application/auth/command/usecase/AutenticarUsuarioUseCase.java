package com.arquisoft.seguridad.application.auth.command.usecase;

import com.arquisoft.seguridad.application.auth.command.result.AutenticacionResult;
import com.arquisoft.seguridad.domain.auth.AutenticacionDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface AutenticarUsuarioUseCase extends UseCase<AutenticacionDomain, AutenticacionResult> {}
