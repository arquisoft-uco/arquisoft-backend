package com.arquisoft.mapas_ruta.application.tarea.query.primaryport.interactor;

import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.model.ConsultarTareasMapaRutaAsesorQuery;
import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarTareasMapaRutaAsesorInteractor
        extends Interactor<ConsultarTareasMapaRutaAsesorQuery, PaginatedResult<TareaReadModel>> {}
