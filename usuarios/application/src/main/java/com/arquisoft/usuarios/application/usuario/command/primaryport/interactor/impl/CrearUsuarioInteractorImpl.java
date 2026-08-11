package com.arquisoft.usuarios.application.usuario.command.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.usuario.command.primaryport.interactor.CrearUsuarioInteractor;
import com.arquisoft.usuarios.application.usuario.command.primaryport.model.CrearUsuarioCommand;
import com.arquisoft.usuarios.application.usuario.command.usecase.CrearUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CrearUsuarioInteractorImpl implements CrearUsuarioInteractor {

    private final CrearUsuarioUseCase crearUsuarioUseCase;

    @Override
    @Transactional(transactionManager = "usuariosTransactionManager")
    public UUID ejecutar(CrearUsuarioCommand command) {
        return crearUsuarioUseCase.ejecutar(command);
    }
}
