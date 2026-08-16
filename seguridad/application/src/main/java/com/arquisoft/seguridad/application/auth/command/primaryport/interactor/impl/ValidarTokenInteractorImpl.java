package com.arquisoft.seguridad.application.auth.command.primaryport.interactor.impl;

import com.arquisoft.seguridad.application.auth.command.primaryport.interactor.ValidarTokenInteractor;
import com.arquisoft.seguridad.application.auth.command.result.ValidacionTokenResult;
import com.arquisoft.seguridad.application.auth.command.usecase.ValidarTokenUseCase;
import com.arquisoft.seguridad.domain.auth.TokenDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidarTokenInteractorImpl implements ValidarTokenInteractor {

    private final ValidarTokenUseCase validarTokenUseCase;

    @Override
    public ValidacionTokenResult ejecutar(TokenDomain token) {
        return validarTokenUseCase.ejecutar(token);
    }
}
