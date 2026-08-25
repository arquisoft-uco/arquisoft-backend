package com.arquisoft.seguridad.application.auth.command.primaryport.interactor.impl;

import com.arquisoft.seguridad.application.auth.command.primaryport.interactor.RefrescarTokenInteractor;
import com.arquisoft.seguridad.application.auth.command.primaryport.mapper.RefrescarTokenMapper;
import com.arquisoft.seguridad.application.auth.command.primaryport.model.RefrescarTokenCommand;
import com.arquisoft.seguridad.application.auth.command.result.RefrescoTokenResult;
import com.arquisoft.seguridad.application.auth.command.usecase.RefrescarTokenUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefrescarTokenInteractorImpl implements RefrescarTokenInteractor {

    private final RefrescarTokenUseCase refrescarTokenUseCase;

    @Override
    public RefrescoTokenResult ejecutar(RefrescarTokenCommand entrada) {
        return refrescarTokenUseCase.ejecutar(RefrescarTokenMapper.toDomain(entrada));
    }
}
