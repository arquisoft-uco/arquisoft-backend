package com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.interactor.RegistrarObservacionItemAsesorInteractor;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.mapper.RegistrarObservacionItemAsesorMapper;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.model.RegistrarObservacionItemAsesorCommand;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.usecase.RegistrarObservacionItemAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarObservacionItemAsesorInteractorImpl implements RegistrarObservacionItemAsesorInteractor {

    private final RegistrarObservacionItemAsesorUseCase registrarObservacionItemAsesorUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public UUID ejecutar(RegistrarObservacionItemAsesorCommand command) {
        return registrarObservacionItemAsesorUseCase.ejecutar(RegistrarObservacionItemAsesorMapper.toDomain(command));
    }
}
