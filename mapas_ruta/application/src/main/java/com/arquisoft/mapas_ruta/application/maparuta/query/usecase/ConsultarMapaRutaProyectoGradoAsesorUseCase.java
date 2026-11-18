package com.arquisoft.mapas_ruta.application.maparuta.query.usecase;

import com.arquisoft.mapas_ruta.application.maparuta.query.criteria.MapaRutaProyectoGradoCriteria;
import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaEstudianteReadModel;
import com.arquisoft.shared.usecase.UseCase;

import java.util.Optional;

public interface ConsultarMapaRutaProyectoGradoAsesorUseCase
        extends UseCase<MapaRutaProyectoGradoCriteria, Optional<MapaRutaEstudianteReadModel>> {
}
