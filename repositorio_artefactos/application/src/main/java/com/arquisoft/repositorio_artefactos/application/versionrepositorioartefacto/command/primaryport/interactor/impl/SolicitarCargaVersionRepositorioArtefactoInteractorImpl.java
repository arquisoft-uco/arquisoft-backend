package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.SolicitarCargaVersionRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.mapper.SolicitarCargaVersionRepositorioArtefactoMapper;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model.SolicitarCargaVersionRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.CargaVersionRepositorioArtefactoResult;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.usecase.SolicitarCargaVersionRepositorioArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SolicitarCargaVersionRepositorioArtefactoInteractorImpl
        implements SolicitarCargaVersionRepositorioArtefactoInteractor {

    private final SolicitarCargaVersionRepositorioArtefactoUseCase solicitarCargaUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "repositorioArtefactosTransactionManager")
    public CargaVersionRepositorioArtefactoResult ejecutar(SolicitarCargaVersionRepositorioArtefactoCommand command) {
        return solicitarCargaUseCase.ejecutar(SolicitarCargaVersionRepositorioArtefactoMapper.toDomain(command));
    }
}
