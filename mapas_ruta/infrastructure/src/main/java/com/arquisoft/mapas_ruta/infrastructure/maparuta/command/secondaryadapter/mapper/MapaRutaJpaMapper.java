package com.arquisoft.mapas_ruta.infrastructure.maparuta.command.secondaryadapter.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.entity.MapaRutaEntity;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.command.secondaryadapter.entity.MapaRutaJpaEntity;

public final class MapaRutaJpaMapper {

    private MapaRutaJpaMapper() {}

    public static MapaRutaEntity toEntity(MapaRutaJpaEntity jpaEntity) {
        return new MapaRutaEntity(
                jpaEntity.getId(), jpaEntity.getProyectoGradoId(), jpaEntity.getFechaInicio(), jpaEntity.getFechaFin());
    }

    public static MapaRutaJpaEntity toJpaEntity(MapaRutaEntity entity) {
        return MapaRutaJpaEntity.builder()
                .id(entity.id())
                .proyectoGradoId(entity.proyectoGrado())
                .fechaInicio(entity.fechaInicio())
                .fechaFin(entity.fechaFin())
                .build();
    }
}
