package com.arquisoft.usuarios.application.estudiante.query.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.estudiante.query.primaryport.interactor.ConsultarEstudiantesVigentesInteractor;
import com.arquisoft.usuarios.application.estudiante.query.primaryport.mapper.ConsultarEstudiantesVigentesMapper;
import com.arquisoft.usuarios.application.estudiante.query.readmodel.EstudianteVigenteReadModel;
import com.arquisoft.usuarios.application.estudiante.query.usecase.ConsultarEstudiantesVigentesUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarEstudiantesVigentesInteractorImpl implements ConsultarEstudiantesVigentesInteractor {

    private final ConsultarEstudiantesVigentesUseCase consultarEstudiantesVigentesUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "usuariosTransactionManager")
    public PaginatedResult<EstudianteVigenteReadModel> ejecutar(ConsultaCriteriaQuery entrada) {
        var criteria = ConsultarEstudiantesVigentesMapper.toCriteria(entrada);
        return consultarEstudiantesVigentesUseCase.ejecutar(criteria);
    }
}
