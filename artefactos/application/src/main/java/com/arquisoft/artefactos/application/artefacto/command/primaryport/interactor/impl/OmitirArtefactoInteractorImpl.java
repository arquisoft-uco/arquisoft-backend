package com.arquisoft.artefactos.application.artefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.interactor.OmitirArtefactoInteractor;
import com.arquisoft.artefactos.application.artefacto.command.primaryport.mapper.OmitirArtefactoMapper;
import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.OmitirArtefactoCommand;
import com.arquisoft.artefactos.application.artefacto.command.usecase.OmitirArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OmitirArtefactoInteractorImpl implements OmitirArtefactoInteractor {

    private final OmitirArtefactoUseCase omitirArtefactoUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public void ejecutar(OmitirArtefactoCommand command) {
        omitirArtefactoUseCase.ejecutar(OmitirArtefactoMapper.toDomain(command));
    }
}
