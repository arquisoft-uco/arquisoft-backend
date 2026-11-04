package com.arquisoft.seguridad.application.autenticacion.command.primaryport.interactor.impl;

import com.arquisoft.seguridad.application.autenticacion.command.primaryport.interactor.ValidarTokenInteractor;
import com.arquisoft.seguridad.application.autenticacion.command.primaryport.mapper.ValidarTokenMapper;
import com.arquisoft.seguridad.application.autenticacion.command.primaryport.model.ValidarTokenCommand;
import com.arquisoft.seguridad.application.autenticacion.command.result.ValidacionTokenResult;
import com.arquisoft.seguridad.application.autenticacion.command.usecase.ValidarTokenUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidarTokenInteractorImpl implements ValidarTokenInteractor {

    private final ValidarTokenUseCase validarTokenUseCase;

    @Override
    public ValidacionTokenResult ejecutar(ValidarTokenCommand entrada) {
        return validarTokenUseCase.ejecutar(ValidarTokenMapper.toDomain(entrada));
    }
}
