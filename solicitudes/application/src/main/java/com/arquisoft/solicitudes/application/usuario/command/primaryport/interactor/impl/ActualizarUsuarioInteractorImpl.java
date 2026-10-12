package com.arquisoft.solicitudes.application.usuario.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.usuario.command.primaryport.interactor.ActualizarUsuarioInteractor;
import com.arquisoft.solicitudes.application.usuario.command.primaryport.mapper.ActualizarUsuarioMapper;
import com.arquisoft.solicitudes.application.usuario.command.primaryport.model.ActualizarUsuarioCommand;
import com.arquisoft.solicitudes.application.usuario.command.result.ActualizacionUsuarioResult;
import com.arquisoft.solicitudes.application.usuario.command.usecase.ActualizarUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ActualizarUsuarioInteractorImpl implements ActualizarUsuarioInteractor {

    private final ActualizarUsuarioUseCase actualizarUsuarioUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public ActualizacionUsuarioResult ejecutar(ActualizarUsuarioCommand command) {
        return actualizarUsuarioUseCase.ejecutar(ActualizarUsuarioMapper.toDomain(command));
    }
}
