package com.arquisoft.biblioteca.application.entregableproyectogrado.query.primaryport.interactor.impl;

import com.arquisoft.biblioteca.application.entregableproyectogrado.query.primaryport.interactor.ConsultarEntregablesDisponiblesInteractor;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.primaryport.mapper.ConsultarEntregablesDisponiblesMapper;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.primaryport.model.ConsultarEntregablesDisponiblesQuery;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.readmodel.EntregableDisponibleReadModel;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.usecase.ConsultarEntregablesDisponiblesUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarEntregablesDisponiblesInteractorImpl implements ConsultarEntregablesDisponiblesInteractor {

    private final ConsultarEntregablesDisponiblesUseCase consultarEntregablesDisponiblesUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "bibliotecaTransactionManager")
    public PaginatedResult<EntregableDisponibleReadModel> ejecutar(ConsultarEntregablesDisponiblesQuery entrada) {
        var criteria = ConsultarEntregablesDisponiblesMapper.toCriteria(entrada);
        return consultarEntregablesDisponiblesUseCase.ejecutar(criteria);
    }
}
