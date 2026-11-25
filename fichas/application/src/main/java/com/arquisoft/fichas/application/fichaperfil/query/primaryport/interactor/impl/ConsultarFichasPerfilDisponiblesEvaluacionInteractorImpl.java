package com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.ConsultarFichasPerfilDisponiblesEvaluacionInteractor;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.mapper.ConsultarFichasPerfilDisponiblesEvaluacionMapper;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilDisponiblesEvaluacionQuery;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.usecase.ConsultarFichasPerfilDisponiblesEvaluacionUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarFichasPerfilDisponiblesEvaluacionInteractorImpl
        implements ConsultarFichasPerfilDisponiblesEvaluacionInteractor {

    private final ConsultarFichasPerfilDisponiblesEvaluacionUseCase consultarFichasPerfilDisponiblesEvaluacionUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public PaginatedResult<FichaPerfilReadModel> ejecutar(ConsultarFichasPerfilDisponiblesEvaluacionQuery entrada) {
        var criteria = ConsultarFichasPerfilDisponiblesEvaluacionMapper.toCriteria(entrada);
        return consultarFichasPerfilDisponiblesEvaluacionUseCase.ejecutar(criteria);
    }
}
