package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.interactor.ConsultarItemsFichaPerfilRepresentanteInteractor;
import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.mapper.ConsultarItemsFichaPerfilRepresentanteMapper;
import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilRepresentanteQuery;
import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.fichas.application.itemfichaperfil.query.usecase.ConsultarItemsFichaPerfilRepresentanteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarItemsFichaPerfilRepresentanteInteractorImpl implements ConsultarItemsFichaPerfilRepresentanteInteractor {

    private final ConsultarItemsFichaPerfilRepresentanteUseCase consultarItemsFichaPerfilRepresentanteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public List<ItemFichaPerfilReadModel> ejecutar(ConsultarItemsFichaPerfilRepresentanteQuery entrada) {
        var criteria = ConsultarItemsFichaPerfilRepresentanteMapper.toCriteria(entrada);
        return consultarItemsFichaPerfilRepresentanteUseCase.ejecutar(criteria);
    }
}
