package com.arquisoft.evaluaciones.application.entregable.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.entregable.command.primaryport.interactor.AgregarEntregableInteractor;
import com.arquisoft.evaluaciones.application.entregable.command.primaryport.mapper.AgregarEntregableMapper;
import com.arquisoft.evaluaciones.application.entregable.command.primaryport.model.AgregarEntregableCommand;
import com.arquisoft.evaluaciones.application.entregable.command.result.AgregacionEntregableResult;
import com.arquisoft.evaluaciones.application.entregable.command.usecase.AgregarEntregableUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarEntregableInteractorImpl implements AgregarEntregableInteractor {

    private final AgregarEntregableUseCase agregarEntregableUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public AgregacionEntregableResult ejecutar(AgregarEntregableCommand command) {
        return agregarEntregableUseCase.ejecutar(AgregarEntregableMapper.toDomain(command));
    }
}
