package com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.interactor.PurgarPaquetesProyectoGradoInteractor;
import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.mapper.PurgarPaquetesProyectoGradoMapper;
import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.model.PurgarPaquetesProyectoGradoCommand;
import com.arquisoft.artefactos.application.paqueteentregable.command.result.PurgaPaquetesProyectoGradoResult;
import com.arquisoft.artefactos.application.paqueteentregable.command.usecase.PurgarPaquetesProyectoGradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PurgarPaquetesProyectoGradoInteractorImpl implements PurgarPaquetesProyectoGradoInteractor {

    private final PurgarPaquetesProyectoGradoUseCase purgarPaquetesProyectoGradoUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public PurgaPaquetesProyectoGradoResult ejecutar(PurgarPaquetesProyectoGradoCommand command) {
        return purgarPaquetesProyectoGradoUseCase.ejecutar(PurgarPaquetesProyectoGradoMapper.toDomain(command));
    }
}
