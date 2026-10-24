package com.arquisoft.mapas_ruta.application.estadoitem.query.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.estadoitem.query.primaryport.interactor.ConsultarEstadosItemInteractor;
import com.arquisoft.mapas_ruta.application.estadoitem.query.readmodel.EstadoItemReadModel;
import com.arquisoft.mapas_ruta.application.estadoitem.query.usecase.ConsultarEstadosItemUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosItemInteractorImpl implements ConsultarEstadosItemInteractor {

    private final ConsultarEstadosItemUseCase consultarEstadosItemUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "mapasRutaTransactionManager")
    public List<EstadoItemReadModel> ejecutar() {
        return consultarEstadosItemUseCase.ejecutar();
    }
}
