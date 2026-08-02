package com.arquisoft.seguridad.application.auth.command.interactor.impl;

import com.arquisoft.seguridad.application.auth.command.interactor.AuthenticateUserInteractor;
import com.arquisoft.seguridad.application.auth.command.model.AuthenticateUserCommand;
import com.arquisoft.seguridad.application.auth.command.usecase.AuthenticateUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// El contexto seguridad no tiene DataSource propio (Keycloak + Redis), asi que el
// interactor no declara @Transactional: no hay unidad de trabajo que delimitar.
@Component
@RequiredArgsConstructor
public class AuthenticateUserInteractorImpl implements AuthenticateUserInteractor {

    private final AuthenticateUserUseCase authenticateUserUseCase;

    @Override
    public AuthenticateUserUseCase.AuthResult ejecutar(AuthenticateUserCommand command) {
        return authenticateUserUseCase.ejecutar(command);
    }
}
