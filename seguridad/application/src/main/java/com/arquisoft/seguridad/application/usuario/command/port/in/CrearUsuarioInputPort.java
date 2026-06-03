package com.arquisoft.seguridad.application.usuario.command.port.in;

import com.arquisoft.seguridad.application.usuario.command.model.CrearUsuarioCommand;
import com.arquisoft.shared.inputport.InputPort;

import java.util.UUID;

/**
 * Puerto de entrada — caso de uso para crear un nuevo usuario en el sistema.
 *
 * <p>El use case orquesta: crear el aggregate, persistirlo y drenar los eventos
 * acumulados por el aggregate hacia el bus de mensajería.
 */
public interface CrearUsuarioInputPort extends InputPort<CrearUsuarioCommand, UUID> {}
