package com.arquisoft.artefactos.application.artefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.interactor.AsignarArtefactosInteractor;
import com.arquisoft.artefactos.application.artefacto.command.primaryport.mapper.AsignarArtefactosMapper;
import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.AsignarArtefactosCommand;
import com.arquisoft.artefactos.application.artefacto.command.result.ArtefactosAsignadosResult;
import com.arquisoft.artefactos.application.artefacto.command.usecase.AsignarArtefactosUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AsignarArtefactosInteractorImpl implements AsignarArtefactosInteractor {

    private final AsignarArtefactosUseCase asignarArtefactosUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public ArtefactosAsignadosResult ejecutar(AsignarArtefactosCommand command) {
        return asignarArtefactosUseCase.ejecutar(AsignarArtefactosMapper.toDomain(command));
    }
}
