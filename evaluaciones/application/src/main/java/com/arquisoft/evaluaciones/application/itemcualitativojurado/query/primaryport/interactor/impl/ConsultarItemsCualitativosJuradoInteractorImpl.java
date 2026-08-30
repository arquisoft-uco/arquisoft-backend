package com.arquisoft.evaluaciones.application.itemcualitativojurado.query.primaryport.interactor.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.primaryport.interactor.ConsultarItemsCualitativosJuradoInteractor;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.readmodel.ItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.usecase.ConsultarItemsCualitativosJuradoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarItemsCualitativosJuradoInteractorImpl implements ConsultarItemsCualitativosJuradoInteractor {

    private final ConsultarItemsCualitativosJuradoUseCase consultarItemsCualitativosJuradoUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "evaluacionesTransactionManager")
    public List<ItemCualitativoJuradoReadModel> ejecutar(Void entrada) {
        return consultarItemsCualitativosJuradoUseCase.ejecutar(entrada);
    }
}
