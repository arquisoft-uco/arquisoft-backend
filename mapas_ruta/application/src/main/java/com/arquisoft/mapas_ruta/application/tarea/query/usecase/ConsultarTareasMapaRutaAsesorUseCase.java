package com.arquisoft.mapas_ruta.application.tarea.query.usecase;

import com.arquisoft.mapas_ruta.application.tarea.query.criteria.TareaMapaRutaCriteria;
import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarTareasMapaRutaAsesorUseCase
        extends UseCase<TareaMapaRutaCriteria, PaginatedResult<TareaReadModel>> {}
