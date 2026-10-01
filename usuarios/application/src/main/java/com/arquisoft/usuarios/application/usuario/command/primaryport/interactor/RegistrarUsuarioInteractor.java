package com.arquisoft.usuarios.application.usuario.command.primaryport.interactor;

import com.arquisoft.usuarios.application.usuario.command.primaryport.model.RegistrarUsuarioCommand;
import com.arquisoft.shared.interactor.Interactor;

import java.util.UUID;

public interface RegistrarUsuarioInteractor extends Interactor<RegistrarUsuarioCommand, UUID> {}
