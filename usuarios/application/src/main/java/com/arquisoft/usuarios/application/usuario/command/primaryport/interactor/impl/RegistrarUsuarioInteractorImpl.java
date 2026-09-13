package com.arquisoft.usuarios.application.usuario.command.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.usuario.command.primaryport.interactor.RegistrarUsuarioInteractor;
import com.arquisoft.usuarios.application.usuario.command.primaryport.mapper.RegistrarUsuarioMapper;
import com.arquisoft.usuarios.application.usuario.command.primaryport.model.RegistrarUsuarioCommand;
import com.arquisoft.usuarios.application.usuario.command.usecase.RegistrarUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarUsuarioInteractorImpl implements RegistrarUsuarioInteractor {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;

    @Override
    @Transactional(transactionManager = "usuariosTransactionManager")
    public UUID ejecutar(RegistrarUsuarioCommand command) {
        return registrarUsuarioUseCase.ejecutar(RegistrarUsuarioMapper.toDomain(command));
    }
}
