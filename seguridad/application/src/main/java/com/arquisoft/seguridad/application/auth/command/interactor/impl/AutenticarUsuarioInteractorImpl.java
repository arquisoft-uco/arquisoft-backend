package com.arquisoft.seguridad.application.auth.command.interactor.impl;

import com.arquisoft.seguridad.application.auth.command.interactor.AutenticarUsuarioInteractor;
import com.arquisoft.seguridad.application.auth.command.model.AutenticarUsuarioCommand;
import com.arquisoft.seguridad.application.auth.command.result.AutenticacionResult;
import com.arquisoft.seguridad.application.auth.command.usecase.AutenticarUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// El contexto seguridad no tiene DataSource propio (Keycloak + Redis), asi que el
// interactor no declara @Transactional: no hay unidad de trabajo que delimitar.
@Component
@RequiredArgsConstructor
public class AutenticarUsuarioInteractorImpl implements AutenticarUsuarioInteractor {

    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    @Override
    public AutenticacionResult ejecutar(AutenticarUsuarioCommand command) {
        return autenticarUsuarioUseCase.ejecutar(command);
    }
}
