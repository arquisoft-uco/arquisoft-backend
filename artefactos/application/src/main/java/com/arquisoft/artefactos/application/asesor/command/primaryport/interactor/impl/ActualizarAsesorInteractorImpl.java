package com.arquisoft.artefactos.application.asesor.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.asesor.command.primaryport.interactor.ActualizarAsesorInteractor;
import com.arquisoft.artefactos.application.asesor.command.primaryport.mapper.ActualizarAsesorMapper;
import com.arquisoft.artefactos.application.asesor.command.primaryport.model.ActualizarAsesorCommand;
import com.arquisoft.artefactos.application.asesor.command.result.ActualizacionAsesorResult;
import com.arquisoft.artefactos.application.asesor.command.usecase.ActualizarAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ActualizarAsesorInteractorImpl implements ActualizarAsesorInteractor {

    private final ActualizarAsesorUseCase actualizarAsesorUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public ActualizacionAsesorResult ejecutar(ActualizarAsesorCommand command) {
        return actualizarAsesorUseCase.ejecutar(ActualizarAsesorMapper.toDomain(command));
    }
}
