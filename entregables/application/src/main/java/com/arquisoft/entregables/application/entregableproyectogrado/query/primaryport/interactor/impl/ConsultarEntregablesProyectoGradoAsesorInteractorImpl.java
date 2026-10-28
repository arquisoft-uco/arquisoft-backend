package com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.interactor.ConsultarEntregablesProyectoGradoAsesorInteractor;
import com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.mapper.ConsultarEntregablesProyectoGradoAsesorMapper;
import com.arquisoft.entregables.application.entregableproyectogrado.query.readmodel.EntregableProyectoGradoReadModel;
import com.arquisoft.entregables.application.entregableproyectogrado.query.usecase.ConsultarEntregablesProyectoGradoAsesorUseCase;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarEntregablesProyectoGradoAsesorInteractorImpl
        implements ConsultarEntregablesProyectoGradoAsesorInteractor {

    private final ConsultarEntregablesProyectoGradoAsesorUseCase consultarEntregablesProyectoGradoAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "entregablesTransactionManager")
    public PaginatedResult<EntregableProyectoGradoReadModel> ejecutar(ConsultaCriteriaQuery entrada) {
        var criteria = ConsultarEntregablesProyectoGradoAsesorMapper.toCriteria(entrada);
        return consultarEntregablesProyectoGradoAsesorUseCase.ejecutar(criteria);
    }
}
