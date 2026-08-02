package com.arquisoft.fichas.application.estadoevaluacionficha.command.interactor.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.model.AgregarEstadoEvaluacionFichaCommand;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.interactor.AgregarEstadoEvaluacionFichaInteractor;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.AgregarEstadoEvaluacionFichaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarEstadoEvaluacionFichaInteractorImpl implements AgregarEstadoEvaluacionFichaInteractor {

    private final AgregarEstadoEvaluacionFichaUseCase agregarEstadoEvaluacionFichaUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(AgregarEstadoEvaluacionFichaCommand command) {
        return agregarEstadoEvaluacionFichaUseCase.ejecutar(command);
    }
}
