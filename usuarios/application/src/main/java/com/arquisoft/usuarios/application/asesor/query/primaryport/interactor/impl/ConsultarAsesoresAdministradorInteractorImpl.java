package com.arquisoft.usuarios.application.asesor.query.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.asesor.query.primaryport.interactor.ConsultarAsesoresAdministradorInteractor;
import com.arquisoft.usuarios.application.asesor.query.primaryport.mapper.ConsultarAsesoresAdministradorMapper;
import com.arquisoft.usuarios.application.asesor.query.readmodel.AsesorReadModel;
import com.arquisoft.usuarios.application.asesor.query.usecase.ConsultarAsesoresAdministradorUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarAsesoresAdministradorInteractorImpl implements ConsultarAsesoresAdministradorInteractor {

    private final ConsultarAsesoresAdministradorUseCase consultarAsesoresAdministradorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "usuariosTransactionManager")
    public PaginatedResult<AsesorReadModel> ejecutar(ConsultaCriteriaQuery entrada) {
        var criteria = ConsultarAsesoresAdministradorMapper.toCriteria(entrada);
        return consultarAsesoresAdministradorUseCase.ejecutar(criteria);
    }
}
