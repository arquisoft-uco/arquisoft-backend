package com.arquisoft.usuarios.application.coordinador.query.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.coordinador.query.primaryport.interactor.ConsultarCoordinadoresVigentesInteractor;
import com.arquisoft.usuarios.application.coordinador.query.primaryport.mapper.ConsultarCoordinadoresVigentesMapper;
import com.arquisoft.usuarios.application.coordinador.query.readmodel.CoordinadorVigenteReadModel;
import com.arquisoft.usuarios.application.coordinador.query.usecase.ConsultarCoordinadoresVigentesUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarCoordinadoresVigentesInteractorImpl implements ConsultarCoordinadoresVigentesInteractor {

    private final ConsultarCoordinadoresVigentesUseCase consultarCoordinadoresVigentesUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "usuariosTransactionManager")
    public PaginatedResult<CoordinadorVigenteReadModel> ejecutar(ConsultaCriteriaQuery entrada) {
        var criteria = ConsultarCoordinadoresVigentesMapper.toCriteria(entrada);
        return consultarCoordinadoresVigentesUseCase.ejecutar(criteria);
    }
}
