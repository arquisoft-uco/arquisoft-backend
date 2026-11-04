package com.arquisoft.usuarios.application.jurado.query.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.jurado.query.primaryport.interactor.ConsultarJuradosVigentesInteractor;
import com.arquisoft.usuarios.application.jurado.query.primaryport.mapper.ConsultarJuradosVigentesMapper;
import com.arquisoft.usuarios.application.jurado.query.readmodel.JuradoVigenteReadModel;
import com.arquisoft.usuarios.application.jurado.query.usecase.ConsultarJuradosVigentesUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarJuradosVigentesInteractorImpl implements ConsultarJuradosVigentesInteractor {

    private final ConsultarJuradosVigentesUseCase consultarJuradosVigentesUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "usuariosTransactionManager")
    public PaginatedResult<JuradoVigenteReadModel> ejecutar(ConsultaCriteriaQuery entrada) {
        var criteria = ConsultarJuradosVigentesMapper.toCriteria(entrada);
        return consultarJuradosVigentesUseCase.ejecutar(criteria);
    }
}
