package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.interactor.ConsultarEvaluacionesFichaPerfilAsesorInteractor;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.mapper.ConsultarEvaluacionesFichaPerfilAsesorMapper;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilAsesorQuery;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilAsesorReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase.ConsultarEvaluacionesFichaPerfilAsesorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesFichaPerfilAsesorInteractorImpl implements ConsultarEvaluacionesFichaPerfilAsesorInteractor {

    private final ConsultarEvaluacionesFichaPerfilAsesorUseCase consultarEvaluacionesFichaPerfilAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public PaginatedResult<EvaluacionFichaPerfilAsesorReadModel> ejecutar(ConsultarEvaluacionesFichaPerfilAsesorQuery entrada) {
        var criteria = ConsultarEvaluacionesFichaPerfilAsesorMapper.toCriteria(entrada);
        return consultarEvaluacionesFichaPerfilAsesorUseCase.ejecutar(criteria);
    }
}
