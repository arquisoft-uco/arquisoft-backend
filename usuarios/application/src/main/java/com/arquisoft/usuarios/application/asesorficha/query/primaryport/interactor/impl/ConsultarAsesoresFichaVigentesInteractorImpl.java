package com.arquisoft.usuarios.application.asesorficha.query.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.asesorficha.query.primaryport.interactor.ConsultarAsesoresFichaVigentesInteractor;
import com.arquisoft.usuarios.application.asesorficha.query.primaryport.mapper.ConsultarAsesoresFichaVigentesMapper;
import com.arquisoft.usuarios.application.asesorficha.query.readmodel.AsesorFichaVigenteReadModel;
import com.arquisoft.usuarios.application.asesorficha.query.usecase.ConsultarAsesoresFichaVigentesUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarAsesoresFichaVigentesInteractorImpl implements ConsultarAsesoresFichaVigentesInteractor {

    private final ConsultarAsesoresFichaVigentesUseCase consultarAsesoresFichaVigentesUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "usuariosTransactionManager")
    public PaginatedResult<AsesorFichaVigenteReadModel> ejecutar(ConsultaCriteriaQuery entrada) {
        var criteria = ConsultarAsesoresFichaVigentesMapper.toCriteria(entrada);
        return consultarAsesoresFichaVigentesUseCase.ejecutar(criteria);
    }
}
