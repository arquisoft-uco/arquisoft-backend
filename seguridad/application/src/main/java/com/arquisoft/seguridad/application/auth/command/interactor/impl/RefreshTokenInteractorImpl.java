package com.arquisoft.seguridad.application.auth.command.interactor.impl;

import com.arquisoft.seguridad.application.auth.command.interactor.RefreshTokenInteractor;
import com.arquisoft.seguridad.application.auth.command.usecase.RefreshTokenUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// El contexto seguridad no tiene DataSource propio (Keycloak + Redis), asi que el
// interactor no declara @Transactional: no hay unidad de trabajo que delimitar.
@Component
@RequiredArgsConstructor
public class RefreshTokenInteractorImpl implements RefreshTokenInteractor {

    private final RefreshTokenUseCase refreshTokenUseCase;

    @Override
    public RefreshTokenUseCase.RefreshResult ejecutar(String tokenRefresco) {
        return refreshTokenUseCase.ejecutar(tokenRefresco);
    }
}
