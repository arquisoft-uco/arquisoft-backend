package com.arquisoft.fichas.application.estudiante.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estudiante.command.primaryport.interactor.ActualizarEstudianteInteractor;
import com.arquisoft.fichas.application.estudiante.command.primaryport.mapper.ActualizarEstudianteMapper;
import com.arquisoft.fichas.application.estudiante.command.primaryport.model.ActualizarEstudianteCommand;
import com.arquisoft.fichas.application.estudiante.command.result.ActualizacionEstudianteResult;
import com.arquisoft.fichas.application.estudiante.command.usecase.ActualizarEstudianteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ActualizarEstudianteInteractorImpl implements ActualizarEstudianteInteractor {

    private final ActualizarEstudianteUseCase actualizarEstudianteUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public ActualizacionEstudianteResult ejecutar(ActualizarEstudianteCommand command) {
        return actualizarEstudianteUseCase.ejecutar(ActualizarEstudianteMapper.toDomain(command));
    }
}
