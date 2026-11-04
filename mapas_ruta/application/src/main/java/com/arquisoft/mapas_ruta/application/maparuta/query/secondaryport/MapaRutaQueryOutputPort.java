package com.arquisoft.mapas_ruta.application.maparuta.query.secondaryport;

import com.arquisoft.mapas_ruta.application.maparuta.query.criteria.MapaRutaCriteria;
import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaReadModel;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface MapaRutaQueryOutputPort {

    PaginatedResult<MapaRutaReadModel> consultarTodos(MapaRutaCriteria criteria);
}
