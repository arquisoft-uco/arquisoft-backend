package com.arquisoft.usuarios.application.jurado.command.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.jurado.command.primaryport.interactor.RemoverJuradoInteractor;
import com.arquisoft.usuarios.application.jurado.command.primaryport.mapper.RemoverJuradoMapper;
import com.arquisoft.usuarios.application.jurado.command.primaryport.model.RemoverJuradoCommand;
import com.arquisoft.usuarios.application.jurado.command.usecase.RemoverJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverJuradoInteractorImpl implements RemoverJuradoInteractor {

    private final RemoverJuradoUseCase removerJuradoUseCase;

    @Override
    @Transactional(transactionManager = "usuariosTransactionManager")
    public void ejecutar(RemoverJuradoCommand command) {
        removerJuradoUseCase.ejecutar(RemoverJuradoMapper.toDomain(command));
    }
}
