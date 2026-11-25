package com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.ConsultarFichasPerfilRepresentanteInteractor;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.mapper.ConsultarFichasPerfilRepresentanteMapper;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilRepresentanteQuery;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilRepresentanteReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.usecase.ConsultarFichasPerfilRepresentanteUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarFichasPerfilRepresentanteInteractorImpl
        implements ConsultarFichasPerfilRepresentanteInteractor {

    private final ConsultarFichasPerfilRepresentanteUseCase consultarFichasPerfilRepresentanteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public PaginatedResult<FichaPerfilRepresentanteReadModel> ejecutar(ConsultarFichasPerfilRepresentanteQuery entrada) {
        var criteria = ConsultarFichasPerfilRepresentanteMapper.toCriteria(entrada);
        return consultarFichasPerfilRepresentanteUseCase.ejecutar(criteria);
    }
}
