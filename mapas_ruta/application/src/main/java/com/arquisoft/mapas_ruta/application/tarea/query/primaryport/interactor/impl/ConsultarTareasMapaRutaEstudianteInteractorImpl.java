package com.arquisoft.mapas_ruta.application.tarea.query.primaryport.interactor.impl;

import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.interactor.ConsultarTareasMapaRutaEstudianteInteractor;
import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.mapper.ConsultarTareasMapaRutaEstudianteMapper;
import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.model.ConsultarTareasMapaRutaEstudianteQuery;
import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;
import com.arquisoft.mapas_ruta.application.tarea.query.usecase.ConsultarTareasMapaRutaEstudianteUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarTareasMapaRutaEstudianteInteractorImpl implements ConsultarTareasMapaRutaEstudianteInteractor {

    private final ConsultarTareasMapaRutaEstudianteUseCase consultarTareasMapaRutaEstudianteUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "mapasRutaTransactionManager")
    public PaginatedResult<TareaReadModel> ejecutar(ConsultarTareasMapaRutaEstudianteQuery entrada) {
        var criteria = ConsultarTareasMapaRutaEstudianteMapper.toCriteria(entrada);
        return consultarTareasMapaRutaEstudianteUseCase.ejecutar(criteria);
    }
}
