package com.arquisoft.entregables.application.artefacto.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.artefacto.command.primaryport.interactor.ReactivarArtefactoInteractor;
import com.arquisoft.entregables.application.artefacto.command.primaryport.mapper.ReactivarArtefactoMapper;
import com.arquisoft.entregables.application.artefacto.command.primaryport.model.ReactivarArtefactoCommand;
import com.arquisoft.entregables.application.artefacto.command.result.ReactivacionArtefactoResult;
import com.arquisoft.entregables.application.artefacto.command.usecase.ReactivarArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReactivarArtefactoInteractorImpl implements ReactivarArtefactoInteractor {

    private final ReactivarArtefactoUseCase reactivarArtefactoUseCase;

    @Override
    @Transactional(transactionManager = "entregablesTransactionManager")
    public ReactivacionArtefactoResult ejecutar(ReactivarArtefactoCommand command) {
        return reactivarArtefactoUseCase.ejecutar(ReactivarArtefactoMapper.toDomain(command));
    }
}
