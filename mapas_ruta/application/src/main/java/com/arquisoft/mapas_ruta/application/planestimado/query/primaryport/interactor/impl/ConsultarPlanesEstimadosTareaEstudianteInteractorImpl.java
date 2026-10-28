package com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.interactor.ConsultarPlanesEstimadosTareaEstudianteInteractor;
import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.mapper.ConsultarPlanesEstimadosTareaEstudianteMapper;
import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.model.ConsultarPlanesEstimadosTareaEstudianteQuery;
import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoReadModel;
import com.arquisoft.mapas_ruta.application.planestimado.query.usecase.ConsultarPlanesEstimadosTareaEstudianteUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarPlanesEstimadosTareaEstudianteInteractorImpl
        implements ConsultarPlanesEstimadosTareaEstudianteInteractor {

    private final ConsultarPlanesEstimadosTareaEstudianteUseCase consultarPlanesEstimadosTareaEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "mapasRutaTransactionManager")
    public PaginatedResult<PlanEstimadoReadModel> ejecutar(ConsultarPlanesEstimadosTareaEstudianteQuery entrada) {
        var criteria = ConsultarPlanesEstimadosTareaEstudianteMapper.toCriteria(entrada);
        return consultarPlanesEstimadosTareaEstudianteUseCase.ejecutar(criteria);
    }
}
