package com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.interactor.ConsultarPlanesEstimadosProyectoGradoAsesorInteractor;
import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.mapper.ConsultarPlanesEstimadosProyectoGradoAsesorMapper;
import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.model.ConsultarPlanesEstimadosProyectoGradoAsesorQuery;
import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoProyectoReadModel;
import com.arquisoft.mapas_ruta.application.planestimado.query.usecase.ConsultarPlanesEstimadosProyectoGradoAsesorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarPlanesEstimadosProyectoGradoAsesorInteractorImpl
        implements ConsultarPlanesEstimadosProyectoGradoAsesorInteractor {

    private final ConsultarPlanesEstimadosProyectoGradoAsesorUseCase consultarPlanesEstimadosProyectoGradoAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "mapasRutaTransactionManager")
    public PaginatedResult<PlanEstimadoProyectoReadModel> ejecutar(ConsultarPlanesEstimadosProyectoGradoAsesorQuery entrada) {
        var criteria = ConsultarPlanesEstimadosProyectoGradoAsesorMapper.toCriteria(entrada);
        return consultarPlanesEstimadosProyectoGradoAsesorUseCase.ejecutar(criteria);
    }
}
