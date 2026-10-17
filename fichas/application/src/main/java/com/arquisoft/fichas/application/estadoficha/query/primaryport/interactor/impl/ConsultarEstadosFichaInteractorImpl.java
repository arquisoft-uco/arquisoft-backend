package com.arquisoft.fichas.application.estadoficha.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadoficha.query.primaryport.interactor.ConsultarEstadosFichaInteractor;
import com.arquisoft.fichas.application.estadoficha.query.primaryport.mapper.ConsultarEstadosFichaMapper;
import com.arquisoft.fichas.application.estadoficha.query.primaryport.model.ConsultarEstadosFichaQuery;
import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import com.arquisoft.fichas.application.estadoficha.query.usecase.ConsultarEstadosFichaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosFichaInteractorImpl implements ConsultarEstadosFichaInteractor {

    private final ConsultarEstadosFichaUseCase consultarEstadosFichaUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public List<EstadoFichaReadModel> ejecutar(ConsultarEstadosFichaQuery entrada) {
        return consultarEstadosFichaUseCase.ejecutar(ConsultarEstadosFichaMapper.toCriteria(entrada));
    }
}
