package com.arquisoft.seguridad.application.autenticacion.command.primaryport.interactor.impl;

import com.arquisoft.seguridad.application.autenticacion.command.primaryport.interactor.AutenticarUsuarioInteractor;
import com.arquisoft.seguridad.application.autenticacion.command.primaryport.mapper.AutenticarUsuarioMapper;
import com.arquisoft.seguridad.application.autenticacion.command.primaryport.model.AutenticarUsuarioCommand;
import com.arquisoft.seguridad.application.autenticacion.command.result.AutenticacionResult;
import com.arquisoft.seguridad.application.autenticacion.command.usecase.AutenticarUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AutenticarUsuarioInteractorImpl implements AutenticarUsuarioInteractor {

    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    @Override
    public AutenticacionResult ejecutar(AutenticarUsuarioCommand entrada) {
        return autenticarUsuarioUseCase.ejecutar(AutenticarUsuarioMapper.toDomain(entrada));
    }
}
