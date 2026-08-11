package com.arquisoft.seguridad.application.auth.command.primaryport.interactor.impl;

import com.arquisoft.seguridad.application.auth.command.primaryport.interactor.RefrescarTokenInteractor;
import com.arquisoft.seguridad.application.auth.command.result.RefrescoTokenResult;
import com.arquisoft.seguridad.application.auth.command.usecase.RefrescarTokenUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// El contexto seguridad no tiene DataSource propio (Keycloak + Redis), asi que el
// interactor no declara @Transactional: no hay unidad de trabajo que delimitar.
@Component
@RequiredArgsConstructor
public class RefrescarTokenInteractorImpl implements RefrescarTokenInteractor {

    private final RefrescarTokenUseCase refrescarTokenUseCase;

    @Override
    public RefrescoTokenResult ejecutar(String tokenRefresco) {
        return refrescarTokenUseCase.ejecutar(tokenRefresco);
    }
}
