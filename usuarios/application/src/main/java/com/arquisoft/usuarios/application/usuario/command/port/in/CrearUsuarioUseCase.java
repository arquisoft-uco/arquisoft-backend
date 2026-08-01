package com.arquisoft.usuarios.application.usuario.command.port.in;

import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.usuarios.application.usuario.command.model.CrearUsuarioCommand;

import java.util.UUID;

public interface CrearUsuarioUseCase extends UseCase<CrearUsuarioCommand, UUID> {}
