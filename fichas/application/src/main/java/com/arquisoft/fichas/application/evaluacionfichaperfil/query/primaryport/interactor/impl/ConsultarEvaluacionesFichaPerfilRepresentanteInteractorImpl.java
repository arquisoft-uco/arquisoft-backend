package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.interactor.ConsultarEvaluacionesFichaPerfilRepresentanteInteractor;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.mapper.ConsultarEvaluacionesFichaPerfilRepresentanteMapper;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilRepresentanteQuery;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase.ConsultarEvaluacionesFichaPerfilRepresentanteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesFichaPerfilRepresentanteInteractorImpl
        implements ConsultarEvaluacionesFichaPerfilRepresentanteInteractor {

    private final ConsultarEvaluacionesFichaPerfilRepresentanteUseCase consultarEvaluacionesFichaPerfilRepresentanteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public List<EvaluacionFichaPerfilReadModel> ejecutar(ConsultarEvaluacionesFichaPerfilRepresentanteQuery entrada) {
        var criteria = ConsultarEvaluacionesFichaPerfilRepresentanteMapper.toCriteria(entrada);
        return consultarEvaluacionesFichaPerfilRepresentanteUseCase.ejecutar(criteria);
    }
}
