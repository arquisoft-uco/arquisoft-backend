package com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.interactor;

import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model.ConsultarMapaRutaEstudianteQuery;
import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaEstudianteReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.Optional;

public interface ConsultarMapaRutaEstudianteInteractor
        extends Interactor<ConsultarMapaRutaEstudianteQuery, Optional<MapaRutaEstudianteReadModel>> {
}
