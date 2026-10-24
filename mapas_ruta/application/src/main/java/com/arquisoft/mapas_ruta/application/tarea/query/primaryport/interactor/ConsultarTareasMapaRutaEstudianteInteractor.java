package com.arquisoft.mapas_ruta.application.tarea.query.primaryport.interactor;

import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.model.ConsultarTareasMapaRutaEstudianteQuery;
import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarTareasMapaRutaEstudianteInteractor
        extends Interactor<ConsultarTareasMapaRutaEstudianteQuery, PaginatedResult<TareaReadModel>> {}
