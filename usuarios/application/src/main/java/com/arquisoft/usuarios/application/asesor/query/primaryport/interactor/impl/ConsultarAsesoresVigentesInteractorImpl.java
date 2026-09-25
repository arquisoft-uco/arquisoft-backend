package com.arquisoft.usuarios.application.asesor.query.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.asesor.query.primaryport.interactor.ConsultarAsesoresVigentesInteractor;
import com.arquisoft.usuarios.application.asesor.query.primaryport.mapper.ConsultarAsesoresVigentesMapper;
import com.arquisoft.usuarios.application.asesor.query.readmodel.AsesorVigenteReadModel;
import com.arquisoft.usuarios.application.asesor.query.usecase.ConsultarAsesoresVigentesUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarAsesoresVigentesInteractorImpl implements ConsultarAsesoresVigentesInteractor {

    private final ConsultarAsesoresVigentesUseCase consultarAsesoresVigentesUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "usuariosTransactionManager")
    public PaginatedResult<AsesorVigenteReadModel> ejecutar(ConsultaCriteriaQuery entrada) {
        var criteria = ConsultarAsesoresVigentesMapper.toCriteria(entrada);
        return consultarAsesoresVigentesUseCase.ejecutar(criteria);
    }
}
