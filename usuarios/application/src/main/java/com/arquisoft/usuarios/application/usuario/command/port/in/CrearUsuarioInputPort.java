package com.arquisoft.usuarios.application.usuario.command.port.in;

import com.arquisoft.shared.inputport.InputPort;
import com.arquisoft.usuarios.application.usuario.command.model.CrearUsuarioCommand;

import java.util.UUID;

public interface CrearUsuarioInputPort extends InputPort<CrearUsuarioCommand, UUID> {}
