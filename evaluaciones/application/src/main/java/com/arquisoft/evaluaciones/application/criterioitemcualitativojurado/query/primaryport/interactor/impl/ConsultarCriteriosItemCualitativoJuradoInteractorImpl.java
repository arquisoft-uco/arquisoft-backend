package com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.primaryport.interactor.ConsultarCriteriosItemCualitativoJuradoInteractor;
import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.readmodel.CriterioItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.usecase.ConsultarCriteriosItemCualitativoJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarCriteriosItemCualitativoJuradoInteractorImpl
        implements ConsultarCriteriosItemCualitativoJuradoInteractor {

    private final ConsultarCriteriosItemCualitativoJuradoUseCase consultarCriteriosItemCualitativoJuradoUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public List<CriterioItemCualitativoJuradoReadModel> ejecutar(Void entrada) {
        return consultarCriteriosItemCualitativoJuradoUseCase.ejecutar(entrada);
    }
}
