package com.arquisoft.usuarios.application.coordinador.query.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.coordinador.query.primaryport.interactor.ConsultarCoordinadoresAdministradorInteractor;
import com.arquisoft.usuarios.application.coordinador.query.primaryport.mapper.ConsultarCoordinadoresAdministradorMapper;
import com.arquisoft.usuarios.application.coordinador.query.readmodel.CoordinadorReadModel;
import com.arquisoft.usuarios.application.coordinador.query.usecase.ConsultarCoordinadoresAdministradorUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarCoordinadoresAdministradorInteractorImpl implements ConsultarCoordinadoresAdministradorInteractor {

    private final ConsultarCoordinadoresAdministradorUseCase consultarCoordinadoresAdministradorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "usuariosTransactionManager")
    public PaginatedResult<CoordinadorReadModel> ejecutar(ConsultaCriteriaQuery entrada) {
        var criteria = ConsultarCoordinadoresAdministradorMapper.toCriteria(entrada);
        return consultarCoordinadoresAdministradorUseCase.ejecutar(criteria);
    }
}
