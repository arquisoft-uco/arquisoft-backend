package com.arquisoft.proyectos.application.coordinador.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.coordinador.command.primaryport.interactor.ActualizarCoordinadorInteractor;
import com.arquisoft.proyectos.application.coordinador.command.primaryport.mapper.ActualizarCoordinadorMapper;
import com.arquisoft.proyectos.application.coordinador.command.primaryport.model.ActualizarCoordinadorCommand;
import com.arquisoft.proyectos.application.coordinador.command.result.ActualizacionCoordinadorResult;
import com.arquisoft.proyectos.application.coordinador.command.usecase.ActualizarCoordinadorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ActualizarCoordinadorInteractorImpl implements ActualizarCoordinadorInteractor {

    private final ActualizarCoordinadorUseCase actualizarCoordinadorUseCase;

    @Override
    @Transactional(transactionManager = "proyectosTransactionManager")
    public ActualizacionCoordinadorResult ejecutar(ActualizarCoordinadorCommand command) {
        return actualizarCoordinadorUseCase.ejecutar(ActualizarCoordinadorMapper.toDomain(command));
    }
}
