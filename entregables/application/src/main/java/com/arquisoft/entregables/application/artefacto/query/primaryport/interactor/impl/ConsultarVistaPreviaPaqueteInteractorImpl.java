package com.arquisoft.entregables.application.artefacto.query.primaryport.interactor.impl;

import com.arquisoft.entregables.application.artefacto.query.primaryport.interactor.ConsultarVistaPreviaPaqueteInteractor;
import com.arquisoft.entregables.application.artefacto.query.primaryport.mapper.ConsultarVistaPreviaPaqueteMapper;
import com.arquisoft.entregables.application.artefacto.query.primaryport.model.ConsultarVistaPreviaPaqueteQuery;
import com.arquisoft.entregables.application.artefacto.query.readmodel.ArtefactoVistaPreviaReadModel;
import com.arquisoft.entregables.application.artefacto.query.usecase.ConsultarVistaPreviaPaqueteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarVistaPreviaPaqueteInteractorImpl implements ConsultarVistaPreviaPaqueteInteractor {

    private final ConsultarVistaPreviaPaqueteUseCase consultarVistaPreviaPaqueteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "entregablesTransactionManager")
    public List<ArtefactoVistaPreviaReadModel> ejecutar(ConsultarVistaPreviaPaqueteQuery query) {
        return consultarVistaPreviaPaqueteUseCase.ejecutar(ConsultarVistaPreviaPaqueteMapper.toCriteria(query));
    }
}
