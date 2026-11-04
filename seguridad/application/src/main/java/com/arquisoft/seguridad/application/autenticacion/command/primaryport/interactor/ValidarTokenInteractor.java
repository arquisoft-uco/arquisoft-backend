package com.arquisoft.seguridad.application.autenticacion.command.primaryport.interactor;

import com.arquisoft.seguridad.application.autenticacion.command.primaryport.model.ValidarTokenCommand;
import com.arquisoft.seguridad.application.autenticacion.command.result.ValidacionTokenResult;
import com.arquisoft.shared.interactor.Interactor;

public interface ValidarTokenInteractor
        extends Interactor<ValidarTokenCommand, ValidacionTokenResult> {}
