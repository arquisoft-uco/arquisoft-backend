package com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.interactor.ModificarObservacionItemAsesorInteractor;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.mapper.ModificarObservacionItemAsesorMapper;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.model.ModificarObservacionItemAsesorCommand;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.usecase.ModificarObservacionItemAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModificarObservacionItemAsesorInteractorImpl implements ModificarObservacionItemAsesorInteractor {

    private final ModificarObservacionItemAsesorUseCase modificarObservacionItemAsesorUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(ModificarObservacionItemAsesorCommand command) {
        modificarObservacionItemAsesorUseCase.ejecutar(ModificarObservacionItemAsesorMapper.toDomain(command));
    }
}
