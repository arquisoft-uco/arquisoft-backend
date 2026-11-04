package com.arquisoft.seguridad.application.autenticacion.command.usecase;

import com.arquisoft.seguridad.application.autenticacion.command.result.ValidacionTokenResult;
import com.arquisoft.seguridad.domain.autenticacion.TokenDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface ValidarTokenUseCase extends UseCase<TokenDomain, ValidacionTokenResult> {}
