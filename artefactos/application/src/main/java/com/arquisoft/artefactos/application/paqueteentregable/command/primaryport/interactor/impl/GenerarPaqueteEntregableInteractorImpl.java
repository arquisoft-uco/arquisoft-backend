package com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.interactor.GenerarPaqueteEntregableInteractor;
import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.mapper.GenerarPaqueteEntregableMapper;
import com.arquisoft.artefactos.application.paqueteentregable.command.primaryport.model.GenerarPaqueteEntregableCommand;
import com.arquisoft.artefactos.application.paqueteentregable.command.result.GeneracionPaqueteEntregableResult;
import com.arquisoft.artefactos.application.paqueteentregable.command.usecase.GenerarPaqueteEntregableUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GenerarPaqueteEntregableInteractorImpl implements GenerarPaqueteEntregableInteractor {

    private final GenerarPaqueteEntregableUseCase generarPaqueteEntregableUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public GeneracionPaqueteEntregableResult ejecutar(GenerarPaqueteEntregableCommand command) {
        return generarPaqueteEntregableUseCase.ejecutar(GenerarPaqueteEntregableMapper.toDomain(command));
    }
}
