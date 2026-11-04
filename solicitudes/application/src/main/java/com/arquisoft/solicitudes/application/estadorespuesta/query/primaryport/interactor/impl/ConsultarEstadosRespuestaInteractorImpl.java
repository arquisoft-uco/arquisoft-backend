package com.arquisoft.solicitudes.application.estadorespuesta.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.estadorespuesta.query.primaryport.interactor.ConsultarEstadosRespuestaInteractor;
import com.arquisoft.solicitudes.application.estadorespuesta.query.readmodel.EstadoRespuestaReadModel;
import com.arquisoft.solicitudes.application.estadorespuesta.query.usecase.ConsultarEstadosRespuestaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosRespuestaInteractorImpl implements ConsultarEstadosRespuestaInteractor {

    private final ConsultarEstadosRespuestaUseCase consultarEstadosRespuestaUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public List<EstadoRespuestaReadModel> ejecutar() {
        return consultarEstadosRespuestaUseCase.ejecutar();
    }
}
