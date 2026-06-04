package com.arquisoft.seguridad.application.auth.command.port.in;

import com.arquisoft.seguridad.application.auth.command.model.TokenSesionCommand;
import com.arquisoft.shared.inputport.VoidInputPort;

public interface LogoutInputPort extends VoidInputPort<TokenSesionCommand> {}
