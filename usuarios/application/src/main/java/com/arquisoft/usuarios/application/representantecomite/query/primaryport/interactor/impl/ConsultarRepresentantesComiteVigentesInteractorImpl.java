package com.arquisoft.usuarios.application.representantecomite.query.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.representantecomite.query.primaryport.interactor.ConsultarRepresentantesComiteVigentesInteractor;
import com.arquisoft.usuarios.application.representantecomite.query.primaryport.mapper.ConsultarRepresentantesComiteVigentesMapper;
import com.arquisoft.usuarios.application.representantecomite.query.readmodel.RepresentanteComiteVigenteReadModel;
import com.arquisoft.usuarios.application.representantecomite.query.usecase.ConsultarRepresentantesComiteVigentesUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarRepresentantesComiteVigentesInteractorImpl implements ConsultarRepresentantesComiteVigentesInteractor {

    private final ConsultarRepresentantesComiteVigentesUseCase consultarRepresentantesComiteVigentesUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "usuariosTransactionManager")
    public PaginatedResult<RepresentanteComiteVigenteReadModel> ejecutar(ConsultaCriteriaQuery entrada) {
        var criteria = ConsultarRepresentantesComiteVigentesMapper.toCriteria(entrada);
        return consultarRepresentantesComiteVigentesUseCase.ejecutar(criteria);
    }
}
