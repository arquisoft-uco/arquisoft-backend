package com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.interactor.RemoverObservacionItemAsesorInteractor;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.mapper.RemoverObservacionItemAsesorMapper;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.primaryport.model.RemoverObservacionItemAsesorCommand;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.usecase.RemoverObservacionItemAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverObservacionItemAsesorInteractorImpl implements RemoverObservacionItemAsesorInteractor {

    private final RemoverObservacionItemAsesorUseCase removerObservacionItemAsesorUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public void ejecutar(RemoverObservacionItemAsesorCommand command) {
        removerObservacionItemAsesorUseCase.ejecutar(RemoverObservacionItemAsesorMapper.toDomain(command));
    }
}
