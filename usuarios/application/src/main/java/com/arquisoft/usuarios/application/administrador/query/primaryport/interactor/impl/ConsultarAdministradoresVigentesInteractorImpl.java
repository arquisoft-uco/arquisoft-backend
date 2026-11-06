package com.arquisoft.usuarios.application.administrador.query.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.administrador.query.primaryport.interactor.ConsultarAdministradoresVigentesInteractor;
import com.arquisoft.usuarios.application.administrador.query.primaryport.mapper.ConsultarAdministradoresVigentesMapper;
import com.arquisoft.usuarios.application.administrador.query.readmodel.AdministradorVigenteReadModel;
import com.arquisoft.usuarios.application.administrador.query.usecase.ConsultarAdministradoresVigentesUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarAdministradoresVigentesInteractorImpl implements ConsultarAdministradoresVigentesInteractor {

    private final ConsultarAdministradoresVigentesUseCase consultarAdministradoresVigentesUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "usuariosTransactionManager")
    public PaginatedResult<AdministradorVigenteReadModel> ejecutar(ConsultaCriteriaQuery entrada) {
        var criteria = ConsultarAdministradoresVigentesMapper.toCriteria(entrada);
        return consultarAdministradoresVigentesUseCase.ejecutar(criteria);
    }
}
