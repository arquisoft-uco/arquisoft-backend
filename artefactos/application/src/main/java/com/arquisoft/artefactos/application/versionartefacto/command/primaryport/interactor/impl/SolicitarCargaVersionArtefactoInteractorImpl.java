package com.arquisoft.artefactos.application.versionartefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.interactor.SolicitarCargaVersionArtefactoInteractor;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.mapper.SolicitarCargaVersionArtefactoMapper;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.SolicitarCargaVersionArtefactoCommand;
import com.arquisoft.artefactos.application.versionartefacto.command.result.CargaVersionArtefactoResult;
import com.arquisoft.artefactos.application.versionartefacto.command.usecase.SolicitarCargaVersionArtefactoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SolicitarCargaVersionArtefactoInteractorImpl implements SolicitarCargaVersionArtefactoInteractor {

    private final SolicitarCargaVersionArtefactoUseCase solicitarCargaUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "artefactosTransactionManager")
    public CargaVersionArtefactoResult ejecutar(SolicitarCargaVersionArtefactoCommand command) {
        return solicitarCargaUseCase.ejecutar(SolicitarCargaVersionArtefactoMapper.toDomain(command));
    }
}
