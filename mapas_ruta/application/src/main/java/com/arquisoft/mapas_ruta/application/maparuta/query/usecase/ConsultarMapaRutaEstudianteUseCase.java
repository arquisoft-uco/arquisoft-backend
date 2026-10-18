package com.arquisoft.mapas_ruta.application.maparuta.query.usecase;

import com.arquisoft.mapas_ruta.application.maparuta.query.criteria.MapaRutaEstudianteCriteria;
import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaEstudianteReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.Optional;

public interface ConsultarMapaRutaEstudianteUseCase
        extends UseCase<MapaRutaEstudianteCriteria, Optional<MapaRutaEstudianteReadModel>> {
}
