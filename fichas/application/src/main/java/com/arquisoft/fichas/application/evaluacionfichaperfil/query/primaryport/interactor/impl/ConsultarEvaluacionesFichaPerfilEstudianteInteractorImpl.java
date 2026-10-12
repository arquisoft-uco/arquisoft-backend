package com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.interactor.ConsultarEvaluacionesFichaPerfilEstudianteInteractor;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.mapper.ConsultarEvaluacionesFichaPerfilEstudianteMapper;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilEstudianteQuery;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase.ConsultarEvaluacionesFichaPerfilEstudianteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesFichaPerfilEstudianteInteractorImpl
        implements ConsultarEvaluacionesFichaPerfilEstudianteInteractor {

    private final ConsultarEvaluacionesFichaPerfilEstudianteUseCase consultarEvaluacionesFichaPerfilEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public List<EvaluacionFichaPerfilEstudianteReadModel> ejecutar(ConsultarEvaluacionesFichaPerfilEstudianteQuery entrada) {
        var criteria = ConsultarEvaluacionesFichaPerfilEstudianteMapper.toCriteria(entrada);
        return consultarEvaluacionesFichaPerfilEstudianteUseCase.ejecutar(criteria);
    }
}
