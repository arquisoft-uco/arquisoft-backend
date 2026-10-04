package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.interactor.ConsultarEstadosFichaPerfilRepresentanteInteractor;
import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.mapper.ConsultarEstadosFichaPerfilRepresentanteMapper;
import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilRepresentanteQuery;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.usecase.ConsultarEstadosFichaPerfilRepresentanteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosFichaPerfilRepresentanteInteractorImpl implements ConsultarEstadosFichaPerfilRepresentanteInteractor {

    private final ConsultarEstadosFichaPerfilRepresentanteUseCase consultarEstadosFichaPerfilRepresentanteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public List<EstadoFichaPerfilReadModel> ejecutar(ConsultarEstadosFichaPerfilRepresentanteQuery entrada) {
        var criteria = ConsultarEstadosFichaPerfilRepresentanteMapper.toCriteria(entrada);
        return consultarEstadosFichaPerfilRepresentanteUseCase.ejecutar(criteria);
    }
}
