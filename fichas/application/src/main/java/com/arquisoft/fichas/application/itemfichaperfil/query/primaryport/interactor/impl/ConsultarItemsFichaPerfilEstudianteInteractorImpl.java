package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.interactor.ConsultarItemsFichaPerfilEstudianteInteractor;
import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.mapper.ConsultarItemsFichaPerfilEstudianteMapper;
import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilEstudianteQuery;
import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.fichas.application.itemfichaperfil.query.usecase.ConsultarItemsFichaPerfilEstudianteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarItemsFichaPerfilEstudianteInteractorImpl implements ConsultarItemsFichaPerfilEstudianteInteractor {

    private final ConsultarItemsFichaPerfilEstudianteUseCase consultarItemsFichaPerfilEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public List<ItemFichaPerfilReadModel> ejecutar(ConsultarItemsFichaPerfilEstudianteQuery entrada) {
        var criteria = ConsultarItemsFichaPerfilEstudianteMapper.toCriteria(entrada);
        return consultarItemsFichaPerfilEstudianteUseCase.ejecutar(criteria);
    }
}
