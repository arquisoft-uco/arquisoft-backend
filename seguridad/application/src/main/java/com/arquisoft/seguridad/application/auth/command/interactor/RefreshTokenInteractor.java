package com.arquisoft.seguridad.application.auth.command.interactor;

import com.arquisoft.seguridad.application.auth.command.result.RefrescoTokenResult;
import com.arquisoft.shared.interactor.Interactor;

public interface RefreshTokenInteractor
        extends Interactor<String, RefrescoTokenResult> {}
