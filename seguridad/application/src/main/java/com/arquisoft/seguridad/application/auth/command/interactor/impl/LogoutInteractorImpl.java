package com.arquisoft.seguridad.application.auth.command.interactor.impl;

import com.arquisoft.seguridad.application.auth.command.interactor.LogoutInteractor;
import com.arquisoft.seguridad.application.auth.command.model.TokenSesionCommand;
import com.arquisoft.seguridad.application.auth.command.usecase.LogoutUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// El contexto seguridad no tiene DataSource propio (Keycloak + Redis), asi que el
// interactor no declara @Transactional: no hay unidad de trabajo que delimitar.
@Component
@RequiredArgsConstructor
public class LogoutInteractorImpl implements LogoutInteractor {

    private final LogoutUseCase logoutUseCase;

    @Override
    public void ejecutar(TokenSesionCommand command) {
        logoutUseCase.ejecutar(command);
    }
}
