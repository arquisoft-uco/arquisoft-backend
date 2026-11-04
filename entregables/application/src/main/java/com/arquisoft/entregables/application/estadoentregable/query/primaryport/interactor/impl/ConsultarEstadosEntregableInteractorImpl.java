package com.arquisoft.entregables.application.estadoentregable.query.primaryport.interactor.impl;

import com.arquisoft.entregables.application.estadoentregable.query.primaryport.interactor.ConsultarEstadosEntregableInteractor;
import com.arquisoft.entregables.application.estadoentregable.query.readmodel.EstadoEntregableReadModel;
import com.arquisoft.entregables.application.estadoentregable.query.usecase.ConsultarEstadosEntregableUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosEntregableInteractorImpl implements ConsultarEstadosEntregableInteractor {

    private final ConsultarEstadosEntregableUseCase consultarEstadosEntregableUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "entregablesTransactionManager")
    public List<EstadoEntregableReadModel> ejecutar() {
        return consultarEstadosEntregableUseCase.ejecutar();
    }
}
