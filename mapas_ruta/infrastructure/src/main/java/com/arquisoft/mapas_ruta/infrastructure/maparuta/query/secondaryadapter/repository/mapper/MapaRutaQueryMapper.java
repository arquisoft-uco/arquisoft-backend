package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.secondaryadapter.repository.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaReadModel;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.query.secondaryadapter.repository.MapaRutaJpaQueryEntity;

public final class MapaRutaQueryMapper {

    private MapaRutaQueryMapper() {}

    public static MapaRutaReadModel toReadModel(MapaRutaJpaQueryEntity entity) {
        return new MapaRutaReadModel(
                entity.getId(),
                entity.getProyectoGradoId(),
                entity.getTituloProyecto(),
                entity.getFechaInicio(),
                entity.getFechaFin());
    }
}
