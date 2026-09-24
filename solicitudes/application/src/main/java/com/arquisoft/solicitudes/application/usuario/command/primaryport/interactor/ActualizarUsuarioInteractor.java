package com.arquisoft.solicitudes.application.usuario.command.primaryport.interactor;

import com.arquisoft.solicitudes.application.usuario.command.primaryport.model.ActualizarUsuarioCommand;
import com.arquisoft.solicitudes.application.usuario.command.result.ActualizacionUsuarioResult;
import com.arquisoft.shared.interactor.Interactor;

public interface ActualizarUsuarioInteractor
        extends Interactor<ActualizarUsuarioCommand, ActualizacionUsuarioResult> {
}
