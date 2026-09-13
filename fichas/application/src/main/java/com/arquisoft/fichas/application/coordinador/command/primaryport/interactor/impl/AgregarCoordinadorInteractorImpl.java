package com.arquisoft.fichas.application.coordinador.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.coordinador.command.primaryport.interactor.AgregarCoordinadorInteractor;
import com.arquisoft.fichas.application.coordinador.command.primaryport.mapper.AgregarCoordinadorMapper;
import com.arquisoft.fichas.application.coordinador.command.primaryport.model.AgregarCoordinadorCommand;
import com.arquisoft.fichas.application.coordinador.command.result.AgregacionCoordinadorResult;
import com.arquisoft.fichas.application.coordinador.command.usecase.AgregarCoordinadorFichasUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AgregarCoordinadorInteractorImpl implements AgregarCoordinadorInteractor {

    private final AgregarCoordinadorFichasUseCase agregarCoordinadorFichasUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public AgregacionCoordinadorResult ejecutar(AgregarCoordinadorCommand command) {
        return agregarCoordinadorFichasUseCase.ejecutar(AgregarCoordinadorMapper.toDomain(command));
    }
}
