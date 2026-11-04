package com.arquisoft.artefactos.application.observacion.command.primaryport.interactor.impl;

import com.arquisoft.artefactos.application.observacion.command.primaryport.interactor.RegistrarObservacionInteractor;
import com.arquisoft.artefactos.application.observacion.command.primaryport.mapper.RegistrarObservacionMapper;
import com.arquisoft.artefactos.application.observacion.command.primaryport.model.RegistrarObservacionCommand;
import com.arquisoft.artefactos.application.observacion.command.usecase.RegistrarObservacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarObservacionInteractorImpl implements RegistrarObservacionInteractor {

    private final RegistrarObservacionUseCase registrarObservacionUseCase;

    @Override
    @Transactional(transactionManager = "artefactosTransactionManager")
    public UUID ejecutar(RegistrarObservacionCommand command) {
        return registrarObservacionUseCase.ejecutar(RegistrarObservacionMapper.toDomain(command));
    }
}
