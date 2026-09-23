package com.arquisoft.usuarios.application.usuario.command.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.usuario.command.primaryport.interactor.ModificarUsuarioInteractor;
import com.arquisoft.usuarios.application.usuario.command.primaryport.mapper.ModificarUsuarioMapper;
import com.arquisoft.usuarios.application.usuario.command.primaryport.model.ModificarUsuarioCommand;
import com.arquisoft.usuarios.application.usuario.command.usecase.ModificarUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModificarUsuarioInteractorImpl implements ModificarUsuarioInteractor {

    private final ModificarUsuarioUseCase modificarUsuarioUseCase;

    @Override
    @Transactional(transactionManager = "usuariosTransactionManager")
    public void ejecutar(ModificarUsuarioCommand command) {
        modificarUsuarioUseCase.ejecutar(ModificarUsuarioMapper.toDomain(command));
    }
}
