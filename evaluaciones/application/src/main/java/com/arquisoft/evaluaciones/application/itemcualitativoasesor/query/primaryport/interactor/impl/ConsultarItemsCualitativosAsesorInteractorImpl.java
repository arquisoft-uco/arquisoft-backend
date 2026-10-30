package com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.primaryport.interactor.ConsultarItemsCualitativosAsesorInteractor;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.readmodel.ItemCualitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.usecase.ConsultarItemsCualitativosAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarItemsCualitativosAsesorInteractorImpl
        implements ConsultarItemsCualitativosAsesorInteractor {

    private final ConsultarItemsCualitativosAsesorUseCase consultarItemsCualitativosAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public List<ItemCualitativoAsesorReadModel> ejecutar() {
        return consultarItemsCualitativosAsesorUseCase.ejecutar();
    }
}
