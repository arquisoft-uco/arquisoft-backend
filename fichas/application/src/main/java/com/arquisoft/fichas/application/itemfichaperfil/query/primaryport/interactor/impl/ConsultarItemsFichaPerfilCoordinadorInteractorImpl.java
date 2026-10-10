package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.interactor.ConsultarItemsFichaPerfilCoordinadorInteractor;
import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.mapper.ConsultarItemsFichaPerfilCoordinadorMapper;
import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilCoordinadorQuery;
import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.fichas.application.itemfichaperfil.query.usecase.ConsultarItemsFichaPerfilCoordinadorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarItemsFichaPerfilCoordinadorInteractorImpl implements ConsultarItemsFichaPerfilCoordinadorInteractor {

    private final ConsultarItemsFichaPerfilCoordinadorUseCase consultarItemsFichaPerfilCoordinadorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public List<ItemFichaPerfilReadModel> ejecutar(ConsultarItemsFichaPerfilCoordinadorQuery entrada) {
        var criteria = ConsultarItemsFichaPerfilCoordinadorMapper.toCriteria(entrada);
        return consultarItemsFichaPerfilCoordinadorUseCase.ejecutar(criteria);
    }
}
