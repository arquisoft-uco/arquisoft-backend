package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.AgregarVersionRepositorioArtefactoInteractor;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.mapper.AgregarVersionRepositorioArtefactoMapper;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.model.AgregarVersionRepositorioArtefactoCommand;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.result.AgregacionVersionRepositorioArtefactoResult;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.usecase.AgregarVersionRepositorioArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarVersionRepositorioArtefactoInteractorImpl implements AgregarVersionRepositorioArtefactoInteractor {

    private final AgregarVersionRepositorioArtefactoUseCase agregarVersionRepositorioArtefactoUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public AgregacionVersionRepositorioArtefactoResult ejecutar(AgregarVersionRepositorioArtefactoCommand command) {
        return agregarVersionRepositorioArtefactoUseCase.ejecutar(
                AgregarVersionRepositorioArtefactoMapper.toDomain(command));
    }
}
