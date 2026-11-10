package com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.interactor.PurgarPaquetesAnterioresInteractor;
import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.mapper.PurgarPaquetesAnterioresMapper;
import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.model.PurgarPaquetesAnterioresCommand;
import com.arquisoft.artefactos.application.paqueteentregable.command.result.PurgaPaquetesAnterioresResult;
import com.arquisoft.artefactos.application.paqueteentregable.command.usecase.PurgarPaquetesAnterioresUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PurgarPaquetesAnterioresInteractorImpl implements PurgarPaquetesAnterioresInteractor {

    private final PurgarPaquetesAnterioresUseCase purgarPaquetesAnterioresUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public PurgaPaquetesAnterioresResult ejecutar(PurgarPaquetesAnterioresCommand command) {
        return purgarPaquetesAnterioresUseCase.ejecutar(PurgarPaquetesAnterioresMapper.toDomain(command));
    }
}
