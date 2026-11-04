package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.interactor.ConsultarEvaluacionesFichaPerfilCoordinadorInteractor;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.mapper.ConsultarEvaluacionesFichaPerfilCoordinadorMapper;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilCoordinadorQuery;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilCoordinadorReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase.ConsultarEvaluacionesFichaPerfilCoordinadorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesFichaPerfilCoordinadorInteractorImpl
        implements ConsultarEvaluacionesFichaPerfilCoordinadorInteractor {

    private final ConsultarEvaluacionesFichaPerfilCoordinadorUseCase consultarEvaluacionesFichaPerfilCoordinadorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public List<EvaluacionFichaPerfilCoordinadorReadModel> ejecutar(ConsultarEvaluacionesFichaPerfilCoordinadorQuery entrada) {
        var criteria = ConsultarEvaluacionesFichaPerfilCoordinadorMapper.toCriteria(entrada);
        return consultarEvaluacionesFichaPerfilCoordinadorUseCase.ejecutar(criteria);
    }
}
