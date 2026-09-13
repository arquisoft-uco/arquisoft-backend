package com.arquisoft.proyectos.application.estudiante.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estudiante.command.primaryport.interactor.AgregarEstudianteProyectosInteractor;
import com.arquisoft.proyectos.application.estudiante.command.primaryport.mapper.AgregarEstudianteMapper;
import com.arquisoft.proyectos.application.estudiante.command.primaryport.model.AgregarEstudianteCommand;
import com.arquisoft.proyectos.application.estudiante.command.result.AgregacionEstudianteResult;
import com.arquisoft.proyectos.application.estudiante.command.usecase.AgregarEstudianteProyectosUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarEstudianteProyectosInteractorImpl implements AgregarEstudianteProyectosInteractor {

    private final AgregarEstudianteProyectosUseCase agregarEstudianteProyectosUseCase;

    @Override
    @Transactional(transactionManager = "proyectosTransactionManager")
    public AgregacionEstudianteResult ejecutar(AgregarEstudianteCommand command) {
        return agregarEstudianteProyectosUseCase.ejecutar(AgregarEstudianteMapper.toDomain(command));
    }
}
