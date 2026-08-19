package com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.impl;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.ConsultarFichasPerfilInteractor;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.usecase.ConsultarFichasPerfilUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarFichasPerfilInteractorImpl implements ConsultarFichasPerfilInteractor {

    private final ConsultarFichasPerfilUseCase consultarFichasPerfilUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "fichasTransactionManager")
    public PaginatedResult<FichaPerfilReadModel> ejecutar(FichaPerfilCriteria entrada) {
        return consultarFichasPerfilUseCase.ejecutar(entrada);
    }
}
