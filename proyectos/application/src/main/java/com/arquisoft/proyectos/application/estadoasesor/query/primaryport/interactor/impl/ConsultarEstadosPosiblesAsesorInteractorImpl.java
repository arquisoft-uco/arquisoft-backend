package com.arquisoft.proyectos.application.estadoasesor.query.primaryport.interactor.impl;

import com.arquisoft.proyectos.application.estadoasesor.query.primaryport.interactor.ConsultarEstadosPosiblesAsesorInteractor;
import com.arquisoft.proyectos.application.estadoasesor.query.primaryport.mapper.ConsultarEstadosPosiblesAsesorMapper;
import com.arquisoft.proyectos.application.estadoasesor.query.readmodel.EstadoAsesorReadModel;
import com.arquisoft.proyectos.application.estadoasesor.query.usecase.ConsultarEstadosPosiblesAsesorUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosPosiblesAsesorInteractorImpl implements ConsultarEstadosPosiblesAsesorInteractor {

    private final ConsultarEstadosPosiblesAsesorUseCase consultarEstadosPosiblesAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "proyectosTransactionManager")
    public PaginatedResult<EstadoAsesorReadModel> ejecutar(ConsultaCriteriaQuery entrada) {
        var criteria = ConsultarEstadosPosiblesAsesorMapper.toCriteria(entrada);
        return consultarEstadosPosiblesAsesorUseCase.ejecutar(criteria);
    }
}
