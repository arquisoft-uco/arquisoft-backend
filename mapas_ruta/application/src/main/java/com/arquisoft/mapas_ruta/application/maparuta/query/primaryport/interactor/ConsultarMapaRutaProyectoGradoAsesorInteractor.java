package com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.interactor;

import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model.ConsultarMapaRutaProyectoGradoAsesorQuery;
import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaEstudianteReadModel;
import com.arquisoft.shared.interactor.Interactor;

import java.util.Optional;

public interface ConsultarMapaRutaProyectoGradoAsesorInteractor
        extends Interactor<ConsultarMapaRutaProyectoGradoAsesorQuery, Optional<MapaRutaEstudianteReadModel>> {
}
