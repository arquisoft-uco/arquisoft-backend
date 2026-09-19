package com.arquisoft.proyectos.application.coordinador.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.coordinador.command.primaryport.interactor.AgregarCoordinadorInteractor;
import com.arquisoft.proyectos.application.coordinador.command.primaryport.mapper.AgregarCoordinadorMapper;
import com.arquisoft.proyectos.application.coordinador.command.primaryport.model.AgregarCoordinadorCommand;
import com.arquisoft.proyectos.application.coordinador.command.result.AgregacionCoordinadorResult;
import com.arquisoft.proyectos.application.coordinador.command.usecase.AgregarCoordinadorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarCoordinadorInteractorImpl implements AgregarCoordinadorInteractor {

    private final AgregarCoordinadorUseCase agregarCoordinadorUseCase;

    @Override
    @Transactional(transactionManager = "proyectosTransactionManager")
    public AgregacionCoordinadorResult ejecutar(AgregarCoordinadorCommand command) {
        return agregarCoordinadorUseCase.ejecutar(AgregarCoordinadorMapper.toDomain(command));
    }
}
