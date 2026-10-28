package com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.interactor.ConsultarEntregablesProyectoGradoJuradoInteractor;
import com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.mapper.ConsultarEntregablesProyectoGradoJuradoMapper;
import com.arquisoft.entregables.application.entregableproyectogrado.query.readmodel.EntregableProyectoGradoReadModel;
import com.arquisoft.entregables.application.entregableproyectogrado.query.usecase.ConsultarEntregablesProyectoGradoJuradoUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarEntregablesProyectoGradoJuradoInteractorImpl
        implements ConsultarEntregablesProyectoGradoJuradoInteractor {

    private final ConsultarEntregablesProyectoGradoJuradoUseCase consultarEntregablesProyectoGradoJuradoUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "entregablesTransactionManager")
    public PaginatedResult<EntregableProyectoGradoReadModel> ejecutar(ConsultaCriteriaQuery entrada) {
        var criteria = ConsultarEntregablesProyectoGradoJuradoMapper.toCriteria(entrada);
        return consultarEntregablesProyectoGradoJuradoUseCase.ejecutar(criteria);
    }
}
