package com.arquisoft.seguridad.application.auth.command.interactor;

import com.arquisoft.seguridad.application.auth.command.usecase.RefreshTokenUseCase;
import com.arquisoft.shared.interactor.Interactor;

public interface RefreshTokenInteractor
        extends Interactor<String, RefreshTokenUseCase.RefreshResult> {}
