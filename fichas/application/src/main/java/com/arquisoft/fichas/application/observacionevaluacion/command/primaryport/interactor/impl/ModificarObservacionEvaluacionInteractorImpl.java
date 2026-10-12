package com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.interactor.ModificarObservacionEvaluacionInteractor;
import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.mapper.ModificarObservacionEvaluacionMapper;
import com.arquisoft.fichas.application.observacionevaluacion.command.primaryport.model.ModificarObservacionEvaluacionCommand;
import com.arquisoft.fichas.application.observacionevaluacion.command.usecase.ModificarObservacionEvaluacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModificarObservacionEvaluacionInteractorImpl implements ModificarObservacionEvaluacionInteractor {

    private final ModificarObservacionEvaluacionUseCase modificarObservacionEvaluacionUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(ModificarObservacionEvaluacionCommand command) {
        modificarObservacionEvaluacionUseCase.ejecutar(ModificarObservacionEvaluacionMapper.toDomain(command));
    }
}
