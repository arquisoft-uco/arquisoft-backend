package com.arquisoft.entregables.application.artefacto.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.artefacto.command.primaryport.interactor.PurgarArtefactosProyectoGradoInteractor;
import com.arquisoft.entregables.application.artefacto.command.primaryport.mapper.PurgarArtefactosProyectoGradoMapper;
import com.arquisoft.entregables.application.artefacto.command.primaryport.model.PurgarArtefactosProyectoGradoCommand;
import com.arquisoft.entregables.application.artefacto.command.result.PurgaArtefactosProyectoGradoResult;
import com.arquisoft.entregables.application.artefacto.command.usecase.PurgarArtefactosProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PurgarArtefactosProyectoGradoInteractorImpl implements PurgarArtefactosProyectoGradoInteractor {

    private final PurgarArtefactosProyectoGradoUseCase purgarArtefactosProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "entregablesTransactionManager")
    public PurgaArtefactosProyectoGradoResult ejecutar(PurgarArtefactosProyectoGradoCommand command) {
        return purgarArtefactosProyectoGradoUseCase.ejecutar(PurgarArtefactosProyectoGradoMapper.toDomain(command));
    }
}
