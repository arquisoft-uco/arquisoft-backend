package com.arquisoft.entregables.application.paqueteentregable.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.interactor.AgregarPaqueteEntregableInteractor;
import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.mapper.AgregarPaqueteEntregableMapper;
import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.model.AgregarPaqueteEntregableCommand;
import com.arquisoft.entregables.application.paqueteentregable.command.result.AgregacionPaqueteEntregableResult;
import com.arquisoft.entregables.application.paqueteentregable.command.usecase.AgregarPaqueteEntregableUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarPaqueteEntregableInteractorImpl implements AgregarPaqueteEntregableInteractor {

    private final AgregarPaqueteEntregableUseCase agregarPaqueteEntregableUseCase;

    @Override
    @Transactional(transactionManager = "entregablesTransactionManager")
    public AgregacionPaqueteEntregableResult ejecutar(AgregarPaqueteEntregableCommand command) {
        return agregarPaqueteEntregableUseCase.ejecutar(AgregarPaqueteEntregableMapper.toDomain(command));
    }
}
