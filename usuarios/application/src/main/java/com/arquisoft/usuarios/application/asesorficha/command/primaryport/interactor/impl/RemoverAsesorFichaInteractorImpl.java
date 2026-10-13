package com.arquisoft.usuarios.application.asesorficha.command.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.asesorficha.command.primaryport.interactor.RemoverAsesorFichaInteractor;
import com.arquisoft.usuarios.application.asesorficha.command.primaryport.mapper.RemoverAsesorFichaMapper;
import com.arquisoft.usuarios.application.asesorficha.command.primaryport.model.RemoverAsesorFichaCommand;
import com.arquisoft.usuarios.application.asesorficha.command.usecase.RemoverAsesorFichaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverAsesorFichaInteractorImpl implements RemoverAsesorFichaInteractor {

    private final RemoverAsesorFichaUseCase removerAsesorFichaUseCase;

    @Override
    @Transactional(transactionManager = "usuariosTransactionManager")
    public void ejecutar(RemoverAsesorFichaCommand command) {
        removerAsesorFichaUseCase.ejecutar(RemoverAsesorFichaMapper.toDomain(command));
    }
}
