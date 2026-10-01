package com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.ConsultarFichasPerfilCoordinadorInteractor;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.mapper.ConsultarFichasPerfilCoordinadorMapper;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.usecase.ConsultarFichasPerfilCoordinadorUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarFichasPerfilCoordinadorInteractorImpl implements ConsultarFichasPerfilCoordinadorInteractor {

    private final ConsultarFichasPerfilCoordinadorUseCase consultarFichasPerfilCoordinadorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public PaginatedResult<FichaPerfilReadModel> ejecutar(ConsultaCriteriaQuery entrada) {
        var criteria = ConsultarFichasPerfilCoordinadorMapper.toCriteria(entrada);
        return consultarFichasPerfilCoordinadorUseCase.ejecutar(criteria);
    }
}
