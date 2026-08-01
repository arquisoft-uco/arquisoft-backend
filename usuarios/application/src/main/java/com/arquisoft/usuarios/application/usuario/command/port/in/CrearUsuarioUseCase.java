package com.arquisoft.usuarios.application.usuario.command.port.in;

import com.arquisoft.shared.usecase.UseCase;
import com.arquisoft.usuarios.application.usuario.command.model.CrearUsuarioCommand;

import java.util.UUID;

/**
 * Puerto de entrada — caso de uso para crear un nuevo usuario en el sistema.
 *
 * <p>El use case orquesta: crear el aggregate, persistirlo y drenar los eventos
 * acumulados por el aggregate hacia el bus de mensajería.
 */
public interface CrearUsuarioUseCase extends UseCase<CrearUsuarioCommand, UUID> {}
