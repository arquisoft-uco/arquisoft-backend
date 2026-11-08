package com.arquisoft.mapas_ruta.application.tarea.query.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.interactor.ConsultarTareasMapaRutaAsesorInteractor;
import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.mapper.ConsultarTareasMapaRutaAsesorMapper;
import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.model.ConsultarTareasMapaRutaAsesorQuery;
import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;
import com.arquisoft.mapas_ruta.application.tarea.query.usecase.ConsultarTareasMapaRutaAsesorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarTareasMapaRutaAsesorInteractorImpl implements ConsultarTareasMapaRutaAsesorInteractor {

    private final ConsultarTareasMapaRutaAsesorUseCase consultarTareasMapaRutaAsesorUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "mapasRutaTransactionManager")
    public PaginatedResult<TareaReadModel> ejecutar(ConsultarTareasMapaRutaAsesorQuery entrada) {
        var criteria = ConsultarTareasMapaRutaAsesorMapper.toCriteria(entrada);
        return consultarTareasMapaRutaAsesorUseCase.ejecutar(criteria);
    }
}
