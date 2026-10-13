package com.arquisoft.usuarios.application.administrador.command.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.administrador.command.primaryport.interactor.RemoverAdministradorInteractor;
import com.arquisoft.usuarios.application.administrador.command.primaryport.mapper.RemoverAdministradorMapper;
import com.arquisoft.usuarios.application.administrador.command.primaryport.model.RemoverAdministradorCommand;
import com.arquisoft.usuarios.application.administrador.command.usecase.RemoverAdministradorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverAdministradorInteractorImpl implements RemoverAdministradorInteractor {

    private final RemoverAdministradorUseCase removerAdministradorUseCase;

    @Override
    @Transactional(transactionManager = "usuariosTransactionManager")
    public void ejecutar(RemoverAdministradorCommand command) {
        removerAdministradorUseCase.ejecutar(RemoverAdministradorMapper.toDomain(command));
    }
}
