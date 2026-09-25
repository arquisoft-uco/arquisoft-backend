package com.arquisoft.usuarios.application.asesorficha.query.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.asesorficha.query.primaryport.interactor.ConsultarAsesoresFichaAdministradorInteractor;
import com.arquisoft.usuarios.application.asesorficha.query.primaryport.mapper.ConsultarAsesoresFichaAdministradorMapper;
import com.arquisoft.usuarios.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.usuarios.application.asesorficha.query.usecase.ConsultarAsesoresFichaAdministradorUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarAsesoresFichaAdministradorInteractorImpl implements ConsultarAsesoresFichaAdministradorInteractor {

    private final ConsultarAsesoresFichaAdministradorUseCase consultarAsesoresFichaAdministradorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "usuariosTransactionManager")
    public PaginatedResult<AsesorFichaReadModel> ejecutar(ConsultaCriteriaQuery entrada) {
        var criteria = ConsultarAsesoresFichaAdministradorMapper.toCriteria(entrada);
        return consultarAsesoresFichaAdministradorUseCase.ejecutar(criteria);
    }
}
