package com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.interactor.RegistrarObservacionItemJuradoInteractor;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.mapper.RegistrarObservacionItemJuradoMapper;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model.RegistrarObservacionItemJuradoCommand;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.usecase.RegistrarObservacionItemJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarObservacionItemJuradoInteractorImpl implements RegistrarObservacionItemJuradoInteractor {

    private final RegistrarObservacionItemJuradoUseCase registrarObservacionItemJuradoUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public UUID ejecutar(RegistrarObservacionItemJuradoCommand command) {
        return registrarObservacionItemJuradoUseCase.ejecutar(RegistrarObservacionItemJuradoMapper.toDomain(command));
    }
}
