package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.SubirVersionRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.mapper.SubirVersionRepositorioArtefactoMapper;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model.SubirVersionRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.VersionRepositorioArtefactoSubidaResult;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.usecase.SubirVersionRepositorioArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SubirVersionRepositorioArtefactoInteractorImpl implements SubirVersionRepositorioArtefactoInteractor {

    private final SubirVersionRepositorioArtefactoUseCase subirVersionUseCase;

    @Override
    @Transactional(transactionManager = "repositorioArtefactosTransactionManager")
    public VersionRepositorioArtefactoSubidaResult ejecutar(SubirVersionRepositorioArtefactoCommand command) {
        return subirVersionUseCase.ejecutar(SubirVersionRepositorioArtefactoMapper.toDomain(command));
    }
}
