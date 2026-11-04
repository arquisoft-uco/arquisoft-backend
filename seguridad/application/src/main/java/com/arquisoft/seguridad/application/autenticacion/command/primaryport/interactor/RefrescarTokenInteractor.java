package com.arquisoft.seguridad.application.autenticacion.command.primaryport.interactor;

import com.arquisoft.seguridad.application.autenticacion.command.primaryport.model.RefrescarTokenCommand;
import com.arquisoft.seguridad.application.autenticacion.command.result.RefrescoTokenResult;
import com.arquisoft.shared.interactor.Interactor;

public interface RefrescarTokenInteractor
        extends Interactor<RefrescarTokenCommand, RefrescoTokenResult> {}
