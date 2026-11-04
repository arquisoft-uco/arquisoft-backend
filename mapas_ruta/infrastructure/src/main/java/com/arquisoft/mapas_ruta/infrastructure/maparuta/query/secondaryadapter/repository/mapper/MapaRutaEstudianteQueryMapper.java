package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.secondaryadapter.repository.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaEstudianteReadModel;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.query.secondaryadapter.repository.MapaRutaEstudianteJpaQueryEntity;

public final class MapaRutaEstudianteQueryMapper {

    private MapaRutaEstudianteQueryMapper() {}

    public static MapaRutaEstudianteReadModel toReadModel(MapaRutaEstudianteJpaQueryEntity entity) {
        return new MapaRutaEstudianteReadModel(
                entity.getId(),
                entity.getProyectoGradoId(),
                entity.getTituloProyecto(),
                entity.getFechaInicio(),
                entity.getFechaFin());
    }
}
