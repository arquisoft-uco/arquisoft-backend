package com.arquisoft.seguridad.application.autenticacion.command.primaryport.interactor;

import com.arquisoft.seguridad.application.autenticacion.command.primaryport.model.AutenticarUsuarioCommand;
import com.arquisoft.seguridad.application.autenticacion.command.result.AutenticacionResult;
import com.arquisoft.shared.interactor.Interactor;

public interface AutenticarUsuarioInteractor
        extends Interactor<AutenticarUsuarioCommand, AutenticacionResult> {}
