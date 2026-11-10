package com.arquisoft.artefactos.application.versionartefacto.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.interactor.PurgarVersionesProyectoGradoInteractor;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.mapper.PurgarVersionesProyectoGradoMapper;
import com.arquisoft.artefactos.application.versionartefacto.command.primaryport.model.PurgarVersionesProyectoGradoCommand;
import com.arquisoft.artefactos.application.versionartefacto.command.result.PurgaVersionesProyectoGradoResult;
import com.arquisoft.artefactos.application.versionartefacto.command.usecase.PurgarVersionesProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PurgarVersionesProyectoGradoInteractorImpl implements PurgarVersionesProyectoGradoInteractor {

    private final PurgarVersionesProyectoGradoUseCase purgarVersionesProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public PurgaVersionesProyectoGradoResult ejecutar(PurgarVersionesProyectoGradoCommand command) {
        return purgarVersionesProyectoGradoUseCase.ejecutar(PurgarVersionesProyectoGradoMapper.toDomain(command));
    }
}
