package com.arquisoft.seguridad.application.auth.command.port.in;

import com.arquisoft.seguridad.application.auth.command.model.AuthenticateUserCommand;
import com.arquisoft.shared.inputport.InputPort;

public interface AuthenticateUserInputPort
        extends InputPort<AuthenticateUserCommand, AuthenticateUserInputPort.AuthResult> {

    record AuthResult(
            String accessToken,
            String refreshToken,
            long expiresIn,
            String tokenType,
            String scope
    ) {}
}
