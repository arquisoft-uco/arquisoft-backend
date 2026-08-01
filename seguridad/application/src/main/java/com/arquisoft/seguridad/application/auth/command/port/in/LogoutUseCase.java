package com.arquisoft.seguridad.application.auth.command.port.in;

import com.arquisoft.seguridad.application.auth.command.model.TokenSesionCommand;
import com.arquisoft.shared.usecase.VoidUseCase;

public interface LogoutUseCase extends VoidUseCase<TokenSesionCommand> {}
