package com.arquisoft.seguridad.application.auth.command.interactor;

import com.arquisoft.seguridad.application.auth.command.model.AutenticarUsuarioCommand;
import com.arquisoft.seguridad.application.auth.command.result.AutenticacionResult;
import com.arquisoft.shared.interactor.Interactor;

public interface AutenticarUsuarioInteractor
        extends Interactor<AutenticarUsuarioCommand, AutenticacionResult> {}
