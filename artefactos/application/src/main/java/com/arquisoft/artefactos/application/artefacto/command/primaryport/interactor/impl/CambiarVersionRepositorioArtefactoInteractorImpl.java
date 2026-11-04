package com.arquisoft.artefactos.application.artefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.interactor.CambiarVersionRepositorioArtefactoInteractor;
import com.arquisoft.artefactos.application.artefacto.command.primaryport.mapper.CambiarVersionRepositorioArtefactoMapper;
import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.CambiarVersionRepositorioArtefactoCommand;
import com.arquisoft.artefactos.application.artefacto.command.usecase.CambiarVersionRepositorioArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CambiarVersionRepositorioArtefactoInteractorImpl implements CambiarVersionRepositorioArtefactoInteractor {

    private final CambiarVersionRepositorioArtefactoUseCase cambiarVersionRepositorioArtefactoUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public void ejecutar(CambiarVersionRepositorioArtefactoCommand command) {
        cambiarVersionRepositorioArtefactoUseCase.ejecutar(
                CambiarVersionRepositorioArtefactoMapper.toDomain(command));
    }
}
