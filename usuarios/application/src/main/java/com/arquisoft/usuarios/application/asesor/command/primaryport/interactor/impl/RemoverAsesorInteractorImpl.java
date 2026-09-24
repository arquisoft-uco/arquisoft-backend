package com.arquisoft.usuarios.application.asesor.command.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.asesor.command.primaryport.interactor.RemoverAsesorInteractor;
import com.arquisoft.usuarios.application.asesor.command.primaryport.mapper.RemoverAsesorMapper;
import com.arquisoft.usuarios.application.asesor.command.primaryport.model.RemoverAsesorCommand;
import com.arquisoft.usuarios.application.asesor.command.usecase.RemoverAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverAsesorInteractorImpl implements RemoverAsesorInteractor {

    private final RemoverAsesorUseCase removerAsesorUseCase;

    @Override
    @Transactional(transactionManager = "usuariosTransactionManager")
    public void ejecutar(RemoverAsesorCommand command) {
        removerAsesorUseCase.ejecutar(RemoverAsesorMapper.toDomain(command));
    }
}
