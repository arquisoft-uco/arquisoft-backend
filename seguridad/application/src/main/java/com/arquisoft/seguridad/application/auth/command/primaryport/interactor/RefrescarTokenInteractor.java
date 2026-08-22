package com.arquisoft.seguridad.application.auth.command.primaryport.interactor;

import com.arquisoft.seguridad.application.auth.command.primaryport.model.RefrescarTokenCommand;
import com.arquisoft.seguridad.application.auth.command.result.RefrescoTokenResult;
import com.arquisoft.shared.interactor.Interactor;

public interface RefrescarTokenInteractor
        extends Interactor<RefrescarTokenCommand, RefrescoTokenResult> {}
