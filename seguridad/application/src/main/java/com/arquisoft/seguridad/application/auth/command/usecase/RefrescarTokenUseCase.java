package com.arquisoft.seguridad.application.auth.command.usecase;

import com.arquisoft.seguridad.application.auth.command.result.RefrescoTokenResult;
import com.arquisoft.seguridad.domain.auth.TokenDomain;
import com.arquisoft.shared.usecase.UseCase;

public interface RefrescarTokenUseCase extends UseCase<TokenDomain, RefrescoTokenResult> {}
