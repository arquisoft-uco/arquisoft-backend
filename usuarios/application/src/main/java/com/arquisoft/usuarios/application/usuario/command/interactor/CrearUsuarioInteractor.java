package com.arquisoft.usuarios.application.usuario.command.interactor;

import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.usuarios.application.usuario.command.model.CrearUsuarioCommand;

import java.util.UUID;

public interface CrearUsuarioInteractor extends Interactor<CrearUsuarioCommand, UUID> {}
