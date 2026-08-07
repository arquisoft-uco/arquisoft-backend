package com.arquisoft.seguridad.application.auth.command.interactor.impl;

import com.arquisoft.seguridad.application.auth.command.interactor.ValidarTokenInteractor;
import com.arquisoft.seguridad.application.auth.command.result.ValidacionTokenResult;
import com.arquisoft.seguridad.application.auth.command.usecase.ValidarTokenUseCase;
import com.arquisoft.seguridad.domain.auth.aggregate.TokenDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// El contexto seguridad no tiene DataSource propio (Keycloak + Redis), asi que el
// interactor no declara @Transactional: no hay unidad de trabajo que delimitar.
@Component
@RequiredArgsConstructor
public class ValidarTokenInteractorImpl implements ValidarTokenInteractor {

    private final ValidarTokenUseCase validarTokenUseCase;

    @Override
    public ValidacionTokenResult ejecutar(TokenDomain token) {
        return validarTokenUseCase.ejecutar(token);
    }
}
