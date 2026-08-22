package com.arquisoft.seguridad.application.auth.command.primaryport.interactor;

import com.arquisoft.seguridad.application.auth.command.primaryport.model.ValidarTokenCommand;
import com.arquisoft.seguridad.application.auth.command.result.ValidacionTokenResult;
import com.arquisoft.shared.interactor.Interactor;

public interface ValidarTokenInteractor
        extends Interactor<ValidarTokenCommand, ValidacionTokenResult> {}
