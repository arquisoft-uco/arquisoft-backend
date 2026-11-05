package com.arquisoft.mapas_ruta.application.planreal.query.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.planreal.query.primaryport.interactor.ConsultarPlanesRealesTareaEstudianteInteractor;
import com.arquisoft.mapas_ruta.application.planreal.query.primaryport.mapper.ConsultarPlanesRealesTareaEstudianteMapper;
import com.arquisoft.mapas_ruta.application.planreal.query.primaryport.model.ConsultarPlanesRealesTareaEstudianteQuery;
import com.arquisoft.mapas_ruta.application.planreal.query.readmodel.PlanRealReadModel;
import com.arquisoft.mapas_ruta.application.planreal.query.usecase.ConsultarPlanesRealesTareaEstudianteUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarPlanesRealesTareaEstudianteInteractorImpl
        implements ConsultarPlanesRealesTareaEstudianteInteractor {

    private final ConsultarPlanesRealesTareaEstudianteUseCase consultarPlanesRealesTareaEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "mapasRutaTransactionManager")
    public PaginatedResult<PlanRealReadModel> ejecutar(ConsultarPlanesRealesTareaEstudianteQuery entrada) {
        var criteria = ConsultarPlanesRealesTareaEstudianteMapper.toCriteria(entrada);
        return consultarPlanesRealesTareaEstudianteUseCase.ejecutar(criteria);
    }
}
