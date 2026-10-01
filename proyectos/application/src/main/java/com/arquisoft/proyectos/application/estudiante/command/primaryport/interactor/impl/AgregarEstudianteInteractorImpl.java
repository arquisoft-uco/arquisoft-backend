package com.arquisoft.proyectos.application.estudiante.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estudiante.command.primaryport.interactor.AgregarEstudianteInteractor;
import com.arquisoft.proyectos.application.estudiante.command.primaryport.mapper.AgregarEstudianteMapper;
import com.arquisoft.proyectos.application.estudiante.command.primaryport.model.AgregarEstudianteCommand;
import com.arquisoft.proyectos.application.estudiante.command.result.AgregacionEstudianteResult;
import com.arquisoft.proyectos.application.estudiante.command.usecase.AgregarEstudianteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarEstudianteInteractorImpl implements AgregarEstudianteInteractor {

    private final AgregarEstudianteUseCase agregarEstudianteUseCase;

    @Override
    @Transactional(transactionManager = "proyectosTransactionManager")
    public AgregacionEstudianteResult ejecutar(AgregarEstudianteCommand command) {
        return agregarEstudianteUseCase.ejecutar(AgregarEstudianteMapper.toDomain(command));
    }
}
