package com.arquisoft.entregables.application.artefacto.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.artefacto.command.primaryport.interactor.OmitirArtefactoInteractor;
import com.arquisoft.entregables.application.artefacto.command.primaryport.mapper.OmitirArtefactoMapper;
import com.arquisoft.entregables.application.artefacto.command.primaryport.model.OmitirArtefactoCommand;
import com.arquisoft.entregables.application.artefacto.command.result.OmisionArtefactoResult;
import com.arquisoft.entregables.application.artefacto.command.usecase.OmitirArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OmitirArtefactoInteractorImpl implements OmitirArtefactoInteractor {

    private final OmitirArtefactoUseCase omitirArtefactoUseCase;

    @Override
    @Transactional(transactionManager = "entregablesTransactionManager")
    public OmisionArtefactoResult ejecutar(OmitirArtefactoCommand command) {
        return omitirArtefactoUseCase.ejecutar(OmitirArtefactoMapper.toDomain(command));
    }
}
