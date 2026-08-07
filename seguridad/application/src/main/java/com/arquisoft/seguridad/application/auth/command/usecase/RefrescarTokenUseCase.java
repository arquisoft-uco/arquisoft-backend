package com.arquisoft.seguridad.application.auth.command.usecase;

import com.arquisoft.seguridad.application.auth.command.result.RefrescoTokenResult;
import com.arquisoft.shared.usecase.UseCase;

public interface RefrescarTokenUseCase extends UseCase<String, RefrescoTokenResult> {}
