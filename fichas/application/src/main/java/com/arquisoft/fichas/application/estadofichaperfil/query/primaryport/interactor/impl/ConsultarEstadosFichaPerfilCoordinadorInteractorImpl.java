package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.interactor.ConsultarEstadosFichaPerfilCoordinadorInteractor;
import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.mapper.ConsultarEstadosFichaPerfilCoordinadorMapper;
import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilCoordinadorQuery;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.usecase.ConsultarEstadosFichaPerfilCoordinadorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosFichaPerfilCoordinadorInteractorImpl implements ConsultarEstadosFichaPerfilCoordinadorInteractor {

    private final ConsultarEstadosFichaPerfilCoordinadorUseCase consultarEstadosFichaPerfilCoordinadorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public List<EstadoFichaPerfilReadModel> ejecutar(ConsultarEstadosFichaPerfilCoordinadorQuery entrada) {
        var criteria = ConsultarEstadosFichaPerfilCoordinadorMapper.toCriteria(entrada);
        return consultarEstadosFichaPerfilCoordinadorUseCase.ejecutar(criteria);
    }
}
