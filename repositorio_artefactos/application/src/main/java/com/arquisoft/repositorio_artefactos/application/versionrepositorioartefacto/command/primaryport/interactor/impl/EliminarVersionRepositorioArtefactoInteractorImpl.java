package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.EliminarVersionRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.mapper.EliminarVersionRepositorioArtefactoMapper;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model.EliminarVersionRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.EliminacionVersionRepositorioArtefactoResult;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.usecase.EliminarVersionRepositorioArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EliminarVersionRepositorioArtefactoInteractorImpl
        implements EliminarVersionRepositorioArtefactoInteractor {

    private final EliminarVersionRepositorioArtefactoUseCase eliminarVersionRepositorioArtefactoUseCase;

    @Override
    @Transactional(transactionManager = "repositorioArtefactosTransactionManager")
    public EliminacionVersionRepositorioArtefactoResult ejecutar(EliminarVersionRepositorioArtefactoCommand command) {
        return eliminarVersionRepositorioArtefactoUseCase.ejecutar(
                EliminarVersionRepositorioArtefactoMapper.toDomain(command));
    }
}
