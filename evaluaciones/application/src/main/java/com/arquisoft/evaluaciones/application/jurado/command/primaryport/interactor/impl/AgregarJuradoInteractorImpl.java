package com.arquisoft.evaluaciones.application.jurado.command.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.jurado.command.primaryport.interactor.AgregarJuradoInteractor;
import com.arquisoft.evaluaciones.application.jurado.command.primaryport.mapper.AgregarJuradoMapper;
import com.arquisoft.evaluaciones.application.jurado.command.primaryport.model.AgregarJuradoCommand;
import com.arquisoft.evaluaciones.application.jurado.command.result.AgregacionJuradoResult;
import com.arquisoft.evaluaciones.application.jurado.command.usecase.AgregarJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarJuradoInteractorImpl implements AgregarJuradoInteractor {

    private final AgregarJuradoUseCase agregarJuradoUseCase;

    @Override
    @Transactional(transactionManager = "evaluacionesTransactionManager")
    public AgregacionJuradoResult ejecutar(AgregarJuradoCommand command) {
        return agregarJuradoUseCase.ejecutar(AgregarJuradoMapper.toDomain(command));
    }
}
