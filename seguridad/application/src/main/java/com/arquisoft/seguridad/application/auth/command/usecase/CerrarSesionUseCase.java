package com.arquisoft.seguridad.application.auth.command.usecase;

import com.arquisoft.seguridad.application.auth.command.primaryport.model.TokenSesionCommand;
import com.arquisoft.shared.usecase.VoidUseCase;

public interface CerrarSesionUseCase extends VoidUseCase<TokenSesionCommand> {}
