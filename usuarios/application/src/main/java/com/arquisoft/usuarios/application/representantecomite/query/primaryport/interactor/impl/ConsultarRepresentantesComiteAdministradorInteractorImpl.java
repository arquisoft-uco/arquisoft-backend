package com.arquisoft.usuarios.application.representantecomite.query.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.representantecomite.query.primaryport.interactor.ConsultarRepresentantesComiteAdministradorInteractor;
import com.arquisoft.usuarios.application.representantecomite.query.primaryport.mapper.ConsultarRepresentantesComiteAdministradorMapper;
import com.arquisoft.usuarios.application.representantecomite.query.readmodel.RepresentanteComiteReadModel;
import com.arquisoft.usuarios.application.representantecomite.query.usecase.ConsultarRepresentantesComiteAdministradorUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarRepresentantesComiteAdministradorInteractorImpl implements ConsultarRepresentantesComiteAdministradorInteractor {

    private final ConsultarRepresentantesComiteAdministradorUseCase consultarRepresentantesComiteAdministradorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "usuariosTransactionManager")
    public PaginatedResult<RepresentanteComiteReadModel> ejecutar(ConsultaCriteriaQuery entrada) {
        var criteria = ConsultarRepresentantesComiteAdministradorMapper.toCriteria(entrada);
        return consultarRepresentantesComiteAdministradorUseCase.ejecutar(criteria);
    }
}
