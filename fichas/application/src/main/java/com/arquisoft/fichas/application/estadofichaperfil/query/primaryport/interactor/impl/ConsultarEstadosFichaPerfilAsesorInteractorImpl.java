package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.interactor.ConsultarEstadosFichaPerfilAsesorInteractor;
import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.mapper.ConsultarEstadosFichaPerfilAsesorMapper;
import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilAsesorQuery;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilAsesorReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.usecase.ConsultarEstadosFichaPerfilAsesorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosFichaPerfilAsesorInteractorImpl implements ConsultarEstadosFichaPerfilAsesorInteractor {

    private final ConsultarEstadosFichaPerfilAsesorUseCase consultarEstadosFichaPerfilAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public PaginatedResult<EstadoFichaPerfilAsesorReadModel> ejecutar(ConsultarEstadosFichaPerfilAsesorQuery entrada) {
        var criteria = ConsultarEstadosFichaPerfilAsesorMapper.toCriteria(entrada);
        return consultarEstadosFichaPerfilAsesorUseCase.ejecutar(criteria);
    }
}
