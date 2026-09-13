package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.interactor.ConsultarEstadosFichaPerfilEstudianteInteractor;
import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.mapper.ConsultarEstadosFichaPerfilEstudianteMapper;
import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilEstudianteQuery;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.usecase.ConsultarEstadosFichaPerfilEstudianteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosFichaPerfilEstudianteInteractorImpl implements ConsultarEstadosFichaPerfilEstudianteInteractor {

    private final ConsultarEstadosFichaPerfilEstudianteUseCase consultarEstadosFichaPerfilEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public List<EstadoFichaPerfilReadModel> ejecutar(ConsultarEstadosFichaPerfilEstudianteQuery entrada) {
        var criteria = ConsultarEstadosFichaPerfilEstudianteMapper.toCriteria(entrada);
        return consultarEstadosFichaPerfilEstudianteUseCase.ejecutar(criteria);
    }
}
