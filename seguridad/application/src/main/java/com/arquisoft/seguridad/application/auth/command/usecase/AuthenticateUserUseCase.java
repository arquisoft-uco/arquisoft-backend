package com.arquisoft.seguridad.application.auth.command.usecase;

import com.arquisoft.seguridad.application.auth.command.model.AuthenticateUserCommand;
import com.arquisoft.seguridad.application.auth.command.result.AutenticacionResult;
import com.arquisoft.shared.usecase.UseCase;

public interface AuthenticateUserUseCase extends UseCase<AuthenticateUserCommand, AutenticacionResult> {}
