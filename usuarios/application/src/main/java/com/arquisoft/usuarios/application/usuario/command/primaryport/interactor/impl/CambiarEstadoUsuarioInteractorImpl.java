package com.arquisoft.usuarios.application.usuario.command.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.usuario.command.primaryport.interactor.CambiarEstadoUsuarioInteractor;
import com.arquisoft.usuarios.application.usuario.command.primaryport.mapper.CambiarEstadoUsuarioMapper;
import com.arquisoft.usuarios.application.usuario.command.primaryport.model.CambiarEstadoUsuarioCommand;
import com.arquisoft.usuarios.application.usuario.command.usecase.CambiarEstadoUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CambiarEstadoUsuarioInteractorImpl implements CambiarEstadoUsuarioInteractor {

    private final CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase;

    @Override
    @Transactional(transactionManager = "usuariosTransactionManager")
    public void ejecutar(CambiarEstadoUsuarioCommand command) {
        cambiarEstadoUsuarioUseCase.ejecutar(CambiarEstadoUsuarioMapper.toDomain(command));
    }
}
