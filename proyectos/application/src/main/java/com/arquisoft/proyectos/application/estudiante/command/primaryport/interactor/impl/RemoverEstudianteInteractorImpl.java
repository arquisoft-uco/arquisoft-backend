package com.arquisoft.proyectos.application.estudiante.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estudiante.command.primaryport.interactor.RemoverEstudianteInteractor;
import com.arquisoft.proyectos.application.estudiante.command.primaryport.mapper.RemoverEstudianteMapper;
import com.arquisoft.proyectos.application.estudiante.command.primaryport.model.RemoverEstudianteCommand;
import com.arquisoft.proyectos.application.estudiante.command.result.RemocionEstudianteResult;
import com.arquisoft.proyectos.application.estudiante.command.usecase.RemoverEstudianteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoverEstudianteInteractorImpl implements RemoverEstudianteInteractor {

    private final RemoverEstudianteUseCase removerEstudianteUseCase;

    @Override
    @Transactional(transactionManager = "proyectosTransactionManager")
    public RemocionEstudianteResult ejecutar(RemoverEstudianteCommand command) {
        return removerEstudianteUseCase.ejecutar(RemoverEstudianteMapper.toDomain(command));
    }
}
