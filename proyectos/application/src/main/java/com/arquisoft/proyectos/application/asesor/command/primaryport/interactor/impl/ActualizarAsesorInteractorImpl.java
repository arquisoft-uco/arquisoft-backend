package com.arquisoft.proyectos.application.asesor.command.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.asesor.command.primaryport.interactor.ActualizarAsesorInteractor;
import com.arquisoft.proyectos.application.asesor.command.primaryport.mapper.ActualizarAsesorMapper;
import com.arquisoft.proyectos.application.asesor.command.primaryport.model.ActualizarAsesorCommand;
import com.arquisoft.proyectos.application.asesor.command.result.ActualizacionAsesorResult;
import com.arquisoft.proyectos.application.asesor.command.usecase.ActualizarAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ActualizarAsesorInteractorImpl implements ActualizarAsesorInteractor {

    private final ActualizarAsesorUseCase actualizarAsesorUseCase;

    @Override
    @Transactional(transactionManager = "proyectosTransactionManager")
    public ActualizacionAsesorResult ejecutar(ActualizarAsesorCommand command) {
        return actualizarAsesorUseCase.ejecutar(ActualizarAsesorMapper.toDomain(command));
    }
}
