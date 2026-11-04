package com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.interactor.ConsultarEntregablesProyectoGradoEstudianteInteractor;
import com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.mapper.ConsultarEntregablesProyectoGradoEstudianteMapper;
import com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.model.ConsultarEntregablesProyectoGradoEstudianteQuery;
import com.arquisoft.entregables.application.entregableproyectogrado.query.readmodel.EntregableProyectoGradoReadModel;
import com.arquisoft.entregables.application.entregableproyectogrado.query.usecase.ConsultarEntregablesProyectoGradoEstudianteUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarEntregablesProyectoGradoEstudianteInteractorImpl
        implements ConsultarEntregablesProyectoGradoEstudianteInteractor {

    private final ConsultarEntregablesProyectoGradoEstudianteUseCase consultarEntregablesProyectoGradoEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "entregablesTransactionManager")
    public PaginatedResult<EntregableProyectoGradoReadModel> ejecutar(
            ConsultarEntregablesProyectoGradoEstudianteQuery entrada) {
        var criteria = ConsultarEntregablesProyectoGradoEstudianteMapper.toCriteria(entrada);
        return consultarEntregablesProyectoGradoEstudianteUseCase.ejecutar(criteria);
    }
}
