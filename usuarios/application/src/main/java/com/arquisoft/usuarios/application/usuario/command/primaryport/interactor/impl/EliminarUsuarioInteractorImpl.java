package com.arquisoft.usuarios.application.usuario.command.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.usuario.command.primaryport.interactor.EliminarUsuarioInteractor;
import com.arquisoft.usuarios.application.usuario.command.primaryport.mapper.EliminarUsuarioMapper;
import com.arquisoft.usuarios.application.usuario.command.primaryport.model.EliminarUsuarioCommand;
import com.arquisoft.usuarios.application.usuario.command.usecase.EliminarUsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EliminarUsuarioInteractorImpl implements EliminarUsuarioInteractor {

    private final EliminarUsuarioUseCase eliminarUsuarioUseCase;

    @Override
    @Transactional(transactionManager = "usuariosTransactionManager")
    public void ejecutar(EliminarUsuarioCommand command) {
        eliminarUsuarioUseCase.ejecutar(EliminarUsuarioMapper.toDomain(command));
    }
}
