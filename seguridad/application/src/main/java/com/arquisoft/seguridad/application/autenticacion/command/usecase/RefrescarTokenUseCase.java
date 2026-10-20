package com.arquisoft.seguridad.application.autenticacion.command.usecase;

import com.arquisoft.seguridad.application.autenticacion.command.result.RefrescoTokenResult;
import com.arquisoft.seguridad.domain.autenticacion.TokenDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface RefrescarTokenUseCase extends UseCase<TokenDomain, RefrescoTokenResult> {}
