package com.arquisoft.fichas.application.representantecomite.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.representantecomite.command.primaryport.interactor.RemoverRepresentanteComiteInteractor;
import com.arquisoft.fichas.application.representantecomite.command.primaryport.mapper.RemoverRepresentanteComiteMapper;
import com.arquisoft.fichas.application.representantecomite.command.primaryport.model.RemoverRepresentanteComiteCommand;
import com.arquisoft.fichas.application.representantecomite.command.result.RemocionRepresentanteComiteResult;
import com.arquisoft.fichas.application.representantecomite.command.usecase.RemoverRepresentanteComiteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverRepresentanteComiteInteractorImpl implements RemoverRepresentanteComiteInteractor {

    private final RemoverRepresentanteComiteUseCase removerRepresentanteComiteUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public RemocionRepresentanteComiteResult ejecutar(RemoverRepresentanteComiteCommand command) {
        return removerRepresentanteComiteUseCase.ejecutar(RemoverRepresentanteComiteMapper.toDomain(command));
    }
}
