package com.arquisoft.fichas.application.estadoevaluacionficha.command.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.primaryport.interactor.DescartarEvaluacionFichaInteractor;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.primaryport.mapper.DescartarEvaluacionFichaMapper;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.primaryport.model.DescartarEvaluacionFichaCommand;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.DescartarEvaluacionFichaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DescartarEvaluacionFichaInteractorImpl implements DescartarEvaluacionFichaInteractor {

    private final DescartarEvaluacionFichaUseCase descartarEvaluacionFichaUseCase;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public void ejecutar(DescartarEvaluacionFichaCommand command) {
        descartarEvaluacionFichaUseCase.ejecutar(DescartarEvaluacionFichaMapper.toDomain(command));
    }
}
