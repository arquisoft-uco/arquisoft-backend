package com.arquisoft.solicitudes.application.usuario.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.usuario.command.primaryport.interactor.RegistrarUsuarioInteractor;
import com.arquisoft.solicitudes.application.usuario.command.primaryport.mapper.RegistrarUsuarioMapper;
import com.arquisoft.solicitudes.application.usuario.command.primaryport.model.RegistrarUsuarioCommand;
import com.arquisoft.solicitudes.application.usuario.command.result.AgregacionUsuarioResult;
import com.arquisoft.solicitudes.application.usuario.command.usecase.RegistrarUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RegistrarUsuarioInteractorImpl implements RegistrarUsuarioInteractor {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public AgregacionUsuarioResult ejecutar(RegistrarUsuarioCommand input) {
        return registrarUsuarioUseCase.ejecutar(RegistrarUsuarioMapper.toDomain(input));
    }
}
