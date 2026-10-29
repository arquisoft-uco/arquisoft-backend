package com.arquisoft.proyectos.application.estadoasesor.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estadoasesor.query.primaryport.interactor.ConsultarEstadosAsesorInteractor;
import com.arquisoft.proyectos.application.estadoasesor.query.readmodel.EstadoAsesorReadModel;
import com.arquisoft.proyectos.application.estadoasesor.query.usecase.ConsultarEstadosAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosAsesorInteractorImpl implements ConsultarEstadosAsesorInteractor {

    private final ConsultarEstadosAsesorUseCase consultarEstadosAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "proyectosTransactionManager")
    public List<EstadoAsesorReadModel> ejecutar() {
        return consultarEstadosAsesorUseCase.ejecutar();
    }
}
