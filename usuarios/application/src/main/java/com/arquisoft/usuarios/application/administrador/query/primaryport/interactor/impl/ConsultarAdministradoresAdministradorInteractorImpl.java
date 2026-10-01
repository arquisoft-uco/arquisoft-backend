package com.arquisoft.usuarios.application.administrador.query.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.administrador.query.primaryport.interactor.ConsultarAdministradoresAdministradorInteractor;
import com.arquisoft.usuarios.application.administrador.query.primaryport.mapper.ConsultarAdministradoresAdministradorMapper;
import com.arquisoft.usuarios.application.administrador.query.readmodel.AdministradorReadModel;
import com.arquisoft.usuarios.application.administrador.query.usecase.ConsultarAdministradoresAdministradorUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarAdministradoresAdministradorInteractorImpl implements ConsultarAdministradoresAdministradorInteractor {

    private final ConsultarAdministradoresAdministradorUseCase consultarAdministradoresAdministradorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "usuariosTransactionManager")
    public PaginatedResult<AdministradorReadModel> ejecutar(ConsultaCriteriaQuery entrada) {
        var criteria = ConsultarAdministradoresAdministradorMapper.toCriteria(entrada);
        return consultarAdministradoresAdministradorUseCase.ejecutar(criteria);
    }
}
