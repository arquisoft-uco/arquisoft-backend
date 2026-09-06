package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.interactor.ConsultarItemsFichaPerfilAsesorInteractor;
import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.mapper.ConsultarItemsFichaPerfilAsesorMapper;
import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilAsesorQuery;
import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.fichas.application.itemfichaperfil.query.usecase.ConsultarItemsFichaPerfilAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarItemsFichaPerfilAsesorInteractorImpl implements ConsultarItemsFichaPerfilAsesorInteractor {

    private final ConsultarItemsFichaPerfilAsesorUseCase consultarItemsFichaPerfilAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public List<ItemFichaPerfilReadModel> ejecutar(ConsultarItemsFichaPerfilAsesorQuery entrada) {
        var criteria = ConsultarItemsFichaPerfilAsesorMapper.toCriteria(entrada);
        return consultarItemsFichaPerfilAsesorUseCase.ejecutar(criteria);
    }
}
