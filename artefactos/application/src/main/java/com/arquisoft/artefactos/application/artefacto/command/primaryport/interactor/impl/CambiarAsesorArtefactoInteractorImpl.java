package com.arquisoft.artefactos.application.artefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.interactor.CambiarAsesorArtefactoInteractor;
import com.arquisoft.artefactos.application.artefacto.command.primaryport.mapper.CambiarAsesorArtefactoMapper;
import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.CambiarAsesorArtefactoCommand;
import com.arquisoft.artefactos.application.artefacto.command.usecase.CambiarAsesorArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CambiarAsesorArtefactoInteractorImpl implements CambiarAsesorArtefactoInteractor {

    private final CambiarAsesorArtefactoUseCase cambiarAsesorArtefactoUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public void ejecutar(CambiarAsesorArtefactoCommand command) {
        cambiarAsesorArtefactoUseCase.ejecutar(CambiarAsesorArtefactoMapper.toDomain(command));
    }
}
