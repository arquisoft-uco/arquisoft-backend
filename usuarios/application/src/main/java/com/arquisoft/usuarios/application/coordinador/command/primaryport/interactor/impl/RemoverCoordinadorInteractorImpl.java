package com.arquisoft.usuarios.application.coordinador.command.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.coordinador.command.primaryport.interactor.RemoverCoordinadorInteractor;
import com.arquisoft.usuarios.application.coordinador.command.primaryport.mapper.RemoverCoordinadorMapper;
import com.arquisoft.usuarios.application.coordinador.command.primaryport.model.RemoverCoordinadorCommand;
import com.arquisoft.usuarios.application.coordinador.command.usecase.RemoverCoordinadorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverCoordinadorInteractorImpl implements RemoverCoordinadorInteractor {

    private final RemoverCoordinadorUseCase removerCoordinadorUseCase;

    @Override
    @Transactional(transactionManager = "usuariosTransactionManager")
    public void ejecutar(RemoverCoordinadorCommand command) {
        removerCoordinadorUseCase.ejecutar(RemoverCoordinadorMapper.toDomain(command));
    }
}
