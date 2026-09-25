package com.arquisoft.usuarios.application.estudiante.query.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.estudiante.query.primaryport.interactor.ConsultarEstudiantesAdministradorInteractor;
import com.arquisoft.usuarios.application.estudiante.query.primaryport.mapper.ConsultarEstudiantesAdministradorMapper;
import com.arquisoft.usuarios.application.estudiante.query.readmodel.EstudianteReadModel;
import com.arquisoft.usuarios.application.estudiante.query.usecase.ConsultarEstudiantesAdministradorUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarEstudiantesAdministradorInteractorImpl implements ConsultarEstudiantesAdministradorInteractor {

    private final ConsultarEstudiantesAdministradorUseCase consultarEstudiantesAdministradorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "usuariosTransactionManager")
    public PaginatedResult<EstudianteReadModel> ejecutar(ConsultaCriteriaQuery entrada) {
        var criteria = ConsultarEstudiantesAdministradorMapper.toCriteria(entrada);
        return consultarEstudiantesAdministradorUseCase.ejecutar(criteria);
    }
}
