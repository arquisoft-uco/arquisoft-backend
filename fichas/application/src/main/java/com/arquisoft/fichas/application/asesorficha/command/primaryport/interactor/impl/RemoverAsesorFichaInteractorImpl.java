package com.arquisoft.fichas.application.asesorficha.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.asesorficha.command.primaryport.interactor.RemoverAsesorFichaInteractor;
import com.arquisoft.fichas.application.asesorficha.command.primaryport.mapper.RemoverAsesorFichaMapper;
import com.arquisoft.fichas.application.asesorficha.command.primaryport.model.RemoverAsesorFichaCommand;
import com.arquisoft.fichas.application.asesorficha.command.result.RemocionAsesorFichaResult;
import com.arquisoft.fichas.application.asesorficha.command.usecase.RemoverAsesorFichaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverAsesorFichaInteractorImpl implements RemoverAsesorFichaInteractor {

    private final RemoverAsesorFichaUseCase removerAsesorFichaUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public RemocionAsesorFichaResult ejecutar(RemoverAsesorFichaCommand command) {
        return removerAsesorFichaUseCase.ejecutar(RemoverAsesorFichaMapper.toDomain(command));
    }
}
