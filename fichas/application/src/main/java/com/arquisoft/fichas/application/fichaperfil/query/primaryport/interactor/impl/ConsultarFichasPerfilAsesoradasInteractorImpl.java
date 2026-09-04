package com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.ConsultarFichasPerfilAsesoradasInteractor;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.mapper.ConsultarFichasPerfilAsesoradasMapper;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilAsesoradasQuery;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.usecase.ConsultarFichasPerfilAsesoradasUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarFichasPerfilAsesoradasInteractorImpl implements ConsultarFichasPerfilAsesoradasInteractor {

    private final ConsultarFichasPerfilAsesoradasUseCase consultarFichasPerfilAsesoradasUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public PaginatedResult<FichaPerfilReadModel> ejecutar(ConsultarFichasPerfilAsesoradasQuery entrada) {
        var criteria = ConsultarFichasPerfilAsesoradasMapper.toCriteria(entrada);
        return consultarFichasPerfilAsesoradasUseCase.ejecutar(criteria);
    }
}
