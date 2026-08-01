package com.arquisoft.seguridad.application.auth.command.port.in;

import com.arquisoft.seguridad.application.auth.command.model.AuthenticateUserCommand;
import com.arquisoft.shared.usecase.UseCase;

public interface AuthenticateUserUseCase
        extends UseCase<AuthenticateUserCommand, AuthenticateUserUseCase.AuthResult> {

    record AuthResult(
            String accessToken,
            String refreshToken,
            long expiresIn,
            String tokenType,
            String scope
    ) {}
}
