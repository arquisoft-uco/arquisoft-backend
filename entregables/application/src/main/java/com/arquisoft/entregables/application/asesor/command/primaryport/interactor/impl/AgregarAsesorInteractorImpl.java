package com.arquisoft.entregables.application.asesor.command.primaryport.interactor.impl;

import com.arquisoft.entregables.application.asesor.command.primaryport.interactor.AgregarAsesorInteractor;
import com.arquisoft.entregables.application.asesor.command.primaryport.mapper.AgregarAsesorMapper;
import com.arquisoft.entregables.application.asesor.command.primaryport.model.AgregarAsesorCommand;
import com.arquisoft.entregables.application.asesor.command.result.AgregacionAsesorResult;
import com.arquisoft.entregables.application.asesor.command.usecase.AgregarAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarAsesorInteractorImpl implements AgregarAsesorInteractor {

    private final AgregarAsesorUseCase agregarAsesorUseCase;

    @Override
    @Transactional(transactionManager = "entregablesTransactionManager")
    public AgregacionAsesorResult ejecutar(AgregarAsesorCommand command) {
        return agregarAsesorUseCase.ejecutar(AgregarAsesorMapper.toDomain(command));
    }
}
