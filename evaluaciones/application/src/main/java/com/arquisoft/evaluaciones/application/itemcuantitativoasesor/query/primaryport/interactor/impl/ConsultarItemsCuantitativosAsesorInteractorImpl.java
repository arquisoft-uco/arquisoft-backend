package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.primaryport.interactor.ConsultarItemsCuantitativosAsesorInteractor;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.readmodel.ItemCuantitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.usecase.ConsultarItemsCuantitativosAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarItemsCuantitativosAsesorInteractorImpl
        implements ConsultarItemsCuantitativosAsesorInteractor {

    private final ConsultarItemsCuantitativosAsesorUseCase consultarItemsCuantitativosAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public List<ItemCuantitativoAsesorReadModel> ejecutar() {
        return consultarItemsCuantitativosAsesorUseCase.ejecutar();
    }
}
