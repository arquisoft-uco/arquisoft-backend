package com.arquisoft.seguridad.application.auth.command.primaryport.interactor;

import com.arquisoft.seguridad.application.auth.command.primaryport.model.AutenticarUsuarioCommand;
import com.arquisoft.seguridad.application.auth.command.result.AutenticacionResult;
import com.arquisoft.shared.interactor.Interactor;

public interface AutenticarUsuarioInteractor
        extends Interactor<AutenticarUsuarioCommand, AutenticacionResult> {}
