package com.arquisoft.mapas_ruta.application.maparuta.query.usecase;

import com.arquisoft.mapas_ruta.application.maparuta.query.criteria.MapaRutaCriteria;
import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.usecase.UseCase;

public interface ConsultarMapasRutaCoordinadorUseCase
        extends UseCase<MapaRutaCriteria, PaginatedResult<MapaRutaReadModel>> {}
