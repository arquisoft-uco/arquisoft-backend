package com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.interactor;

import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model.ConsultarMapasRutaCoordinadorQuery;
import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaReadModel;
import com.arquisoft.shared.interactor.Interactor;
import com.arquisoft.shared.query.pagination.PaginatedResult;

public interface ConsultarMapasRutaCoordinadorInteractor
        extends Interactor<ConsultarMapasRutaCoordinadorQuery, PaginatedResult<MapaRutaReadModel>> {}
