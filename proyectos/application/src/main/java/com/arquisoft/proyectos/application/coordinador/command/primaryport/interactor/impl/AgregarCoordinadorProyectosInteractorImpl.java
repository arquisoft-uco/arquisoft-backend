package com.arquisoft.proyectos.application.coordinador.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.coordinador.command.primaryport.interactor.AgregarCoordinadorProyectosInteractor;
import com.arquisoft.proyectos.application.coordinador.command.primaryport.mapper.AgregarCoordinadorMapper;
import com.arquisoft.proyectos.application.coordinador.command.primaryport.model.AgregarCoordinadorCommand;
import com.arquisoft.proyectos.application.coordinador.command.result.AgregacionCoordinadorResult;
import com.arquisoft.proyectos.application.coordinador.command.usecase.AgregarCoordinadorProyectosUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarCoordinadorProyectosInteractorImpl implements AgregarCoordinadorProyectosInteractor {

    private final AgregarCoordinadorProyectosUseCase agregarCoordinadorProyectosUseCase;

    @Override
    @Transactional(transactionManager = "proyectosTransactionManager")
    public AgregacionCoordinadorResult ejecutar(AgregarCoordinadorCommand command) {
        return agregarCoordinadorProyectosUseCase.ejecutar(AgregarCoordinadorMapper.toDomain(command));
    }
}
