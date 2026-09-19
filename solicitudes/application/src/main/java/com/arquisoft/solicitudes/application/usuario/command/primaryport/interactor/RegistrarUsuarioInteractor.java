package com.arquisoft.solicitudes.application.usuario.command.primaryport.interactor;

import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.solicitudes.application.usuario.command.primaryport.model.RegistrarUsuarioCommand;
import com.arquisoft.solicitudes.application.usuario.command.result.AgregacionUsuarioResult;

public interface RegistrarUsuarioInteractor
        extends Interactor<RegistrarUsuarioCommand, AgregacionUsuarioResult> {
}
