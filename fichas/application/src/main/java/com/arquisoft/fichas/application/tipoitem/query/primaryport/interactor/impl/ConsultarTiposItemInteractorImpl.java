package com.arquisoft.fichas.application.tipoitem.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.tipoitem.query.primaryport.interactor.ConsultarTiposItemInteractor;
import com.arquisoft.fichas.application.tipoitem.query.readmodel.TipoItemReadModel;
import com.arquisoft.fichas.application.tipoitem.query.usecase.ConsultarTiposItemUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarTiposItemInteractorImpl implements ConsultarTiposItemInteractor {

    private final ConsultarTiposItemUseCase consultarTiposItemUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public List<TipoItemReadModel> ejecutar() {
        return consultarTiposItemUseCase.ejecutar();
    }
}
