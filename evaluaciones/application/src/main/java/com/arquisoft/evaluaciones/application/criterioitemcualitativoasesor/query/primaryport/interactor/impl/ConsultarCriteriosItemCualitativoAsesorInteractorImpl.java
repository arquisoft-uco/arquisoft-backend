package com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.primaryport.interactor.ConsultarCriteriosItemCualitativoAsesorInteractor;
import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.readmodel.CriterioItemCualitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.usecase.ConsultarCriteriosItemCualitativoAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarCriteriosItemCualitativoAsesorInteractorImpl
        implements ConsultarCriteriosItemCualitativoAsesorInteractor {

    private final ConsultarCriteriosItemCualitativoAsesorUseCase consultarCriteriosItemCualitativoAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public List<CriterioItemCualitativoAsesorReadModel> ejecutar() {
        return consultarCriteriosItemCualitativoAsesorUseCase.ejecutar();
    }
}
