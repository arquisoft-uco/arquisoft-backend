package com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.primaryport.interactor.ConsultarItemsCuantitativosJuradoInteractor;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.readmodel.ItemCuantitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.usecase.ConsultarItemsCuantitativosJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarItemsCuantitativosJuradoInteractorImpl
        implements ConsultarItemsCuantitativosJuradoInteractor {

    private final ConsultarItemsCuantitativosJuradoUseCase consultarItemsCuantitativosJuradoUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public List<ItemCuantitativoJuradoReadModel> ejecutar() {
        return consultarItemsCuantitativosJuradoUseCase.ejecutar();
    }
}
