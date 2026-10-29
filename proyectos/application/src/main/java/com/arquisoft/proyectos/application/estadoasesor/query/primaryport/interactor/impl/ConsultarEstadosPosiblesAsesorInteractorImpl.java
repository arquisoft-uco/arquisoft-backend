package com.arquisoft.proyectos.application.estadoasesor.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estadoasesor.query.primaryport.interactor.ConsultarEstadosPosiblesAsesorInteractor;
import com.arquisoft.proyectos.application.estadoasesor.query.readmodel.EstadoAsesorReadModel;
import com.arquisoft.proyectos.application.estadoasesor.query.usecase.ConsultarEstadosPosiblesAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosPosiblesAsesorInteractorImpl implements ConsultarEstadosPosiblesAsesorInteractor {

    private final ConsultarEstadosPosiblesAsesorUseCase consultarEstadosPosiblesAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "proyectosTransactionManager")
    public List<EstadoAsesorReadModel> ejecutar() {
        return consultarEstadosPosiblesAsesorUseCase.ejecutar();
    }
}
