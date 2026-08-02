package com.arquisoft.seguridad.application.auth.command.interactor.impl;

import com.arquisoft.seguridad.application.auth.command.interactor.ValidateTokenInteractor;
import com.arquisoft.seguridad.application.auth.command.usecase.ValidateTokenUseCase;
import com.arquisoft.seguridad.domain.auth.aggregate.TokenAggregate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// El contexto seguridad no tiene DataSource propio (Keycloak + Redis), asi que el
// interactor no declara @Transactional: no hay unidad de trabajo que delimitar.
@Component
@RequiredArgsConstructor
public class ValidateTokenInteractorImpl implements ValidateTokenInteractor {

    private final ValidateTokenUseCase validateTokenUseCase;

    @Override
    public ValidateTokenUseCase.ValidationResult ejecutar(TokenAggregate token) {
        return validateTokenUseCase.ejecutar(token);
    }
}
