package com.arquisoft.usuarios.application.usuario.command.usecase;

import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.usuarios.application.usuario.command.primaryport.model.CrearUsuarioCommand;

import java.util.UUID;

public interface CrearUsuarioUseCase extends UseCase<CrearUsuarioCommand, UUID> {}
