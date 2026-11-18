package com.arquisoft.mapas_ruta.application.planreal.query.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planreal.query.primaryport.interactor.ConsultarPlanesRealesProyectoGradoAsesorInteractor;
import com.arquisoft.mapas_ruta.application.planreal.query.primaryport.mapper.ConsultarPlanesRealesProyectoGradoAsesorMapper;
import com.arquisoft.mapas_ruta.application.planreal.query.primaryport.model.ConsultarPlanesRealesProyectoGradoAsesorQuery;
import com.arquisoft.mapas_ruta.application.planreal.query.readmodel.PlanRealProyectoReadModel;
import com.arquisoft.mapas_ruta.application.planreal.query.usecase.ConsultarPlanesRealesProyectoGradoAsesorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarPlanesRealesProyectoGradoAsesorInteractorImpl
        implements ConsultarPlanesRealesProyectoGradoAsesorInteractor {

    private final ConsultarPlanesRealesProyectoGradoAsesorUseCase consultarPlanesRealesProyectoGradoAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "mapasRutaTransactionManager")
    public PaginatedResult<PlanRealProyectoReadModel> ejecutar(ConsultarPlanesRealesProyectoGradoAsesorQuery entrada) {
        var criteria = ConsultarPlanesRealesProyectoGradoAsesorMapper.toCriteria(entrada);
        return consultarPlanesRealesProyectoGradoAsesorUseCase.ejecutar(criteria);
    }
}
